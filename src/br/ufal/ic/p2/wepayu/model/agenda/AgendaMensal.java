package br.ufal.ic.p2.wepayu.model.agenda;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

public final class AgendaMensal implements AgendaPagamento {
    private static final long serialVersionUID = 1L;

    private final Integer diaMes;

    public AgendaMensal(Integer diaMes) {
        if (diaMes != null && (diaMes < 1 || diaMes > 28)) {
            throw new IllegalArgumentException("Descricao de agenda invalida");
        }
        this.diaMes = diaMes;
    }

    @Override
    public String descricao() {
        return diaMes == null ? "mensal $" : "mensal " + diaMes;
    }

    @Override
    public boolean devePagar(LocalDate data) {
        return data.equals(dataPagamento(data));
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate pagamento) {
        if (diaMes == null) {
            return pagamento.withDayOfMonth(1);
        }
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
        if (diaMes != null) {
            return referencia.withDayOfMonth(diaMes);
        }
        LocalDate ultimoDiaUtil = referencia.withDayOfMonth(referencia.lengthOfMonth());
        while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY
                || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
            ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
        }
        return ultimoDiaUtil;
    }
}
