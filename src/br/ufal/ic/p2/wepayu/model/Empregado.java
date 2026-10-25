package br.ufal.ic.p2.wepayu.model;

import br.ufal.ic.p2.wepayu.exceptions.RegraNegocioException;
import br.ufal.ic.p2.wepayu.model.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.model.pagamento.MetodoPagamento;
import br.ufal.ic.p2.wepayu.model.pagamento.PagamentoCorreios;
import br.ufal.ic.p2.wepayu.model.pagamento.PagamentoEmMaos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public abstract class Empregado implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String nome;
    private String endereco;
    private BigDecimal salario;
    private AgendaPagamento agendaPagamento;
    private MetodoPagamento metodoPagamento = new PagamentoEmMaos();
    private FiliacaoSindical filiacaoSindical;

    protected Empregado(String id, String nome, String endereco, BigDecimal salario,
                        AgendaPagamento agendaPagamento) {
        this.id = textoValido(id, "Identificacao do empregado nao pode ser nula.");
        this.nome = textoValido(nome, "Nome nao pode ser nulo.");
        this.endereco = textoValido(endereco, "Endereco nao pode ser nulo.");
        this.salario = valorNaoNegativo(salario, "Salario deve ser nao-negativo.");
        this.agendaPagamento = Objects.requireNonNull(agendaPagamento, "agendaPagamento");
    }

    public abstract TipoEmpregado getTipo();

    public abstract CalculoBruto calcularBruto(LocalDate pagamento);

    protected abstract AgendaPagamento agendaPadrao();

    public boolean deveReceber(LocalDate data) {
        return agendaPagamento().devePagar(data);
    }

    public LocalDate inicioPeriodo(LocalDate pagamento) {
        return agendaPagamento().inicioPeriodo(pagamento);
    }

    public LocalDate inicioDescontos(LocalDate pagamento) {
        return inicioPeriodo(pagamento);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void alterarNome(String nome) throws RegraNegocioException {
        if (nome == null || nome.isEmpty()) {
            throw new RegraNegocioException("Nome nao pode ser nulo.");
        }
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void alterarEndereco(String endereco) throws RegraNegocioException {
        if (endereco == null || endereco.isEmpty()) {
            throw new RegraNegocioException("Endereco nao pode ser nulo.");
        }
        this.endereco = endereco;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public String getAgendaPagamento() {
        return agendaPagamento().descricao();
    }

    public void alterarAgendaPagamento(AgendaPagamento agendaPagamento) {
        this.agendaPagamento = Objects.requireNonNull(agendaPagamento, "agendaPagamento");
    }

    public void alterarSalario(BigDecimal salario) throws RegraNegocioException {
        if (salario == null || salario.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("Salario deve ser nao-negativo.");
        }
        this.salario = salario;
    }

    public BigDecimal getComissao() throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao eh comissionado.");
    }

    public void alterarComissao(BigDecimal comissao) throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao eh comissionado.");
    }

    public void lancarCartao(CartaoPonto cartao) throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao eh horista.");
    }

    public BigDecimal horasNormais(LocalDate inicio, LocalDate fimExclusivo) throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao eh horista.");
    }

    public BigDecimal horasExtras(LocalDate inicio, LocalDate fimExclusivo) throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao eh horista.");
    }

    public void lancarVenda(Venda venda) throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao eh comissionado.");
    }

    public BigDecimal vendasRealizadas(LocalDate inicio, LocalDate fimExclusivo) throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao eh comissionado.");
    }

    public String getMetodoPagamento() {
        return metodoPagamento.codigo();
    }

    public String descricaoPagamento() {
        return metodoPagamento.descricao(endereco);
    }

    public String getBanco() throws RegraNegocioException {
        return metodoPagamento.banco();
    }

    public String getAgencia() throws RegraNegocioException {
        return metodoPagamento.agencia();
    }

    public String getContaCorrente() throws RegraNegocioException {
        return metodoPagamento.contaCorrente();
    }

    public void definirMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = Objects.requireNonNull(metodoPagamento, "metodoPagamento");
    }

    public void pagarEmMaos() {
        definirMetodoPagamento(new PagamentoEmMaos());
    }

    public void pagarPelosCorreios() {
        definirMetodoPagamento(new PagamentoCorreios());
    }

    public boolean isSindicalizado() {
        return filiacaoSindical != null;
    }

    public void sindicalizar(FiliacaoSindical filiacaoSindical) {
        this.filiacaoSindical = Objects.requireNonNull(filiacaoSindical, "filiacaoSindical");
    }

    public void dessindicalizar() {
        filiacaoSindical = null;
    }

    public String getIdSindicato() throws RegraNegocioException {
        return filiacao().getIdentificacao();
    }

    public BigDecimal getTaxaSindical() throws RegraNegocioException {
        return filiacao().getTaxaDiaria();
    }

    public void lancarTaxaServico(TaxaServico taxaServico) throws RegraNegocioException {
        filiacao().lancarTaxaServico(taxaServico);
    }

    public BigDecimal taxasServico(LocalDate inicio, LocalDate fimExclusivo) throws RegraNegocioException {
        return filiacao().totalTaxasServico(inicio, fimExclusivo);
    }

    public BigDecimal calcularDescontos(LocalDate inicio, LocalDate fimExclusivo) {
        if (!isSindicalizado()) {
            return BigDecimal.ZERO;
        }
        return filiacaoSindical.calcularDescontos(inicio, fimExclusivo);
    }

    public void herdarDadosComuns(Empregado origem) {
        metodoPagamento = origem.metodoPagamento;
        filiacaoSindical = origem.filiacaoSindical;
        agendaPagamento = origem.agendaPagamento();
    }

    protected BigDecimal salarioDoPeriodo() {
        return agendaPagamento().salarioDoPeriodo(salario);
    }

    protected LocalDate pagamentoAnterior(LocalDate pagamento) {
        return agendaPagamento().pagamentoAnterior(pagamento);
    }

    protected boolean noPeriodo(LocalDate data, LocalDate inicio, LocalDate fimExclusivo) {
        return !data.isBefore(inicio) && data.isBefore(fimExclusivo);
    }

    private FiliacaoSindical filiacao() throws RegraNegocioException {
        if (filiacaoSindical == null) {
            throw new RegraNegocioException("Empregado nao eh sindicalizado.");
        }
        return filiacaoSindical;
    }

    private AgendaPagamento agendaPagamento() {
        if (agendaPagamento == null) {
            agendaPagamento = agendaPadrao();
        }
        return agendaPagamento;
    }

    private static String textoValido(String valor, String mensagem) {
        if (valor == null || valor.isEmpty()) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }

    private static BigDecimal valorNaoNegativo(BigDecimal valor, String mensagem) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(mensagem);
        }
        return valor;
    }
}
