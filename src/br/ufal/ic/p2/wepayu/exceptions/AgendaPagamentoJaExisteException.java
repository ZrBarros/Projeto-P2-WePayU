package br.ufal.ic.p2.wepayu.exceptions;

public class AgendaPagamentoJaExisteException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public AgendaPagamentoJaExisteException() {
        super("Agenda de pagamentos ja existe");
    }
}
