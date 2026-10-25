package br.ufal.ic.p2.wepayu.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Formatador {
    private Formatador() {
    }

    public static BigDecimal centavos(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.DOWN);
    }

    public static String dinheiro(BigDecimal valor) {
        return centavos(valor).toPlainString().replace('.', ',');
    }

    public static String numero(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString().replace('.', ',');
    }
}
