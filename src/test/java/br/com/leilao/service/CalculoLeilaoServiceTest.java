package br.com.leilao.service;

import br.com.leilao.domain.LoteLeilao;
import br.com.leilao.domain.StatusSemaforo;
import br.com.leilao.exception.RegraNegocioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Cenário base (conferido à mão):
 * C_parcial = 5000 + 1500 + 2000 + 1500 + 800 = 10.800
 * Numerador = 50.000 - 10.800 - (50.000 * 0,20) = 29.200
 * Lance máximo = 29.200 / 1,05 = 27.809,5238... -> 27.809,52 (DOWN)
 * Limite verde = 27.809,52 * 0,90 = 25.028,568 -> 25.028,56 (DOWN)
 */
class CalculoLeilaoServiceTest {

    private CalculoLeilaoService service;
    private LoteLeilao lote;

    @BeforeEach
    void setUp() {
        service = new CalculoLeilaoService();
        lote = new LoteLeilao();
        lote.setPrecoMercado(new BigDecimal("50000.00"));
        lote.setCustoReforma(new BigDecimal("5000.00"));
        lote.setCustoLogistica(new BigDecimal("1500.00"));
        lote.setImpostosEncargos(new BigDecimal("2000.00"));
        lote.setCustoCapital(new BigDecimal("1500.00"));
        lote.setTaxaLeilaoFixa(new BigDecimal("800.00"));
        lote.setTaxaLeilaoPercentual(new BigDecimal("0.05"));
        lote.setMargemDesejada(new BigDecimal("0.20"));
    }

    @Test
    void calculaCustoParcial() {
        assertEquals(new BigDecimal("10800.00"), service.calcularCustoParcial(lote));
    }

    @Test
    void calculaLanceMaximo() {
        assertEquals(new BigDecimal("27809.52"), service.calcularLanceMaximo(lote));
    }

    @Test
    void semaforoVerdeAbaixoDoLimite() {
        assertEquals(StatusSemaforo.VERDE, service.avaliar(lote, new BigDecimal("25000.00")).status());
    }

    @Test
    void fronteiraVerdeAmarelo() {
        assertEquals(StatusSemaforo.VERDE, service.avaliar(lote, new BigDecimal("25028.56")).status());
        assertEquals(StatusSemaforo.AMARELO, service.avaliar(lote, new BigDecimal("25028.57")).status());
    }

    @Test
    void fronteiraAmareloVermelho() {
        assertEquals(StatusSemaforo.AMARELO, service.avaliar(lote, new BigDecimal("27809.52")).status());
        assertEquals(StatusSemaforo.VERMELHO, service.avaliar(lote, new BigDecimal("27809.53")).status());
    }

    @Test
    void lucroNoLanceMaximoFicaProximoDaMargemDesejada() {
        // Margem desejada = 10.000; a diferença vem apenas do arredondamento DOWN do lance.
        ResultadoAvaliacao r = service.avaliar(lote, new BigDecimal("27809.52"));
        assertEquals(new BigDecimal("10000.00"), r.lucroEstimado());
    }

    @Test
    void loteInviavelRetornaLanceZeroEVermelho() {
        lote.setCustoReforma(new BigDecimal("40000.00")); // custos + margem superam o preço de mercado
        ResultadoAvaliacao r = service.avaliar(lote, new BigDecimal("0.00"));
        assertEquals(new BigDecimal("0.00"), r.lanceMaximo());
        assertEquals(StatusSemaforo.VERMELHO, r.status());
    }

    @Test
    void rejeitaValoresNegativos() {
        lote.setCustoLogistica(new BigDecimal("-1"));
        assertThrows(RegraNegocioException.class, () -> service.calcularLanceMaximo(lote));
    }

    @Test
    void rejeitaTaxaNegativaQueGerariaDivisaoPorZero() {
        lote.setTaxaLeilaoPercentual(new BigDecimal("-1"));
        assertThrows(RegraNegocioException.class, () -> service.calcularLanceMaximo(lote));
    }

    @Test
    void rejeitaMargemMaiorOuIgualA100Porcento() {
        lote.setMargemDesejada(BigDecimal.ONE);
        assertThrows(RegraNegocioException.class, () -> service.calcularLanceMaximo(lote));
    }

    @Test
    void rejeitaLanceAtualNegativo() {
        assertThrows(RegraNegocioException.class, () -> service.avaliar(lote, new BigDecimal("-1")));
    }
}
