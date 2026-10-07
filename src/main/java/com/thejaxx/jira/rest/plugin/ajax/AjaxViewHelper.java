package com.thejaxx.jira.rest.plugin.ajax;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.util.I18nHelper;
import com.thejaxx.jira.rest.plugin.RestRowCache;
import com.thejaxx.jira.rest.plugin.config.ConfigUtils;
import com.thejaxx.jira.rest.plugin.RestRow;
import com.thejaxx.jira.rest.plugin.RestRowCachePurpose;
import com.thejaxx.jira.rest.plugin.RestRowFormatter;
import com.thejaxx.jira.rest.plugin.utils.RestFetcher;
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class generates the HTML for all things related to AJAX.
 */
public class AjaxViewHelper {
    // ------------------------------ FIELDS ------------------------------
    private static final Logger logger = Logger.getLogger(AjaxViewHelper.class);

    private static final String EOL = System.getProperty("line.separator");

    private static final String DIRNAME_PATH = "templates/";
    private static final String FILENAME = "autocomplete-input.vm";
    private RestRowCache rowCache = new RestRowCache();

//    private ActiveObjects ao;

    public AjaxViewHelper() {
    }

    public String getHtmlForView(String id, String projectKey, I18nHelper i18nHelper) {
//        refreshCacheIfNeeded(projectKey);
        System.out.println("id = " + id);
        logger.info(String.format("getHtmlForView . ID " + id + ", Project Key $", projectKey));
        RestRow restRow = rowCache.getRow(projectKey, id);
        String result;
        if (restRow != null) {
            result = RestRowFormatter.formatRestRow(restRow, ConfigUtils.get().getRenderingEditPattern(), i18nHelper);
        } else {
            result = "Unknown id: " + id;
        }
        return result;
    }


// -------------------------- PUBLIC METHODS --------------------------

    /**
     * Returns the html that will be used by the ajax auto-completer from scriptacolous.
     *
     * @param query      the string the user already typed. Can be null.
     * @param projectKey the JIRA key of the current project. Can be null.
     * @param i18nHelper helper for internationalization
     * @param purpose    one of {@link AjaxViewResultsPurpose}
     * @return HTML snippet
     */
    public String getHtmlForAjaxResults(String query, String projectKey, I18nHelper i18nHelper, AjaxViewResultsPurpose purpose) {
        RestRowCachePurpose restRowCachePurpose;
        String renderingPattern;
        String defaultRenderingPattern = "{2}, {1}";
        switch (purpose) {
            case EDIT:
                renderingPattern = ConfigUtils.get().getRenderingEditPattern();
                restRowCachePurpose = RestRowCachePurpose.EDIT;
                break;
            case SEARCH:
                renderingPattern = ConfigUtils.get().getRenderingSearchPattern();
                restRowCachePurpose = RestRowCachePurpose.SEARCH;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + purpose);
        }
        return getHtmlForAjaxResultsWithRenderingPattern(query, renderingPattern, projectKey, i18nHelper, restRowCachePurpose);
    }


    public String getAjaxHtmlForEdit(String customFieldId, String id, String baseurl, String projectKey, I18nHelper i18nHelper) {
        AjaxViewResultsPurpose ajaxViewResultsPurpose = AjaxViewResultsPurpose.EDIT;

        StringBuffer builder = buildJavascriptForAutocomplete(customFieldId, id, baseurl, projectKey, i18nHelper, ajaxViewResultsPurpose);
        return builder.toString();
    }


    public String getAjaxHtmlForSearch(String customFieldId, String id, String baseurl, String projectKey, I18nHelper i18nHelper) {
        return buildJavascriptForAutocomplete(customFieldId, id, baseurl, projectKey, i18nHelper, AjaxViewResultsPurpose.SEARCH).toString();
    }

