package br.com.leilao.controller;

import br.com.leilao.service.CalculoLeilaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculoLeilaoController.class)
@Import(CalculoLeilaoService.class)
class CalculoLeilaoControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final String JSON_VALIDO = """
            {"descricao":"Onix 2019","precoMercado":50000.00,"custoReforma":5000.00,
             "taxaLeilaoPercentual":0.05,"taxaLeilaoFixa":800.00,"custoLogistica":1500.00,
             "impostosEncargos":2000.00,"custoCapital":1500.00,"margemDesejada":0.20,
             "lanceAtual":%s}""";

    @Test
    void retornaLanceMaximoESemaforoVerde() throws Exception {
        mvc.perform(post("/api/v1/leiloes/calculo-lance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO.formatted("25000.00")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lanceMaximo").value(27809.52))
                .andExpect(jsonPath("$.status").value("VERDE"));
    }

    @Test
    void retornaVermelhoQuandoLanceAcimaDoMaximo() throws Exception {
        mvc.perform(post("/api/v1/leiloes/calculo-lance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO.formatted("30000.00")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VERMELHO"));
    }

    @Test
    void retorna400ParaValorNegativo() throws Exception {
        mvc.perform(post("/api/v1/leiloes/calculo-lance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_VALIDO.formatted("-10")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.lanceAtual").exists());
    }

    @Test
    void retorna400ParaJsonMalformado() throws Exception {
        mvc.perform(post("/api/v1/leiloes/calculo-lance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalido"))
                .andExpect(status().isBadRequest());
    }
}
