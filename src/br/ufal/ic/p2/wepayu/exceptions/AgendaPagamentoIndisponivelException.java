package br.ufal.ic.p2.wepayu.exceptions;

public class AgendaPagamentoIndisponivelException extends WePayUException {
    private static final long serialVersionUID = 1L;

    public AgendaPagamentoIndisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }
}
