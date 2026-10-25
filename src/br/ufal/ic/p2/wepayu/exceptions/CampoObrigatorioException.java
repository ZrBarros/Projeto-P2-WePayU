package br.ufal.ic.p2.wepayu.exceptions;

public class CampoObrigatorioException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public CampoObrigatorioException(String mensagem) {
        super(mensagem);
    }
}
