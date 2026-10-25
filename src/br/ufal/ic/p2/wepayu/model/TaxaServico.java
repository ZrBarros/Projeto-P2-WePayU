package br.ufal.ic.p2.wepayu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class TaxaServico implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate data;
    private final BigDecimal valor;

    public TaxaServico(LocalDate data, BigDecimal valor) {
        this.data = Objects.requireNonNull(data, "data");
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo.");
        }
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public BigDecimal getValor() {
        return valor;
    }
}
