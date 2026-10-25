package br.ufal.ic.p2.wepayu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

public final class FiliacaoSindical implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String identificacao;
    private final BigDecimal taxaDiaria;
    private final ArrayList<TaxaServico> taxasServico = new ArrayList<>();

    public FiliacaoSindical(String identificacao, BigDecimal taxaDiaria) {
        this.identificacao = Objects.requireNonNull(identificacao, "identificacao");
        if (identificacao.isEmpty()) {
            throw new IllegalArgumentException("Identificacao do sindicato nao pode ser nula.");
        }
        if (taxaDiaria == null || taxaDiaria.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa sindical deve ser nao-negativa.");
        }
        this.taxaDiaria = taxaDiaria;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public BigDecimal getTaxaDiaria() {
        return taxaDiaria;
    }

    public void lancarTaxaServico(TaxaServico taxaServico) {
        taxasServico.add(Objects.requireNonNull(taxaServico, "taxaServico"));
    }

    public BigDecimal totalTaxasServico(LocalDate inicio, LocalDate fimExclusivo) {
        return taxasServico.stream()
                .filter(taxa -> noPeriodo(taxa.getData(), inicio, fimExclusivo))
                .map(TaxaServico::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularDescontos(LocalDate inicio, LocalDate fimExclusivo) {
        long dias = ChronoUnit.DAYS.between(inicio, fimExclusivo);
        return taxaDiaria.multiply(BigDecimal.valueOf(dias))
                .add(totalTaxasServico(inicio, fimExclusivo));
    }

    private boolean noPeriodo(LocalDate data, LocalDate inicio, LocalDate fimExclusivo) {
        return !data.isBefore(inicio) && data.isBefore(fimExclusivo);
    }
}
