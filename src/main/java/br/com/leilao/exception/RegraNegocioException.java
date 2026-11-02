package br.com.leilao.exception;

/** Violação de regra de negócio ou de domínio (ex.: valores negativos, margem >= 100%). */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
