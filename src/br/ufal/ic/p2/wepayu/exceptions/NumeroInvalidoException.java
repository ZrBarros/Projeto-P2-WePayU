package br.ufal.ic.p2.wepayu.exceptions;

public class NumeroInvalidoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public NumeroInvalidoException(String mensagem) {
        super(mensagem);
    }
}
