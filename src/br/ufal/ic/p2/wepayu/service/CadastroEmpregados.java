package br.ufal.ic.p2.wepayu.service;

import br.ufal.ic.p2.wepayu.exceptions.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.exceptions.CampoObrigatorioException;
import br.ufal.ic.p2.wepayu.exceptions.RegraNegocioException;
import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.model.Empregado;
import br.ufal.ic.p2.wepayu.model.TipoEmpregado;
import br.ufal.ic.p2.wepayu.model.pagamento.DepositoBancario;
import br.ufal.ic.p2.wepayu.util.ConversorEntrada;
import br.ufal.ic.p2.wepayu.util.Formatador;

import java.math.BigDecimal;

final class CadastroEmpregados {
    String criar(EstadoSistema estado, String nome, String endereco, String tipo, String salario)
            throws WePayUException {
        TipoEmpregado tipoConvertido = ConversorEntrada.tipo(tipo);
        if (tipoConvertido == TipoEmpregado.COMISSIONADO) {
            throw new RegraNegocioException("Tipo nao aplicavel.");
        }
        return adicionar(estado, nome, endereco, tipoConvertido, salario, null);
    }

    String criar(EstadoSistema estado, String nome, String endereco, String tipo, String salario,
                 String comissao) throws WePayUException {
        TipoEmpregado tipoConvertido = ConversorEntrada.tipo(tipo);
        if (tipoConvertido != TipoEmpregado.COMISSIONADO) {
            throw new RegraNegocioException("Tipo nao aplicavel.");
        }
        return adicionar(estado, nome, endereco, tipoConvertido, salario, comissao);
    }

