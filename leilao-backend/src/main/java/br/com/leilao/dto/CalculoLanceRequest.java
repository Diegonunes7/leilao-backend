package br.com.leilao.dto;

import br.com.leilao.domain.LoteLeilao;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Dados de entrada do cálculo. Percentuais são frações decimais (0.05 = 5%).
 */
public record CalculoLanceRequest(

        @Size(max = 200, message = "descricao deve ter no máximo 200 caracteres")
        String descricao,

        @NotNull(message = "precoMercado é obrigatório")
        @Positive(message = "precoMercado deve ser maior que zero")
        @Digits(integer = 13, fraction = 2, message = "precoMercado aceita até 2 casas decimais")
        BigDecimal precoMercado,

        @NotNull(message = "custoReforma é obrigatório")
        @PositiveOrZero(message = "custoReforma não pode ser negativo")
        @Digits(integer = 13, fraction = 2, message = "custoReforma aceita até 2 casas decimais")
        BigDecimal custoReforma,

        @NotNull(message = "taxaLeilaoPercentual é obrigatória")
        @DecimalMin(value = "0.0", message = "taxaLeilaoPercentual não pode ser negativa")
        @DecimalMax(value = "1.0", message = "taxaLeilaoPercentual deve ser no máximo 1 (100%)")
        @Digits(integer = 1, fraction = 4, message = "taxaLeilaoPercentual aceita até 4 casas decimais")
        BigDecimal taxaLeilaoPercentual,

        @NotNull(message = "taxaLeilaoFixa é obrigatória")
        @PositiveOrZero(message = "taxaLeilaoFixa não pode ser negativa")
        @Digits(integer = 13, fraction = 2, message = "taxaLeilaoFixa aceita até 2 casas decimais")
        BigDecimal taxaLeilaoFixa,

        @NotNull(message = "custoLogistica é obrigatório")
        @PositiveOrZero(message = "custoLogistica não pode ser negativo")
        @Digits(integer = 13, fraction = 2, message = "custoLogistica aceita até 2 casas decimais")
        BigDecimal custoLogistica,

        @NotNull(message = "impostosEncargos é obrigatório")
        @PositiveOrZero(message = "impostosEncargos não pode ser negativo")
        @Digits(integer = 13, fraction = 2, message = "impostosEncargos aceita até 2 casas decimais")
        BigDecimal impostosEncargos,

        @NotNull(message = "custoCapital é obrigatório")
        @PositiveOrZero(message = "custoCapital não pode ser negativo")
        @Digits(integer = 13, fraction = 2, message = "custoCapital aceita até 2 casas decimais")
        BigDecimal custoCapital,

        @NotNull(message = "margemDesejada é obrigatória")
        @DecimalMin(value = "0.0", message = "margemDesejada não pode ser negativa")
        @DecimalMax(value = "1.0", inclusive = false, message = "margemDesejada deve ser menor que 1 (100%)")
        @Digits(integer = 1, fraction = 4, message = "margemDesejada aceita até 4 casas decimais")
        BigDecimal margemDesejada,

        @NotNull(message = "lanceAtual é obrigatório")
        @PositiveOrZero(message = "lanceAtual não pode ser negativo")
        @Digits(integer = 13, fraction = 2, message = "lanceAtual aceita até 2 casas decimais")
        BigDecimal lanceAtual
) {

    /** Converte o DTO em entidade (não persistida) para alimentar o service. */
    public LoteLeilao toLote() {
        LoteLeilao lote = new LoteLeilao();
        lote.setDescricao(descricao);
        lote.setPrecoMercado(precoMercado);
        lote.setCustoReforma(custoReforma);
        lote.setTaxaLeilaoPercentual(taxaLeilaoPercentual);
        lote.setTaxaLeilaoFixa(taxaLeilaoFixa);
        lote.setCustoLogistica(custoLogistica);
        lote.setImpostosEncargos(impostosEncargos);
        lote.setCustoCapital(custoCapital);
        lote.setMargemDesejada(margemDesejada);
        return lote;
    }
}
