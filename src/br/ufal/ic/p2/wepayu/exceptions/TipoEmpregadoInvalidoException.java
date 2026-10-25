package br.ufal.ic.p2.wepayu.exceptions;

public class TipoEmpregadoInvalidoException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public TipoEmpregadoInvalidoException() {
        super("Tipo invalido.");
    }
}
