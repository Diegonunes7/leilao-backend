package br.com.leilao.controller;

import br.com.leilao.dto.CalculoLanceRequest;
import br.com.leilao.service.CalculoLeilaoService;
import br.com.leilao.service.ResultadoAvaliacao;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API do cálculo de lance máximo e semáforo de risco. */
@RestController
@RequestMapping("/api/v1/leiloes")
public class CalculoLeilaoController {

    private final CalculoLeilaoService service;

    public CalculoLeilaoController(CalculoLeilaoService service) {
        this.service = service;
    }

    /**
     * POST /api/v1/leiloes/calculo-lance
     * Recebe os custos do lote e o lance atual do pregão; devolve o lance máximo e o semáforo.
     */
    @PostMapping("/calculo-lance")
    public ResponseEntity<ResultadoAvaliacao> calcular(@Valid @RequestBody CalculoLanceRequest request) {
        ResultadoAvaliacao resultado = service.avaliar(request.toLote(), request.lanceAtual());
        return ResponseEntity.ok(resultado);
    }
}
