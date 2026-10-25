package br.ufal.ic.p2.wepayu.exceptions;

public class WePayUException extends Exception {
    private static final long serialVersionUID = 1L;

    public WePayUException(String mensagem) {
        super(mensagem);
    }

    public WePayUException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
