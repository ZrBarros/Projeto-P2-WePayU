package br.ufal.ic.p2.wepayu.model.agenda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class AgendaSemanal implements AgendaPagamento {
    private static final long serialVersionUID = 1L;
    private static final LocalDate INICIO_CALENDARIO = LocalDate.of(2005, 1, 1);
    private static final BigDecimal SEMANAS_NO_ANO = new BigDecimal("52");

    private final int intervaloSemanas;
    private final int diaSemana;

    public AgendaSemanal(int intervaloSemanas, int diaSemana) {
        if (intervaloSemanas < 1 || intervaloSemanas > 52 || diaSemana < 1 || diaSemana > 7) {
            throw new IllegalArgumentException("Descricao de agenda invalida");
        }
        this.intervaloSemanas = intervaloSemanas;
        this.diaSemana = diaSemana;
    }

    @Override
    public String descricao() {
        if (intervaloSemanas == 1) {
            return "semanal " + diaSemana;
        }
        return "semanal " + intervaloSemanas + " " + diaSemana;
    }

    @Override
    public boolean devePagar(LocalDate data) {
        long diasDesdeInicio = ChronoUnit.DAYS.between(INICIO_CALENDARIO, data);
        if (diasDesdeInicio < 0 || data.getDayOfWeek().getValue() != diaSemana) {
            return false;
        }
        long numeroSemana = diasDesdeInicio / 7 + 1;
        return numeroSemana % intervaloSemanas == 0;
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate pagamento) {
        return pagamento.minusDays(intervaloSemanas * 7L - 1);
    }

    @Override
    public LocalDate pagamentoAnterior(LocalDate pagamento) {
        return pagamento.minusWeeks(intervaloSemanas);
    }

    @Override
    public BigDecimal salarioDoPeriodo(BigDecimal salarioMensal) {
        return salarioMensal.multiply(new BigDecimal("12"))
                .multiply(BigDecimal.valueOf(intervaloSemanas))
                .divide(SEMANAS_NO_ANO, 10, RoundingMode.HALF_UP);
    }
}
