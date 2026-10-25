package br.ufal.ic.p2.wepayu.model.pagamento;

import java.util.Objects;

public final class DepositoBancario implements MetodoPagamento {
    private static final long serialVersionUID = 1L;

    private final String banco;
    private final String agencia;
    private final String contaCorrente;

    public DepositoBancario(String banco, String agencia, String contaCorrente) {
        this.banco = Objects.requireNonNull(banco, "banco");
        this.agencia = Objects.requireNonNull(agencia, "agencia");
        this.contaCorrente = Objects.requireNonNull(contaCorrente, "contaCorrente");
        if (banco.isEmpty() || agencia.isEmpty() || contaCorrente.isEmpty()) {
            throw new IllegalArgumentException("Dados bancarios incompletos.");
        }
    }

    @Override
    public String codigo() {
        return "banco";
    }

    @Override
    public String descricao(String endereco) {
        return banco + ", Ag. " + agencia + " CC " + contaCorrente;
    }

    @Override
    public String banco() {
        return banco;
    }

    @Override
    public String agencia() {
        return agencia;
    }

    @Override
    public String contaCorrente() {
        return contaCorrente;
    }
}
