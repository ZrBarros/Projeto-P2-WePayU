package br.ufal.ic.p2.wepayu.service;

import br.ufal.ic.p2.wepayu.exceptions.HistoricoException;

import java.util.ArrayDeque;
import java.util.Deque;

final class HistoricoEstados {
    private final Deque<EstadoSistema> estadosDesfeitos = new ArrayDeque<>();
    private final Deque<EstadoSistema> estadosParaRefazer = new ArrayDeque<>();

    void registrar(EstadoSistema estadoAnterior) {
        estadosDesfeitos.push(estadoAnterior);
        estadosParaRefazer.clear();
    }

    EstadoSistema desfazer(EstadoSistema estadoAtual) throws HistoricoException {
        if (estadosDesfeitos.isEmpty()) {
            throw new HistoricoException("Nao ha comando a desfazer.");
        }
        estadosParaRefazer.push(estadoAtual.copiar());
        return estadosDesfeitos.pop();
    }

    EstadoSistema refazer(EstadoSistema estadoAtual) throws HistoricoException {
        if (estadosParaRefazer.isEmpty()) {
            throw new HistoricoException("Nao ha comando a refazer.");
        }
        estadosDesfeitos.push(estadoAtual.copiar());
        return estadosParaRefazer.pop();
    }
}
