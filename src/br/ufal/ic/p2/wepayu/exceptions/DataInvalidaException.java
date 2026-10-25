package br.ufal.ic.p2.wepayu.exceptions;

public class DataInvalidaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public DataInvalidaException(String mensagem) {
        super(mensagem);
    }
}
