package br.com.leilao.repository;

import br.com.leilao.domain.LoteLeilao;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositório de lotes (reservado para os próximos módulos: histórico, favoritos, etc.). */
public interface LoteLeilaoRepository extends JpaRepository<LoteLeilao, Long> {
}
