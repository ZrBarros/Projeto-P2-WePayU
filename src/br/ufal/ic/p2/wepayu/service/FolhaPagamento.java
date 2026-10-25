package br.ufal.ic.p2.wepayu.service;

import br.ufal.ic.p2.wepayu.model.CalculoBruto;
import br.ufal.ic.p2.wepayu.model.Empregado;
import br.ufal.ic.p2.wepayu.model.TipoEmpregado;
import br.ufal.ic.p2.wepayu.util.Formatador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class FolhaPagamento {
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final String NOVA_LINHA = "\r\n";
    private static final String SEPARADOR = "=".repeat(127);

    public String total(EstadoSistema estado, LocalDate data) {
        BigDecimal total = ZERO;
        for (Empregado empregado : estado.listarEmpregados()) {
            if (empregado.deveReceber(data)) {
                total = total.add(calcular(empregado, data).bruto);
            }
        }
        return Formatador.dinheiro(total);
    }

    public String gerarRelatorio(EstadoSistema estado, LocalDate data) {
        List<Pagamento> horistas = pagamentos(estado, data, TipoEmpregado.HORISTA);
        List<Pagamento> assalariados = pagamentos(estado, data, TipoEmpregado.ASSALARIADO);
        List<Pagamento> comissionados = pagamentos(estado, data, TipoEmpregado.COMISSIONADO);

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("FOLHA DE PAGAMENTO DO DIA ").append(data).append(NOVA_LINHA);
        relatorio.append("====================================").append(NOVA_LINHA).append(NOVA_LINHA);
        imprimirHoristas(relatorio, horistas);
        imprimirAssalariados(relatorio, assalariados);
        imprimirComissionados(relatorio, comissionados);

        BigDecimal total = somarBruto(horistas)
                .add(somarBruto(assalariados))
                .add(somarBruto(comissionados));
        relatorio.append("TOTAL FOLHA: ").append(Formatador.dinheiro(total)).append(NOVA_LINHA);
        return relatorio.toString();
    }

    private List<Pagamento> pagamentos(EstadoSistema estado, LocalDate data, TipoEmpregado tipo) {
        List<Pagamento> pagamentos = new ArrayList<>();
        for (Empregado empregado : estado.listarEmpregados()) {
            if (empregado.getTipo() == tipo && empregado.deveReceber(data)) {
                pagamentos.add(calcular(empregado, data));
            }
        }
        pagamentos.sort(Comparator.comparing(pagamento -> pagamento.empregado.getNome()));
        return pagamentos;
    }

    private Pagamento calcular(Empregado empregado, LocalDate dataPagamento) {
        CalculoBruto calculo = empregado.calcularBruto(dataPagamento);
        Pagamento pagamento = new Pagamento(empregado, calculo);
        if (pagamento.bruto.compareTo(BigDecimal.ZERO) > 0) {
            LocalDate inicio = empregado.inicioDescontos(dataPagamento);
            LocalDate fimExclusivo = dataPagamento.plusDays(1);
            pagamento.descontos = Formatador.centavos(empregado.calcularDescontos(inicio, fimExclusivo));
        }
        pagamento.liquido = Formatador.centavos(
                pagamento.bruto.subtract(pagamento.descontos).max(BigDecimal.ZERO));
        return pagamento;
    }

    private void imprimirHoristas(StringBuilder relatorio, List<Pagamento> pagamentos) {
        relatorio.append(SEPARADOR).append(NOVA_LINHA)
                .append("===================== HORISTAS ================================================================================================")
                .append(NOVA_LINHA)
                .append(SEPARADOR).append(NOVA_LINHA)
                .append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo")
                .append(NOVA_LINHA)
                .append("==================================== ===== ===== ============= ========= =============== ======================================")
                .append(NOVA_LINHA);
        for (Pagamento pagamento : pagamentos) {
            relatorio.append(String.format(Locale.ROOT, "%-36s %5s %5s %13s %9s %15s %s",
                    pagamento.empregado.getNome(), Formatador.numero(pagamento.horasNormais),
                    Formatador.numero(pagamento.horasExtras), Formatador.dinheiro(pagamento.bruto),
                    Formatador.dinheiro(pagamento.descontos), Formatador.dinheiro(pagamento.liquido),
                    pagamento.empregado.descricaoPagamento())).append(NOVA_LINHA);
        }
        relatorio.append(NOVA_LINHA);
        BigDecimal horas = BigDecimal.ZERO;
        BigDecimal extras = BigDecimal.ZERO;
        for (Pagamento pagamento : pagamentos) {
            horas = horas.add(pagamento.horasNormais);
            extras = extras.add(pagamento.horasExtras);
        }
        relatorio.append(String.format(Locale.ROOT, "%-36s %5s %5s %13s %9s %15s", "TOTAL HORISTAS",
                Formatador.numero(horas), Formatador.numero(extras), Formatador.dinheiro(somarBruto(pagamentos)),
                Formatador.dinheiro(somarDescontos(pagamentos)), Formatador.dinheiro(somarLiquido(pagamentos))))
                .append(NOVA_LINHA).append(NOVA_LINHA);
    }

    private void imprimirAssalariados(StringBuilder relatorio, List<Pagamento> pagamentos) {
        relatorio.append(SEPARADOR).append(NOVA_LINHA)
                .append("===================== ASSALARIADOS ============================================================================================")
                .append(NOVA_LINHA)
                .append(SEPARADOR).append(NOVA_LINHA)
                .append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo")
                .append(NOVA_LINHA)
                .append("================================================ ============= ========= =============== ======================================")
                .append(NOVA_LINHA);
        for (Pagamento pagamento : pagamentos) {
            relatorio.append(String.format(Locale.ROOT, "%-48s %13s %9s %15s %s",
                    pagamento.empregado.getNome(), Formatador.dinheiro(pagamento.bruto),
                    Formatador.dinheiro(pagamento.descontos), Formatador.dinheiro(pagamento.liquido),
                    pagamento.empregado.descricaoPagamento())).append(NOVA_LINHA);
        }
        relatorio.append(NOVA_LINHA);
        relatorio.append(String.format(Locale.ROOT, "%-48s %13s %9s %15s", "TOTAL ASSALARIADOS",
                Formatador.dinheiro(somarBruto(pagamentos)), Formatador.dinheiro(somarDescontos(pagamentos)),
                Formatador.dinheiro(somarLiquido(pagamentos))))
                .append(NOVA_LINHA).append(NOVA_LINHA);
    }

    private void imprimirComissionados(StringBuilder relatorio, List<Pagamento> pagamentos) {
        relatorio.append(SEPARADOR).append(NOVA_LINHA)
                .append("===================== COMISSIONADOS ===========================================================================================")
                .append(NOVA_LINHA)
                .append(SEPARADOR).append(NOVA_LINHA)
                .append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo")
                .append(NOVA_LINHA)
                .append("===================== ======== ======== ======== ============= ========= =============== ======================================")
                .append(NOVA_LINHA);
        for (Pagamento pagamento : pagamentos) {
            relatorio.append(String.format(Locale.ROOT, "%-21s %8s %8s %8s %13s %9s %15s %s",
                    pagamento.empregado.getNome(), Formatador.dinheiro(pagamento.fixo),
                    Formatador.dinheiro(pagamento.vendas), Formatador.dinheiro(pagamento.comissao),
                    Formatador.dinheiro(pagamento.bruto), Formatador.dinheiro(pagamento.descontos),
                    Formatador.dinheiro(pagamento.liquido), pagamento.empregado.descricaoPagamento()))
                    .append(NOVA_LINHA);
        }
        relatorio.append(NOVA_LINHA);
        BigDecimal fixo = ZERO;
        BigDecimal vendas = ZERO;
        BigDecimal comissao = ZERO;
        for (Pagamento pagamento : pagamentos) {
            fixo = fixo.add(pagamento.fixo);
            vendas = vendas.add(pagamento.vendas);
            comissao = comissao.add(pagamento.comissao);
        }
        relatorio.append(String.format(Locale.ROOT, "%-21s %8s %8s %8s %13s %9s %15s", "TOTAL COMISSIONADOS",
                Formatador.dinheiro(fixo), Formatador.dinheiro(vendas), Formatador.dinheiro(comissao),
                Formatador.dinheiro(somarBruto(pagamentos)), Formatador.dinheiro(somarDescontos(pagamentos)),
                Formatador.dinheiro(somarLiquido(pagamentos))))
                .append(NOVA_LINHA).append(NOVA_LINHA);
    }

    private BigDecimal somarBruto(List<Pagamento> pagamentos) {
        return pagamentos.stream().map(pagamento -> pagamento.bruto).reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal somarDescontos(List<Pagamento> pagamentos) {
        return pagamentos.stream().map(pagamento -> pagamento.descontos).reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal somarLiquido(List<Pagamento> pagamentos) {
        return pagamentos.stream().map(pagamento -> pagamento.liquido).reduce(ZERO, BigDecimal::add);
    }

    private static final class Pagamento {
        private final Empregado empregado;
        private final BigDecimal horasNormais;
        private final BigDecimal horasExtras;
        private final BigDecimal fixo;
        private final BigDecimal vendas;
        private final BigDecimal comissao;
        private final BigDecimal bruto;
        private BigDecimal descontos = ZERO;
        private BigDecimal liquido = ZERO;

        private Pagamento(Empregado empregado, CalculoBruto calculo) {
            this.empregado = empregado;
            this.horasNormais = calculo.getHorasNormais();
            this.horasExtras = calculo.getHorasExtras();
            this.fixo = calculo.getFixo();
            this.vendas = calculo.getVendas();
            this.comissao = calculo.getComissao();
            this.bruto = calculo.getBruto();
        }
    }
}
