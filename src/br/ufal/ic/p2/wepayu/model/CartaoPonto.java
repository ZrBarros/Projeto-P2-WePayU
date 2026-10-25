package br.ufal.ic.p2.wepayu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class CartaoPonto implements Serializable {
    private static final long serialVersionUID = 1L;

    private final LocalDate data;
    private final BigDecimal horas;

    public CartaoPonto(LocalDate data, BigDecimal horas) {
        this.data = Objects.requireNonNull(data, "data");
        if (horas == null || horas.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Horas devem ser positivas.");
        }
        this.horas = horas;
    }

    public LocalDate getData() {
        return data;
    }

    public BigDecimal getHoras() {
        return horas;
    }
}
