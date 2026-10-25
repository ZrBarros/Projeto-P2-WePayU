package br.ufal.ic.p2.wepayu.model;

import br.ufal.ic.p2.wepayu.exceptions.TipoEmpregadoInvalidoException;

public enum TipoEmpregado {
    HORISTA("horista"),
    ASSALARIADO("assalariado"),
    COMISSIONADO("comissionado");

    private final String descricao;

    TipoEmpregado(String descricao) {
        this.descricao = descricao;
    }

    public String descricao() {
        return descricao;
    }

    public static TipoEmpregado porDescricao(String descricao) throws TipoEmpregadoInvalidoException {
        for (TipoEmpregado tipo : values()) {
            if (tipo.descricao.equals(descricao)) {
                return tipo;
            }
        }
        throw new TipoEmpregadoInvalidoException();
    }
}
