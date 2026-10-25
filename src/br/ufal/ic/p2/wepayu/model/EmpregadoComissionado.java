package br.ufal.ic.p2.wepayu.model;

import br.ufal.ic.p2.wepayu.exceptions.RegraNegocioException;
import br.ufal.ic.p2.wepayu.model.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.model.agenda.AgendasPadrao;
import br.ufal.ic.p2.wepayu.util.Formatador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public final class EmpregadoComissionado extends Empregado {
    private static final long serialVersionUID = 1L;
    private BigDecimal comissao;
    private final ArrayList<Venda> vendas = new ArrayList<>();

    public EmpregadoComissionado(String id, String nome, String endereco, BigDecimal salarioMensal,
                                 BigDecimal comissao) {
        super(id, nome, endereco, salarioMensal, AgendasPadrao.comissionado());
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
    protected AgendaPagamento agendaPadrao() {
        return AgendasPadrao.comissionado();
    }

    @Override
    public CalculoBruto calcularBruto(LocalDate pagamento) {
        LocalDate inicio = inicioPeriodo(pagamento);
        LocalDate fimExclusivo = pagamento.plusDays(1);
        BigDecimal totalVendas = vendasNoPeriodo(inicio, fimExclusivo);
        BigDecimal fixo = Formatador.centavos(salarioDoPeriodo());
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
