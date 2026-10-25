package br.ufal.ic.p2.wepayu.service;

import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.model.CartaoPonto;
import br.ufal.ic.p2.wepayu.model.Empregado;
import br.ufal.ic.p2.wepayu.model.Venda;
import br.ufal.ic.p2.wepayu.util.ConversorEntrada;
import br.ufal.ic.p2.wepayu.util.Formatador;

import java.math.BigDecimal;
import java.time.LocalDate;

final class LancamentosEmpregado {
    void lancarCartao(EstadoSistema estado, String id, String data, String horas) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        LocalDate dia = ConversorEntrada.data(data, "Data invalida.");
        BigDecimal quantidade = ConversorEntrada.positivo(horas, "Horas devem ser positivas.");
        empregado.lancarCartao(new CartaoPonto(dia, quantidade));
    }

    String horasNormais(EstadoSistema estado, String id, String inicio, String fim) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        LocalDate[] periodo = ConversorEntrada.periodo(inicio, fim);
        return Formatador.numero(empregado.horasNormais(periodo[0], periodo[1]));
    }

    String horasExtras(EstadoSistema estado, String id, String inicio, String fim) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        LocalDate[] periodo = ConversorEntrada.periodo(inicio, fim);
        return Formatador.numero(empregado.horasExtras(periodo[0], periodo[1]));
    }

    void lancarVenda(EstadoSistema estado, String id, String data, String valor) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        LocalDate dia = ConversorEntrada.data(data, "Data invalida.");
        BigDecimal montante = ConversorEntrada.positivo(valor, "Valor deve ser positivo.");
        empregado.lancarVenda(new Venda(dia, montante));
    }

    String vendas(EstadoSistema estado, String id, String inicio, String fim) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        LocalDate[] periodo = ConversorEntrada.periodo(inicio, fim);
        return Formatador.dinheiro(empregado.vendasRealizadas(periodo[0], periodo[1]));
    }
}
