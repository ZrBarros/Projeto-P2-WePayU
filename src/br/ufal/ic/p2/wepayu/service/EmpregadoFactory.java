package br.ufal.ic.p2.wepayu.service;

import br.ufal.ic.p2.wepayu.model.Empregado;
import br.ufal.ic.p2.wepayu.model.EmpregadoAssalariado;
import br.ufal.ic.p2.wepayu.model.EmpregadoComissionado;
import br.ufal.ic.p2.wepayu.model.EmpregadoHorista;
import br.ufal.ic.p2.wepayu.model.TipoEmpregado;

import java.math.BigDecimal;

final class EmpregadoFactory {
    private EmpregadoFactory() {
    }

    static Empregado criar(String id, String nome, String endereco, TipoEmpregado tipo,
                           BigDecimal salario, BigDecimal comissao) {
        return switch (tipo) {
            case HORISTA -> new EmpregadoHorista(id, nome, endereco, salario);
            case ASSALARIADO -> new EmpregadoAssalariado(id, nome, endereco, salario);
            case COMISSIONADO -> new EmpregadoComissionado(id, nome, endereco, salario, comissao);
        };
    }

    static Empregado converter(Empregado atual, TipoEmpregado novoTipo, BigDecimal salario,
                                BigDecimal comissao) {
        Empregado convertido = criar(atual.getId(), atual.getNome(), atual.getEndereco(), novoTipo,
                salario, comissao);
        convertido.herdarDadosComuns(atual);
        return convertido;
    }
}
