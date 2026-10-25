package br.ufal.ic.p2.wepayu.model.agenda;

import java.math.BigDecimal;
import java.time.LocalDate;

final class AgendaMensalUltimoDia implements AgendaPagamento {
    private static final long serialVersionUID = 1L;

    @Override
    public String descricao() {
        return "mensal $";
    }

    @Override
    public boolean devePagar(LocalDate data) {
        return data.getDayOfMonth() == data.lengthOfMonth();
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate pagamento) {
        return pagamento.withDayOfMonth(1);
    }

    @Override
    public LocalDate pagamentoAnterior(LocalDate pagamento) {
        LocalDate mesAnterior = pagamento.minusMonths(1);
        return mesAnterior.withDayOfMonth(mesAnterior.lengthOfMonth());
    }

    @Override
    public BigDecimal salarioDoPeriodo(BigDecimal salarioMensal) {
        return salarioMensal;
    }
}