    void remover(EstadoSistema estado, String id) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        estado.remover(empregado.getId());
    }

    String buscarPorNome(EstadoSistema estado, String nome, String indice) throws WePayUException {
        int posicao;
        try {
            posicao = Integer.parseInt(indice);
        } catch (NumberFormatException excecao) {
            throw new RegraNegocioException("Nao ha empregado com esse nome.");
        }

        int atual = 0;
        for (Empregado empregado : estado.listarEmpregados()) {
            if (empregado.getNome().contains(nome) && ++atual == posicao) {
                return empregado.getId();
            }
        }
        throw new RegraNegocioException("Nao ha empregado com esse nome.");
    }

    String consultarAtributo(EstadoSistema estado, String id, String atributo) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        return switch (atributo) {
            case "nome" -> empregado.getNome();
            case "endereco" -> empregado.getEndereco();
            case "tipo" -> empregado.getTipo().descricao();
            case "salario" -> Formatador.dinheiro(empregado.getSalario());
            case "comissao" -> Formatador.dinheiro(empregado.getComissao());
            case "metodoPagamento" -> empregado.getMetodoPagamento();
            case "banco" -> empregado.getBanco();
            case "agencia" -> empregado.getAgencia();
            case "contaCorrente" -> empregado.getContaCorrente();
            case "sindicalizado" -> Boolean.toString(empregado.isSindicalizado());
            case "idSindicato" -> empregado.getIdSindicato();
            case "taxaSindical" -> Formatador.dinheiro(empregado.getTaxaSindical());
            default -> throw new AtributoNaoExisteException();
        };
    }

    void alterar(EstadoSistema estado, String id, String atributo, String valor) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        switch (atributo) {
            case "nome" -> empregado.alterarNome(
                    ConversorEntrada.textoObrigatorio(valor, "Nome nao pode ser nulo."));
            case "endereco" -> empregado.alterarEndereco(
                    ConversorEntrada.textoObrigatorio(valor, "Endereco nao pode ser nulo."));
            case "tipo" -> alterarTipoSemValor(estado, empregado, valor);
            case "salario" -> empregado.alterarSalario(ConversorEntrada.salario(valor));
            case "comissao" -> empregado.alterarComissao(ConversorEntrada.comissao(valor));
            case "metodoPagamento" -> alterarMetodoSimples(empregado, valor);
            case "sindicalizado" -> alterarSindicalizacaoSimples(empregado, valor);
            default -> throw new AtributoNaoExisteException();
        }
    }

    void alterarTipo(EstadoSistema estado, String id, String atributo, String tipo, String adicional)
            throws WePayUException {
        Empregado atual = estado.buscar(id);
        if (!"tipo".equals(atributo)) {
            throw new AtributoNaoExisteException();
        }

        TipoEmpregado novoTipo = ConversorEntrada.tipo(tipo);
        BigDecimal salario = atual.getSalario();
        BigDecimal comissao = null;
        if (novoTipo == TipoEmpregado.COMISSIONADO) {
            comissao = ConversorEntrada.comissao(adicional);
        } else {
            salario = ConversorEntrada.salario(adicional);
        }
        estado.substituir(EmpregadoFactory.converter(atual, novoTipo, salario, comissao));
    }

    void alterarPagamentoBancario(EstadoSistema estado, String id, String atributo, String metodo,
                                  String banco, String agencia, String contaCorrente) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        if (!"metodoPagamento".equals(atributo) || !"banco".equals(metodo)) {
            throw new RegraNegocioException("Metodo de pagamento invalido.");
        }
        String bancoValidado = ConversorEntrada.textoObrigatorio(banco, "Banco nao pode ser nulo.");
        String agenciaValidada = ConversorEntrada.textoObrigatorio(agencia, "Agencia nao pode ser nulo.");
        String contaValidada = ConversorEntrada.textoObrigatorio(contaCorrente,
                "Conta corrente nao pode ser nulo.");
        empregado.definirMetodoPagamento(new DepositoBancario(bancoValidado, agenciaValidada, contaValidada));
    }

    private String adicionar(EstadoSistema estado, String nome, String endereco, TipoEmpregado tipo,
                             String salario, String comissao) throws WePayUException {
        String nomeValidado = ConversorEntrada.textoObrigatorio(nome, "Nome nao pode ser nulo.");
        String enderecoValidado = ConversorEntrada.textoObrigatorio(endereco, "Endereco nao pode ser nulo.");
        BigDecimal salarioValidado = ConversorEntrada.salario(salario);
        BigDecimal comissaoValidada = comissao == null ? null : ConversorEntrada.comissao(comissao);
        String id = estado.consultarProximoId();
        estado.adicionar(EmpregadoFactory.criar(id, nomeValidado, enderecoValidado, tipo,
                salarioValidado, comissaoValidada));
        return id;
    }

    private void alterarTipoSemValor(EstadoSistema estado, Empregado atual, String tipo)
            throws WePayUException {
        TipoEmpregado novoTipo = ConversorEntrada.tipo(tipo);
        if (novoTipo == TipoEmpregado.COMISSIONADO) {
            throw new CampoObrigatorioException("Comissao nao pode ser nula.");
        }
        estado.substituir(EmpregadoFactory.converter(atual, novoTipo, atual.getSalario(), null));
    }

    private void alterarMetodoSimples(Empregado empregado, String metodo) throws RegraNegocioException {
        if ("emMaos".equals(metodo)) {
            empregado.pagarEmMaos();
        } else if ("correios".equals(metodo)) {
            empregado.pagarPelosCorreios();
        } else {
            throw new RegraNegocioException("Metodo de pagamento invalido.");
        }
    }

    private void alterarSindicalizacaoSimples(Empregado empregado, String valor) throws WePayUException {
        validarBooleano(valor);
        if ("true".equals(valor)) {
            throw new CampoObrigatorioException("Identificacao do sindicato nao pode ser nula.");
        }
        empregado.dessindicalizar();
    }

    static void validarBooleano(String valor) throws RegraNegocioException {
        if (!"true".equals(valor) && !"false".equals(valor)) {
            throw new RegraNegocioException("Valor deve ser true ou false.");
        }
    }
}
