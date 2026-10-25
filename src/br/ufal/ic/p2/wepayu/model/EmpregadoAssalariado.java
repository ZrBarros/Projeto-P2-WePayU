package br.ufal.ic.p2.wepayu.model;

import br.ufal.ic.p2.wepayu.util.Formatador;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

public final class EmpregadoAssalariado extends Empregado {
    private static final long serialVersionUID = 1L;

    public EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal) {
        super(id, nome, endereco, salarioMensal);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.ASSALARIADO;
    }

    @Override
    public boolean deveReceber(LocalDate data) {
        LocalDate ultimoDiaUtil = data.withDayOfMonth(data.lengthOfMonth());
        while (ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SATURDAY
                || ultimoDiaUtil.getDayOfWeek() == DayOfWeek.SUNDAY) {
            ultimoDiaUtil = ultimoDiaUtil.minusDays(1);
        }
        return data.equals(ultimoDiaUtil);
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate pagamento) {
        return pagamento.withDayOfMonth(1);
    }

    @Override
    public CalculoBruto calcularBruto(LocalDate pagamento) {
        BigDecimal bruto = Formatador.centavos(getSalario());
        return new CalculoBruto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, bruto);
    }
}
