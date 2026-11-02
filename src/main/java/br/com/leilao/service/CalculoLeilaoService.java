package br.com.leilao.service;

import br.com.leilao.domain.LoteLeilao;
import br.com.leilao.domain.StatusSemaforo;
import br.com.leilao.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Regras matemáticas do cálculo de lance máximo e do semáforo de risco.
 *
 * <p>Política de arredondamento: valores monetários têm 2 casas decimais e o lance máximo e o
 * limite verde são arredondados <b>para baixo</b> (RoundingMode.DOWN). Assim o sistema nunca
 * recomenda um lance maior do que a margem permite. Toda a comparação do semáforo usa os
 * valores já arredondados, que são exatamente os exibidos ao usuário.</p>
 */
@Service
public class CalculoLeilaoService {

    private static final int ESCALA_MONETARIA = 2;

    /** Fração do lance máximo até a qual o lance é considerado confortável (90%). */
    private static final BigDecimal FATOR_CONFORTO = new BigDecimal("0.90");

    /**
     * C_parcial = custoReforma + custoLogistica + impostosEncargos + custoCapital + taxaLeilaoFixa.
     * São os custos que existem independentemente do valor do lance.
     */
    public BigDecimal calcularCustoParcial(LoteLeilao lote) {
        validar(lote);
        return lote.getCustoReforma()
                .add(lote.getCustoLogistica())
                .add(lote.getImpostosEncargos())
                .add(lote.getCustoCapital())
                .add(lote.getTaxaLeilaoFixa());
    }

    /**
     * Lance Máximo = (precoMercado - C_parcial - precoMercado * margemDesejada) / (1 + taxaLeilaoPercentual).
     *
     * <p>Se o numerador for zero ou negativo, o lote é inviável para a margem desejada e o
     * retorno é 0,00 (não existe lance positivo que preserve a margem).</p>
     */
    public BigDecimal calcularLanceMaximo(LoteLeilao lote) {
        BigDecimal custoParcial = calcularCustoParcial(lote); // também valida o lote

        BigDecimal margemEmReais = lote.getPrecoMercado().multiply(lote.getMargemDesejada());
        BigDecimal numerador = lote.getPrecoMercado().subtract(custoParcial).subtract(margemEmReais);

        if (numerador.signum() <= 0) {
            return BigDecimal.ZERO.setScale(ESCALA_MONETARIA);
        }

        BigDecimal divisor = BigDecimal.ONE.add(lote.getTaxaLeilaoPercentual());
        // Proteção explícita contra divisão por zero (não ocorre com taxa >= 0, mas o service não confia no chamador).
        if (divisor.signum() <= 0) {
            throw new RegraNegocioException("Divisor inválido (1 + taxa de leilão) deve ser maior que zero.");
        }

        // Divisão direta com 2 casas e DOWN: evita erro de duplo arredondamento.
        return numerador.divide(divisor, ESCALA_MONETARIA, RoundingMode.DOWN);
    }

    /**
     * Semáforo de risco:
     * <ul>
     *   <li>VERDE: lance atual &lt;= lanceMaximo * 0,90</li>
     *   <li>AMARELO: lanceMaximo * 0,90 &lt; lance atual &lt;= lanceMaximo</li>
     *   <li>VERMELHO: lance atual &gt; lanceMaximo (ou lote inviável, lanceMaximo = 0)</li>
     * </ul>
     */
    public StatusSemaforo classificarRisco(BigDecimal lanceMaximo, BigDecimal lanceAtual) {
        if (lanceMaximo == null || lanceAtual == null) {
            throw new RegraNegocioException("Lance máximo e lance atual são obrigatórios.");
        }
        if (lanceAtual.signum() < 0) {
            throw new RegraNegocioException("O lance atual não pode ser negativo.");
        }
        // Lote inviável: qualquer lance resulta em prejuízo frente à margem desejada.
        if (lanceMaximo.signum() <= 0) {
            return StatusSemaforo.VERMELHO;
        }
        if (lanceAtual.compareTo(lanceMaximo) > 0) {
            return StatusSemaforo.VERMELHO;
        }
        if (lanceAtual.compareTo(calcularLimiteVerde(lanceMaximo)) <= 0) {
            return StatusSemaforo.VERDE;
        }
        return StatusSemaforo.AMARELO;
    }

    /** Orquestra o cálculo completo: custo parcial, lance máximo, semáforo e lucro estimado. */
    public ResultadoAvaliacao avaliar(LoteLeilao lote, BigDecimal lanceAtual) {
        if (lanceAtual == null || lanceAtual.signum() < 0) {
            throw new RegraNegocioException("O lance atual é obrigatório e não pode ser negativo.");
        }

        BigDecimal custoParcial = calcularCustoParcial(lote);
        BigDecimal lanceMaximo = calcularLanceMaximo(lote);
        BigDecimal limiteVerde = calcularLimiteVerde(lanceMaximo);
        StatusSemaforo status = classificarRisco(lanceMaximo, lanceAtual);
        BigDecimal lucro = calcularLucroEstimado(lote, custoParcial, lanceAtual);

        return new ResultadoAvaliacao(
                custoParcial.setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN),
                lanceMaximo,
                limiteVerde,
                lanceAtual.setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN),
                lucro,
                status,
                status.getIcone() + " " + status.getMensagem());
    }

    /** Lucro = precoMercado - (lance * (1 + taxa%) + C_parcial). Negativo indica prejuízo. */
    private BigDecimal calcularLucroEstimado(LoteLeilao lote, BigDecimal custoParcial, BigDecimal lanceAtual) {
        BigDecimal lanceComTaxa = lanceAtual.multiply(BigDecimal.ONE.add(lote.getTaxaLeilaoPercentual()));
        return lote.getPrecoMercado()
                .subtract(lanceComTaxa)
                .subtract(custoParcial)
                .setScale(ESCALA_MONETARIA, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calcularLimiteVerde(BigDecimal lanceMaximo) {
        return lanceMaximo.multiply(FATOR_CONFORTO).setScale(ESCALA_MONETARIA, RoundingMode.DOWN);
    }

    /** Validações de domínio, independentes do Bean Validation (o service pode ser chamado por outros módulos). */
    private void validar(LoteLeilao lote) {
        if (lote == null) {
            throw new RegraNegocioException("O lote é obrigatório.");
        }
        exigirPositivo(lote.getPrecoMercado(), "precoMercado");
        exigirNaoNegativo(lote.getCustoReforma(), "custoReforma");
        exigirNaoNegativo(lote.getTaxaLeilaoFixa(), "taxaLeilaoFixa");
        exigirNaoNegativo(lote.getCustoLogistica(), "custoLogistica");
        exigirNaoNegativo(lote.getImpostosEncargos(), "impostosEncargos");
        exigirNaoNegativo(lote.getCustoCapital(), "custoCapital");
        exigirNaoNegativo(lote.getTaxaLeilaoPercentual(), "taxaLeilaoPercentual");

        BigDecimal margem = lote.getMargemDesejada();
        exigirNaoNegativo(margem, "margemDesejada");
        if (margem.compareTo(BigDecimal.ONE) >= 0) {
            throw new RegraNegocioException("margemDesejada deve ser menor que 1 (100%).");
        }
    }

    private void exigirPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new RegraNegocioException(campo + " é obrigatório e deve ser maior que zero.");
        }
    }

    private void exigirNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new RegraNegocioException(campo + " é obrigatório e não pode ser negativo.");
        }
    }
}
