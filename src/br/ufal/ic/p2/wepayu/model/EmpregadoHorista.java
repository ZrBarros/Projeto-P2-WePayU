package br.ufal.ic.p2.wepayu.model;

import br.ufal.ic.p2.wepayu.util.Formatador;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public final class EmpregadoHorista extends Empregado {
    private static final long serialVersionUID = 1L;
    private static final BigDecimal LIMITE_NORMAL = new BigDecimal("8");
    private static final BigDecimal ADICIONAL_EXTRA = new BigDecimal("1.5");

    private final ArrayList<CartaoPonto> cartoes = new ArrayList<>();

    public EmpregadoHorista(String id, String nome, String endereco, BigDecimal salarioHora) {
        super(id, nome, endereco, salarioHora);
    }

    @Override
    public TipoEmpregado getTipo() {
        return TipoEmpregado.HORISTA;
    }

    @Override
    public boolean deveReceber(LocalDate data) {
        return data.getDayOfWeek() == DayOfWeek.FRIDAY;
    }

    @Override
    public LocalDate inicioPeriodo(LocalDate pagamento) {
        return pagamento.minusDays(6);
    }

    @Override
    public CalculoBruto calcularBruto(LocalDate pagamento) {
        LocalDate inicio = inicioPeriodo(pagamento);
        LocalDate fimExclusivo = pagamento.plusDays(1);
        BigDecimal normais = horasNormaisNoPeriodo(inicio, fimExclusivo);
        BigDecimal extras = horasExtrasNoPeriodo(inicio, fimExclusivo);
        BigDecimal brutoNormal = getSalario().multiply(normais);
        BigDecimal brutoExtra = getSalario().multiply(ADICIONAL_EXTRA).multiply(extras);
        BigDecimal bruto = Formatador.centavos(brutoNormal.add(brutoExtra));
        return new CalculoBruto(normais, extras, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, bruto);
    }

    @Override
    public LocalDate inicioDescontos(LocalDate pagamento) {
        LocalDate primeiroCartao = cartoes.stream()
                .map(CartaoPonto::getData)
                .min(LocalDate::compareTo)
                .orElse(pagamento);
        for (LocalDate anterior = pagamento.minusWeeks(1);
             !anterior.isBefore(primeiroCartao);
             anterior = anterior.minusWeeks(1)) {
            if (calcularBruto(anterior).getBruto().compareTo(BigDecimal.ZERO) > 0) {
                return anterior.plusDays(1);
            }
        }
        return primeiroCartao;
    }

    @Override
    public void lancarCartao(CartaoPonto cartao) {
        cartoes.add(cartao);
    }

    @Override
    public BigDecimal horasNormais(LocalDate inicio, LocalDate fimExclusivo) {
        return horasNormaisNoPeriodo(inicio, fimExclusivo);
    }

    @Override
    public BigDecimal horasExtras(LocalDate inicio, LocalDate fimExclusivo) {
        return horasExtrasNoPeriodo(inicio, fimExclusivo);
    }

    private BigDecimal horasNormaisNoPeriodo(LocalDate inicio, LocalDate fimExclusivo) {
        return cartoes.stream()
                .filter(cartao -> noPeriodo(cartao.getData(), inicio, fimExclusivo))
                .map(cartao -> cartao.getHoras().min(LIMITE_NORMAL))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal horasExtrasNoPeriodo(LocalDate inicio, LocalDate fimExclusivo) {
        return cartoes.stream()
                .filter(cartao -> noPeriodo(cartao.getData(), inicio, fimExclusivo))
                .map(cartao -> cartao.getHoras().subtract(LIMITE_NORMAL).max(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
