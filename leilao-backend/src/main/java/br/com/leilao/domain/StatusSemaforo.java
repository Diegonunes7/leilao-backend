package br.com.leilao.domain;

/** Classificação de risco (semáforo) do lance atual em relação ao lance máximo. */
public enum StatusSemaforo {

    VERDE("🟢", "Margem confortável"),
    AMARELO("🟡", "Margem no limite"),
    VERMELHO("🔴", "Prejuízo / inviável");

    private final String icone;
    private final String mensagem;

    StatusSemaforo(String icone, String mensagem) {
        this.icone = icone;
        this.mensagem = mensagem;
    }

    public String getIcone() { return icone; }
    public String getMensagem() { return mensagem; }
}
