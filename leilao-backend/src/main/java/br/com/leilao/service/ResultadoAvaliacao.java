package br.com.leilao.service;

import br.com.leilao.domain.StatusSemaforo;

import java.math.BigDecimal;

/**
 * Resultado da avaliação de um lance.
 *
 * @param custoParcial     custos fixos além do lance (reforma + logística + impostos + capital + taxa fixa)
 * @param lanceMaximo      maior lance que ainda preserva a margem desejada (arredondado para baixo)
 * @param limiteVerde      90% do lance máximo: até aqui a margem é confortável
 * @param lanceAtual       lance informado pelo usuário
 * @param lucroEstimado    lucro esperado se o lote for arrematado pelo lance atual
 * @param status           classificação do semáforo
 * @param mensagem         texto amigável com ícone e descrição do status
 */
public record ResultadoAvaliacao(
        BigDecimal custoParcial,
        BigDecimal lanceMaximo,
        BigDecimal limiteVerde,
        BigDecimal lanceAtual,
        BigDecimal lucroEstimado,
        StatusSemaforo status,
        String mensagem
) {
}
