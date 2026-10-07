package com.thejaxx.jira.rest.plugin.utils;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TimedCacheTest {

    private final AtomicLong agora = new AtomicLong(0);
    private final AtomicInteger buscas = new AtomicInteger();
    private final TimedCache<String> cache = new TimedCache<>(1000, 100, agora::get);

    private String buscar(String valor) {
        buscas.incrementAndGet();
        return valor;
    }

    @Test
    public void reaproveitaOValorDentroDaValidade() {
        for (int i = 0; i < 1600; i++) {
            assertEquals("a", cache.get("url", () -> buscar("a"), "falha"));
        }
        assertEquals(1, buscas.get());
    }

    @Test
    public void buscaDeNovoQuandoAValidadeVence() {
        cache.get("url", () -> buscar("a"), "falha");
        agora.set(1000);
        assertEquals("b", cache.get("url", () -> buscar("b"), "falha"));
        assertEquals(2, buscas.get());
    }

    @Test
    public void trocarAChaveDescartaOValorGuardado() {
        cache.get("url1", () -> buscar("a"), "falha");
        assertEquals("b", cache.get("url2", () -> buscar("b"), "falha"));
        assertEquals(2, buscas.get());
    }

    @Test
    public void falhaMantemOUltimoValorBomEEsperaAntesDeTentarDeNovo() {
        cache.get("url", () -> buscar("a"), "falha");
        agora.set(1000);
        assertEquals("a", cache.get("url", () -> { buscas.incrementAndGet(); throw new IOException("HTTP 429"); }, "falha"));
        // Dentro da espera após a falha não busca de novo
        agora.set(1099);
        assertEquals("a", cache.get("url", () -> buscar("b"), "falha"));
        assertEquals(2, buscas.get());
        agora.set(1100);
        assertEquals("b", cache.get("url", () -> buscar("b"), "falha"));
        assertEquals(3, buscas.get());
    }

    @Test
    public void falhaSemValorAnteriorDevolveOValorDeFalhaEEspera() {
        assertEquals("falha", cache.get("url", () -> { buscas.incrementAndGet(); throw new IOException("timeout"); }, "falha"));
        assertEquals("falha", cache.get("url", () -> buscar("a"), "falha"));
        assertEquals(1, buscas.get());
        agora.set(100);
        assertEquals("a", cache.get("url", () -> buscar("a"), "falha"));
    }

    @Test
    public void threadsSimultaneasFazemUmaUnicaBusca() throws Exception {
        CountDownLatch largada = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(16);
        try {
            List<Future<String>> resultados = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                resultados.add(pool.submit(() -> {
                    largada.await();
                    return cache.get("url", () -> {
                        Thread.sleep(50);
                        return buscar("a");
                    }, "falha");
                }));
            }
            largada.countDown();
            for (Future<String> resultado : resultados) {
                assertEquals("a", resultado.get(5, TimeUnit.SECONDS));
            }
        } finally {
            pool.shutdownNow();
        }
        assertEquals(1, buscas.get());
    }
}
