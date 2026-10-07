package com.thejaxx.jira.rest.plugin.utils;

import com.atlassian.jira.util.json.JSONArray;
import com.atlassian.jira.util.json.JSONObject;
import com.thejaxx.jira.rest.plugin.RestRow;
import com.thejaxx.jira.rest.plugin.config.ConfigEntity;
import com.thejaxx.jira.rest.plugin.config.ConfigUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.atlassian.core.util.ClassLoaderUtils;

public final class RestFetcher {

    private static final Logger logger = Logger.getLogger(RestFetcher.class);

    private static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String loadLocal(String name) throws IOException {

        InputStream stream = ClassLoaderUtils.getResourceAsStream(name, RestFetcher.class);
        if (stream == null) {
            URL resource = ClassLoaderUtils.getResource(name, RestFetcher.class);
            if (resource != null) {
                return FileCopyUtils.copyToString(new InputStreamReader(resource.openStream()));
            }
        } else {
            return FileCopyUtils.copyToString(new InputStreamReader(stream));
        }
        return "[]";
    }

    // Mesmo tempo do proxy-cache do Kong na rota /help/v3
    private static final long VALIDADE_CACHE_MS = 5 * 60 * 1000L;
    private static final long ESPERA_APOS_FALHA_MS = 30 * 1000L;
    private static final int TIMEOUT_CONEXAO_MS = 5 * 1000;
    private static final int TIMEOUT_LEITURA_MS = 15 * 1000;
    // Identifica o plugin nos logs do cloud (o padrão do JDK é só "Java/<versão>")
    private static final String USER_AGENT = "pmedico-jira-rest-customfield";

    private static final TimedCache<List<RestRow>> cache = new TimedCache<>(VALIDADE_CACHE_MS, ESPERA_APOS_FALHA_MS);

    /**
     * Lista de valores do campo. A busca no endpoint fica em cache por {@link #VALIDADE_CACHE_MS}:
     * este método é chamado uma vez por issue ao desenhar listas e gadgets.
     * O projectKey não muda a URL consultada, por isso não entra na chave do cache.
     */
    public static List<RestRow> doQuery(String projectKey) {
        try {
            ConfigEntity entity = ConfigUtils.get();
            String chave = entity.getUrl() + "|" + entity.getUsername() + "|" + entity.getPassword() + "|" + entity.getJsonKey();
            return cache.get(chave, () -> fetch(entity), Collections.<RestRow>emptyList());
        } catch (Throwable e) {
            logger.error("doQuery is in exception  " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private static List<RestRow> fetch(ConfigEntity entity) throws Exception {
        logger.info("doQuery fetching values from URL " + entity.getUrl());
        JSONArray array;
        if (entity.getUrl().startsWith("http")) {
            HttpRequest request = HttpRequest.get(entity.getUrl())
                    .userAgent(USER_AGENT)
                    .connectTimeout(TIMEOUT_CONEXAO_MS)
                    .readTimeout(TIMEOUT_LEITURA_MS);
            if (StringUtils.isNotEmpty(entity.getUsername())) {
                logger.info("doQuery is sending username  " + entity.getUsername());
                request.basic(entity.getUsername(), entity.getPassword());
            } else {
                logger.info("doQuery is not using any  username  ");
            }
            // Sem esta checagem, um 429/500 só aparecia como erro de parse do JSON
            if (!request.ok()) {
                throw new IOException("HTTP " + request.code() + " em " + entity.getUrl());
            }
            array = new JSONArray(request.body());
        } else {
            array = new JSONArray(loadLocal(entity.getUrl()));
        }
        logger.debug("Got array of " + array.length());
        if (array.length() > 0)
            logger.debug("First element is " + array.get(0).toString());
        List<RestRow> result = new ArrayList<RestRow>(array.length());
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            /**
             * for now, we support only keys
             */
            result.add(new RestRow(jsonObject.getString(entity.getJsonKey()), jsonObject.getString(entity.getJsonKey())));
        }
        // A mesma lista é devolvida a todas as threads até o cache vencer
        return Collections.unmodifiableList(result);
    }

}
