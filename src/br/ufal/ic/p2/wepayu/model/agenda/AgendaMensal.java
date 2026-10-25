package br.ufal.ic.p2.wepayu.model.agenda;

import java.math.BigDecimal;
import java.time.LocalDate;

final class AgendaMensal implements AgendaPagamento {
    private static final long serialVersionUID = 1L;

    private final int diaMes;

    AgendaMensal(int diaMes) {
        if (diaMes < 1 || diaMes > 28) {
            throw new IllegalArgumentException("Descricao de agenda invalida");
        }
        this.diaMes = diaMes;
    }

    @Override
    public String descricao() {
        return "mensal " + diaMes;
    }

    @Override
    public boolean devePagar(LocalDate data) {
        return data.equals(dataPagamento(data));
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate pagamento) {
        return pagamentoAnterior(pagamento).plusDays(1);
    }

    @Override
    public LocalDate pagamentoAnterior(LocalDate pagamento) {
        LocalDate mesAnterior = pagamento.minusMonths(1);
        return dataPagamento(mesAnterior);
    }

    @Override
    public BigDecimal salarioDoPeriodo(BigDecimal salarioMensal) {
        return salarioMensal;
    }

    private LocalDate dataPagamento(LocalDate referencia) {
        return referencia.withDayOfMonth(diaMes);
    }
}
