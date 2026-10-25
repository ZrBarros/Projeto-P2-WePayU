package br.ufal.ic.p2.wepayu.model;

import br.ufal.ic.p2.wepayu.util.Formatador;
import br.ufal.ic.p2.wepayu.model.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.model.agenda.AgendasPadrao;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class EmpregadoAssalariado extends Empregado {
    private static final long serialVersionUID = 1L;

    public EmpregadoAssalariado(String id, String nome, String endereco, BigDecimal salarioMensal) {
        super(id, nome, endereco, salarioMensal, AgendasPadrao.assalariado());
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.ASSALARIADO;
    }

    @Override
    protected AgendaPagamento agendaPadrao() {
        return AgendasPadrao.assalariado();
    }

    @Override
    public CalculoBruto calcularBruto(LocalDate pagamento) {
        BigDecimal bruto = Formatador.centavos(salarioDoPeriodo());
        return new CalculoBruto(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, bruto);
    }
}
