package br.ufal.ic.p2.wepayu.model.agenda;

public final class AgendasPadrao {
    private AgendasPadrao() {
    }

    public static AgendaPagamento horista() {
        return new AgendaSemanal(1, 5);
    }

    public static AgendaPagamento assalariado() {
        return new AgendaMensal(null);
    }

    public static AgendaPagamento comissionado() {
        return new AgendaSemanal(2, 5);
    }
}
