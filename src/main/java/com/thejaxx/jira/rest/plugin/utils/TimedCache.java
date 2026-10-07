package com.thejaxx.jira.rest.plugin.utils;

import org.apache.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongSupplier;

/**
 * Guarda o resultado de uma busca cara por um tempo fixo, para uma chave por vez.
 * <p>
 * O Jira desenha o campo (e calcula as estatísticas dos gadgets) uma vez por issue, e cada
 * desenho buscava a lista inteira no endpoint REST. Uma listagem ou um gadget sobre ~1600
 * issues virava ~1600 requisições em poucos segundos e estourava o rate limit do Kong.
 * <p>
 * Regras:
 * <ul>
 *     <li>Só uma thread busca por vez; as demais esperam o resultado dela.</li>
 *     <li>Se já existe um valor vencido para a mesma chave, as demais threads recebem o valor
 *     vencido em vez de esperar a nova busca.</li>
 *     <li>Se a busca falhar, mantém o último valor bom (ou {@code valorEmFalha} se nunca houve)
 *     e só tenta de novo depois de {@code esperaAposFalhaMs}, para não martelar o servidor.</li>
 *     <li>Trocar a chave (ex.: URL configurada) descarta o valor guardado.</li>
 * </ul>
 */
public final class TimedCache<T> {

    private static final Logger logger = Logger.getLogger(TimedCache.class);

    private final long validadeMs;
    private final long esperaAposFalhaMs;
    private final LongSupplier relogio;
    private final ReentrantLock trava = new ReentrantLock();
    private volatile Entrada<T> entrada;

    public TimedCache(long validadeMs, long esperaAposFalhaMs) {
        this(validadeMs, esperaAposFalhaMs, System::currentTimeMillis);
    }

    // Construtor com relógio injetável, usado nos testes
    TimedCache(long validadeMs, long esperaAposFalhaMs, LongSupplier relogio) {
        this.validadeMs = validadeMs;
        this.esperaAposFalhaMs = esperaAposFalhaMs;
        this.relogio = relogio;
    }

    public T get(String chave, Callable<T> busca, T valorEmFalha) {
        Entrada<T> atual = entrada;
        if (atual != null && atual.vale(chave, relogio.getAsLong())) {
            return atual.valor;
        }

        boolean temValorAntigo = atual != null && atual.chave.equals(chave);
        if (temValorAntigo) {
            // Outra thread já está renovando: devolve o valor vencido em vez de esperar
            if (!trava.tryLock()) {
                return atual.valor;
            }
        } else {
            trava.lock();
        }

        try {
            // Revalida: a thread que segurava a trava pode ter acabado de renovar
            atual = entrada;
            if (atual != null && atual.vale(chave, relogio.getAsLong())) {
                return atual.valor;
            }

            try {
                T valor = busca.call();
                entrada = new Entrada<>(chave, valor, relogio.getAsLong() + validadeMs);
                return valor;
            } catch (Exception e) {
                T valor = (atual != null && atual.chave.equals(chave)) ? atual.valor : valorEmFalha;
                entrada = new Entrada<>(chave, valor, relogio.getAsLong() + esperaAposFalhaMs);
                logger.error("Falha ao atualizar o cache; nova tentativa em " + esperaAposFalhaMs + " ms: " + e.getMessage(), e);
                return valor;
            }
        } finally {
            trava.unlock();
        }
    }

    private static final class Entrada<T> {
        final String chave;
        final T valor;
        final long expiraEm;

        Entrada(String chave, T valor, long expiraEm) {
            this.chave = chave;
            this.valor = valor;
            this.expiraEm = expiraEm;
        }

        boolean vale(String outraChave, long agora) {
            return chave.equals(outraChave) && agora < expiraEm;
        }
    }
}