// -------------------------- PRIVATE METHODS --------------------------

    private StringBuffer buildJavascriptForAutocomplete(String customFieldId, String id, String baseurl, String projectKey, I18nHelper i18nHelper, AjaxViewResultsPurpose ajaxViewResultsPurpose) {
        logger.debug("buildJavascriptForAutocomplete . Baseurl " + baseurl + " customFieldId " + customFieldId + " , projectKey = " + projectKey + ", ajaxViewResultsPurpose = " + ajaxViewResultsPurpose.name());

        StringBuffer builder = new StringBuffer();

        String valueString = getValueStringForAjaxInputField(id, i18nHelper, projectKey);
        valueString = valueString.replaceAll("\"", "'");

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("baseurl", baseurl);
        context.put("customFieldId", customFieldId);
//		context.put( "renderingEditWidth", m_parameters.getRenderingEditWidth() );
        context.put("projectKey", projectKey);
        context.put("ajaxPurpose", ajaxViewResultsPurpose.name());
        context.put("valueId", id); //The id in the database
        context.put("valueString", valueString); //The string to render to the user

        ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
        String baseUrl = applicationProperties.getString(APKeys.JIRA_BASEURL);
        String webworkEncoding = applicationProperties.getString(APKeys.JIRA_WEBWORK_ENCODING);
        String renderedText = ComponentAccessor.getVelocityManager().getEncodedBody(DIRNAME_PATH, FILENAME, baseUrl, webworkEncoding, context);
        builder.append(renderedText);

        return builder;
    }

    private String getValueStringForAjaxInputField(String id, I18nHelper i18nHelper, String projectKey) {
        logger.debug("getValueStringForAjaxInputField . id " + id + ",projectKey = " + projectKey);
        String valueString;
        if (id != null) {
            /**
             * @todo - change this yoni
             */
            RestRow restRow = new RestRow(id, id);//m_restRowCache.getDatabaseRowFromDatabase( id );
            valueString = RestRowFormatter.formatRestRow(restRow, ConfigUtils.get().getRenderingEditPattern(), i18nHelper);
            valueString = stripExtraInfo(valueString);
        } else {
            valueString = "";
        }
        return valueString;
    }

    private String getHtmlForAjaxResultsWithRenderingPattern(String query, String renderingPattern, String projectKey, I18nHelper i18nHelper,
                                                             RestRowCachePurpose restRowCachePurpose) {
        logger.debug("getHtmlForAjaxResultsWithRenderingPattern . Query " + query + " , projectKey = " + projectKey);

        StringBuffer builder = new StringBuffer("[");
        builder.append(EOL);
        String lowerCaseQuery;
        if (query != null) {
            lowerCaseQuery = query.toLowerCase();
            logger.debug("Query: " + lowerCaseQuery);
        } else {
            lowerCaseQuery = null;
        }
        Collection<RestRow> restRows = RestFetcher.doQuery(projectKey);
        for (RestRow restRow : restRows) {
//			String databaseRowId = restRow.getValue().toString();
            String editString = RestRowFormatter.formatRestRow(restRow, renderingPattern, i18nHelper);
            if (lowerCaseQuery == null
                    || editString.toLowerCase().contains(lowerCaseQuery)) {
                builder.append("{ \"label\": \"");
                builder.append(editString.replaceAll("\"", "&quot;"));
                builder.append("\", \"value\": \"");
                builder.append(restRow.getValue());
                builder.append("\", \"inputlabel\": \"");
                builder.append(stripExtraInfo(editString));

                builder.append("\" },");
                builder.append(EOL);
            } else {
                logger.debug("Not adding row to JSON (lowerCaseQuery=" + lowerCaseQuery +
                        ", editString=" + editString +
                        "): " + restRow);
            }
        }

        if (builder.length() > ("[" + EOL).length()) {
            builder.deleteCharAt(builder.length() - EOL.length() - 1);
        }
        builder.append("]");
        String s = builder.toString();
        logger.debug("Returning JSON: " + s);
        return s;
    }

    private String stripExtraInfo(String editString) {
        if (editString.contains("<br/>")) {
            return editString.substring(0, editString.indexOf("<br/>")).replaceAll("\"", "\\\\\"").replaceAll("&#39;", "'");
        } else {
            return editString.replaceAll("&#39;", "'").replaceAll("\"", "\\\\\"");
        }
    }
}
