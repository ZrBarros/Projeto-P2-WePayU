package br.ufal.ic.p2.wepayu.service;

import br.ufal.ic.p2.wepayu.exceptions.PersistenciaException;
import br.ufal.ic.p2.wepayu.exceptions.SistemaEncerradoException;
import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.persistence.PersistenciaSistema;
import br.ufal.ic.p2.wepayu.util.ConversorEntrada;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/** Coordena os casos de uso e mantem a fronteira de transacao do sistema. */
public final class SistemaFolha {
    private static final Path ARQUIVO_ESTADO = Path.of("wepayu-state.bin");

    private final PersistenciaSistema persistencia = new PersistenciaSistema(ARQUIVO_ESTADO);
    private final HistoricoEstados historico = new HistoricoEstados();
    private final CadastroEmpregados cadastro = new CadastroEmpregados();
    private final LancamentosEmpregado lancamentos = new LancamentosEmpregado();
    private final ServicoSindical sindicato = new ServicoSindical();
    private final FolhaPagamento folhaPagamento = new FolhaPagamento();
    private EstadoSistema estado;
    private boolean encerrado;

    public SistemaFolha() throws PersistenciaException {
        estado = persistencia.carregar();
    }

    public void zerarSistema() throws WePayUException {
        verificarAberto();
        EstadoSistema anterior = estado.copiar();
        estado = new EstadoSistema();
        historico.registrar(anterior);
    }

    public void encerrarSistema() throws WePayUException {
        verificarAberto();
        persistencia.salvar(estado);
        encerrado = true;
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario)
            throws WePayUException {
        return executarAlteracao(estadoAtual -> cadastro.criar(estadoAtual, nome, endereco, tipo, salario));
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao)
            throws WePayUException {
        return executarAlteracao(
                estadoAtual -> cadastro.criar(estadoAtual, nome, endereco, tipo, salario, comissao));
    }

    public void removerEmpregado(String id) throws WePayUException {
        executarAlteracao(estadoAtual -> {
            cadastro.remover(estadoAtual, id);
            return null;
        });
    }

    public int getNumeroDeEmpregados() throws WePayUException {
        verificarAberto();
        return estado.quantidadeEmpregados();
    }

    public String getEmpregadoPorNome(String nome, String indice) throws WePayUException {
        verificarAberto();
        return cadastro.buscarPorNome(estado, nome, indice);
    }

    public String getAtributoEmpregado(String id, String atributo) throws WePayUException {
        verificarAberto();
        return cadastro.consultarAtributo(estado, id, atributo);
    }

    public void alteraEmpregado(String id, String atributo, String valor) throws WePayUException {
        executarAlteracao(estadoAtual -> {
            cadastro.alterar(estadoAtual, id, atributo, valor);
            return null;
        });
    }

    public void alteraEmpregado(String id, String atributo, String valor, String adicional)
            throws WePayUException {
        executarAlteracao(estadoAtual -> {
            cadastro.alterarTipo(estadoAtual, id, atributo, valor, adicional);
            return null;
        });
    }

    public void alteraEmpregado(String id, String atributo, String valor, String idSindicato,
                                String taxaSindical) throws WePayUException {
        executarAlteracao(estadoAtual -> {
            sindicato.alterarFiliacao(estadoAtual, id, atributo, valor, idSindicato, taxaSindical);
            return null;
        });
    }

    public void alteraEmpregado(String id, String atributo, String valor, String banco, String agencia,
                                String contaCorrente) throws WePayUException {
        executarAlteracao(estadoAtual -> {
            cadastro.alterarPagamentoBancario(estadoAtual, id, atributo, valor, banco, agencia, contaCorrente);
            return null;
        });
    }

    public void lancaCartao(String id, String data, String horas) throws WePayUException {
        executarAlteracao(estadoAtual -> {
            lancamentos.lancarCartao(estadoAtual, id, data, horas);
            return null;
        });
    }

    public String getHorasNormaisTrabalhadas(String id, String inicio, String fim) throws WePayUException {
        verificarAberto();
        return lancamentos.horasNormais(estado, id, inicio, fim);
    }

    public String getHorasExtrasTrabalhadas(String id, String inicio, String fim) throws WePayUException {
        verificarAberto();
        return lancamentos.horasExtras(estado, id, inicio, fim);
    }

    public void lancaVenda(String id, String data, String valor) throws WePayUException {
        executarAlteracao(estadoAtual -> {
            lancamentos.lancarVenda(estadoAtual, id, data, valor);
            return null;
        });
    }

    public String getVendasRealizadas(String id, String inicio, String fim) throws WePayUException {
        verificarAberto();
        return lancamentos.vendas(estado, id, inicio, fim);
    }

    public void lancaTaxaServico(String membro, String data, String valor) throws WePayUException {
        executarAlteracao(estadoAtual -> {
            sindicato.lancarTaxa(estadoAtual, membro, data, valor);
            return null;
        });
    }

    public String getTaxasServico(String id, String inicio, String fim) throws WePayUException {
        verificarAberto();
        return sindicato.consultarTaxas(estado, id, inicio, fim);
    }

    public String totalFolha(String data) throws WePayUException {
        verificarAberto();
        LocalDate dia = ConversorEntrada.data(data, "Data invalida.");
        return folhaPagamento.total(estado, dia);
    }

    public void rodaFolha(String data, String arquivoSaida) throws WePayUException {
        LocalDate dia = ConversorEntrada.data(data, "Data invalida.");
        executarAlteracao(estadoAtual -> {
            escreverFolha(arquivoSaida, folhaPagamento.gerarRelatorio(estadoAtual, dia));
            return null;
        });
    }

    public void undo() throws WePayUException {
        verificarAberto();
        estado = historico.desfazer(estado);
    }

    public void redo() throws WePayUException {
        verificarAberto();
        estado = historico.refazer(estado);
    }

    private void escreverFolha(String arquivoSaida, String relatorio) throws PersistenciaException {
        try {
            Files.writeString(Path.of(arquivoSaida), relatorio, StandardCharsets.US_ASCII);
        } catch (IOException excecao) {
            throw new PersistenciaException("Nao foi possivel escrever a folha.", excecao);
        }
    }

    private <T> T executarAlteracao(Operacao<T> operacao) throws WePayUException {
        verificarAberto();
        EstadoSistema anterior = estado.copiar();
        try {
            T resultado = operacao.executar(estado);
            historico.registrar(anterior);
            return resultado;
        } catch (WePayUException | RuntimeException excecao) {
            estado = anterior;
            throw excecao;
        }
    }

    private void verificarAberto() throws SistemaEncerradoException {
        if (encerrado) {
            throw new SistemaEncerradoException();
        }
    }

    @FunctionalInterface
    private interface Operacao<T> {
        T executar(EstadoSistema estadoAtual) throws WePayUException;
    }
}
