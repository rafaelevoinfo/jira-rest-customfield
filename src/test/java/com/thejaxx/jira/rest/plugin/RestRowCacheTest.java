package com.thejaxx.jira.rest.plugin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RestRowCacheTest {

    @Test
    public void cacheVazioDevolveNuloEmVezDeQuebrar() {
        // Cenário do gadget de estatísticas quando a busca no endpoint falhou (lista vazia)
        assertNull(new RestRowCache().getRow(null, "IOG"));
    }

    @Test
    public void devolveOValorGuardado() {
        RestRowCache cache = new RestRowCache();
        RestRow row = new RestRow("IOG", "IOG");
        cache.addCacheEntry("null", "IOG", row);
        assertEquals(row, cache.getRow(null, "IOG"));
        assertNull(cache.getRow(null, "OUTRA"));
    }
}
