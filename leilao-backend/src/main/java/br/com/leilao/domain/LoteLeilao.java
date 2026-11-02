package br.com.leilao.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Lote de leilão com todos os parâmetros financeiros usados no cálculo do lance máximo.
 *
 * <p>Todos os valores monetários usam {@link BigDecimal} (nunca double/float) para evitar
 * erros de ponto flutuante. Percentuais são guardados como fração decimal
 * (ex.: 0.05 = 5%, 0.20 = 20%).</p>
 */
@Entity
@Table(name = "lote_leilao")
public class LoteLeilao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificação livre do veículo/lote (ex.: "Onix 1.0 2019 - Lote 123"). */
    @Column(length = 200)
    private String descricao;

    /** V_mercado: preço de revenda estimado do veículo. */
    @Column(name = "preco_mercado", nullable = false, precision = 15, scale = 2)
    private BigDecimal precoMercado;

    /** C_reforma: custo estimado de reparos/reforma. */
    @Column(name = "custo_reforma", nullable = false, precision = 15, scale = 2)
    private BigDecimal custoReforma;

    /** Comissão do leiloeiro como fração do lance (ex.: 0.05 = 5%). */
    @Column(name = "taxa_leilao_percentual", nullable = false, precision = 7, scale = 4)
    private BigDecimal taxaLeilaoPercentual;

    /** Taxa administrativa fixa de pátio. */
    @Column(name = "taxa_leilao_fixa", nullable = false, precision = 15, scale = 2)
    private BigDecimal taxaLeilaoFixa;

    /** C_logistica: guincho, frete, deslocamento etc. */
    @Column(name = "custo_logistica", nullable = false, precision = 15, scale = 2)
    private BigDecimal custoLogistica;

    /** D_impostos: IPVA, licenciamento, transferência e demais encargos. */
    @Column(name = "impostos_encargos", nullable = false, precision = 15, scale = 2)
    private BigDecimal impostosEncargos;

    /** C_capital: custo de oportunidade do capital imobilizado. */
    @Column(name = "custo_capital", nullable = false, precision = 15, scale = 2)
    private BigDecimal custoCapital;

    /** M_desejada: margem desejada sobre o preço de mercado (ex.: 0.20 = 20%). */
    @Column(name = "margem_desejada", nullable = false, precision = 7, scale = 4)
    private BigDecimal margemDesejada;

    public Long getId() { return id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPrecoMercado() { return precoMercado; }
    public void setPrecoMercado(BigDecimal precoMercado) { this.precoMercado = precoMercado; }

    public BigDecimal getCustoReforma() { return custoReforma; }
    public void setCustoReforma(BigDecimal custoReforma) { this.custoReforma = custoReforma; }

    public BigDecimal getTaxaLeilaoPercentual() { return taxaLeilaoPercentual; }
    public void setTaxaLeilaoPercentual(BigDecimal taxaLeilaoPercentual) { this.taxaLeilaoPercentual = taxaLeilaoPercentual; }

    public BigDecimal getTaxaLeilaoFixa() { return taxaLeilaoFixa; }
    public void setTaxaLeilaoFixa(BigDecimal taxaLeilaoFixa) { this.taxaLeilaoFixa = taxaLeilaoFixa; }

    public BigDecimal getCustoLogistica() { return custoLogistica; }
    public void setCustoLogistica(BigDecimal custoLogistica) { this.custoLogistica = custoLogistica; }

    public BigDecimal getImpostosEncargos() { return impostosEncargos; }
    public void setImpostosEncargos(BigDecimal impostosEncargos) { this.impostosEncargos = impostosEncargos; }

    public BigDecimal getCustoCapital() { return custoCapital; }
    public void setCustoCapital(BigDecimal custoCapital) { this.custoCapital = custoCapital; }

    public BigDecimal getMargemDesejada() { return margemDesejada; }
    public void setMargemDesejada(BigDecimal margemDesejada) { this.margemDesejada = margemDesejada; }
}
