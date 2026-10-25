package br.ufal.ic.p2.wepayu.service;

import br.ufal.ic.p2.wepayu.exceptions.EmpregadoNaoExisteException;
import br.ufal.ic.p2.wepayu.exceptions.IdentificacaoEmpregadoNulaException;
import br.ufal.ic.p2.wepayu.exceptions.AgendaPagamentoIndisponivelException;
import br.ufal.ic.p2.wepayu.exceptions.AgendaPagamentoJaExisteException;
import br.ufal.ic.p2.wepayu.exceptions.DescricaoAgendaInvalidaException;
import br.ufal.ic.p2.wepayu.model.Empregado;
import br.ufal.ic.p2.wepayu.model.agenda.AgendaPagamento;
import br.ufal.ic.p2.wepayu.model.agenda.CatalogoAgendasPagamento;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public final class EstadoSistema implements Serializable {
    private static final long serialVersionUID = 2L;

    private final LinkedHashMap<String, Empregado> empregados = new LinkedHashMap<>();
    private CatalogoAgendasPagamento catalogoAgendas = new CatalogoAgendasPagamento();
    private int proximoId = 1;

    public String consultarProximoId() {
        return Integer.toString(proximoId);
    }

    public void adicionar(Empregado empregado) {
        empregados.put(empregado.getId(), empregado);
        proximoId++;
    }

    public void substituir(Empregado empregado) {
        empregados.put(empregado.getId(), empregado);
    }

    public Empregado buscar(String id)
            throws IdentificacaoEmpregadoNulaException, EmpregadoNaoExisteException {
        if (id == null || id.isEmpty()) {
            throw new IdentificacaoEmpregadoNulaException();
        }
        Empregado empregado = empregados.get(id);
        if (empregado == null) {
            throw new EmpregadoNaoExisteException();
        }
        return empregado;
    }

    public void remover(String id) {
        empregados.remove(id);
    }

    public int quantidadeEmpregados() {
        return empregados.size();
    }

    public List<Empregado> listarEmpregados() {
        return List.copyOf(empregados.values());
    }

    public AgendaPagamento buscarAgendaPagamento(String descricao)
            throws AgendaPagamentoIndisponivelException {
        return catalogoAgendas().buscar(descricao);
    }

    public void criarAgendaPagamento(String descricao)
            throws AgendaPagamentoJaExisteException, DescricaoAgendaInvalidaException {
        catalogoAgendas().criar(descricao);
    }

    public EstadoSistema copiar() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream saida = new ObjectOutputStream(bytes)) {
                saida.writeObject(this);
            }
            try (ObjectInputStream entrada = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                return (EstadoSistema) entrada.readObject();
            }
        } catch (IOException | ClassNotFoundException excecao) {
            throw new IllegalStateException("Falha ao copiar o estado do sistema", excecao);
        }
    }

    private CatalogoAgendasPagamento catalogoAgendas() {
        if (catalogoAgendas == null) {
            catalogoAgendas = new CatalogoAgendasPagamento();
        }
        return catalogoAgendas;
    }
}
