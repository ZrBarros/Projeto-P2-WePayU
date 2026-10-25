package br.ufal.ic.p2.wepayu.model;

import br.ufal.ic.p2.wepayu.exceptions.RegraNegocioException;
import br.ufal.ic.p2.wepayu.util.Formatador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public final class EmpregadoComissionado extends Empregado {
    private static final long serialVersionUID = 1L;
    private static final LocalDate PRIMEIRO_PAGAMENTO = LocalDate.of(2005, 1, 14);

    private BigDecimal comissao;
    private final ArrayList<Venda> vendas = new ArrayList<>();

    public EmpregadoComissionado(String id, String nome, String endereco, BigDecimal salarioMensal,
                                 BigDecimal comissao) {
        super(id, nome, endereco, salarioMensal);
        if (comissao == null || comissao.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Comissao deve ser nao-negativa.");
        }
        this.comissao = comissao;
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.COMISSIONADO;
    }

    @Override
    public boolean deveReceber(LocalDate data) {
        return data.getDayOfWeek() == DayOfWeek.FRIDAY
                && !data.isBefore(PRIMEIRO_PAGAMENTO)
                && ChronoUnit.WEEKS.between(PRIMEIRO_PAGAMENTO, data) % 2 == 0;
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate pagamento) {
        return pagamento.minusDays(13);
    }

    @Override
    public CalculoBruto calcularBruto(LocalDate pagamento) {
        LocalDate inicio = inicioPeriodo(pagamento);
        LocalDate fimExclusivo = pagamento.plusDays(1);
        BigDecimal totalVendas = vendasNoPeriodo(inicio, fimExclusivo);
        BigDecimal fixo = Formatador.centavos(getSalario().multiply(new BigDecimal("12"))
                .divide(new BigDecimal("26"), 10, RoundingMode.HALF_UP));
        BigDecimal valorComissao = Formatador.centavos(totalVendas.multiply(comissao));
        BigDecimal bruto = Formatador.centavos(fixo.add(valorComissao));
        return new CalculoBruto(BigDecimal.ZERO, BigDecimal.ZERO, fixo, totalVendas,
                valorComissao, bruto);
    }

    @Override
    public BigDecimal getComissao() {
        return comissao;
    }

    @Override
    public void alterarComissao(BigDecimal comissao) throws RegraNegocioException {
        if (comissao == null || comissao.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Comissao deve ser nao-negativa.");
        }
        this.comissao = comissao;
    }

    @Override
    public void lancarVenda(Venda venda) {
        vendas.add(venda);
    }

    @Override
    public BigDecimal vendasRealizadas(LocalDate inicio, LocalDate fimExclusivo) {
        return vendasNoPeriodo(inicio, fimExclusivo);
    }

    private BigDecimal vendasNoPeriodo(LocalDate inicio, LocalDate fimExclusivo) {
        return Formatador.centavos(vendas.stream()
                .filter(venda -> noPeriodo(venda.getData(), inicio, fimExclusivo))
                .map(Venda::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
