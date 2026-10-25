package br.ufal.ic.p2.wepayu.exceptions;

public class PersistenciaException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
