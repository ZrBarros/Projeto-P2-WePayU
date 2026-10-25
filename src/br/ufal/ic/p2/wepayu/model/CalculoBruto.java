package br.ufal.ic.p2.wepayu.model;

import java.math.BigDecimal;

public final class CalculoBruto {
    private final BigDecimal horasNormais;
    private final BigDecimal horasExtras;
    private final BigDecimal fixo;
    private final BigDecimal vendas;
    private final BigDecimal comissao;
    private final BigDecimal bruto;

    public CalculoBruto(BigDecimal horasNormais, BigDecimal horasExtras, BigDecimal fixo,
                        BigDecimal vendas, BigDecimal comissao, BigDecimal bruto) {
        this.horasNormais = horasNormais;
        this.horasExtras = horasExtras;
        this.fixo = fixo;
        this.vendas = vendas;
        this.comissao = comissao;
        this.bruto = bruto;
    }

    public BigDecimal getHorasNormais() {
        return horasNormais;
    }

    public BigDecimal getHorasExtras() {
        return horasExtras;
    }

    public BigDecimal getFixo() {
        return fixo;
    }

    public BigDecimal getVendas() {
        return vendas;
    }

    public BigDecimal getComissao() {
        return comissao;
    }

    public BigDecimal getBruto() {
        return bruto;
    }
}
