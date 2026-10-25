package br.ufal.ic.p2.wepayu.model.agenda;

import br.ufal.ic.p2.wepayu.exceptions.AgendaPagamentoIndisponivelException;
import br.ufal.ic.p2.wepayu.exceptions.AgendaPagamentoJaExisteException;
import br.ufal.ic.p2.wepayu.exceptions.DescricaoAgendaInvalidaException;

import java.io.Serializable;
import java.util.LinkedHashMap;

public final class CatalogoAgendasPagamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LinkedHashMap<String, AgendaPagamento> agendas = new LinkedHashMap<>();

    public CatalogoAgendasPagamento() {
        adicionarPadrao(AgendasPadrao.horista());
        adicionarPadrao(AgendasPadrao.assalariado());
        adicionarPadrao(AgendasPadrao.comissionado());
    }

    public AgendaPagamento buscar(String descricao) throws AgendaPagamentoIndisponivelException {
        AgendaPagamento agenda = agendas.get(descricao);
        if (agenda == null) {
            throw new AgendaPagamentoIndisponivelException();
        }
        return agenda;
    }

    public void criar(String descricao)
            throws AgendaPagamentoJaExisteException, DescricaoAgendaInvalidaException {
        AgendaPagamento agenda = AgendaPagamentoFactory.criar(descricao);
        if (agendas.containsKey(agenda.descricao())) {
            throw new AgendaPagamentoJaExisteException();
        }
        agendas.put(agenda.descricao(), agenda);
    }

    private void adicionarPadrao(AgendaPagamento agenda) {
        agendas.put(agenda.descricao(), agenda);
    }
}
