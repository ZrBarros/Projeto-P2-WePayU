package br.ufal.ic.p2.wepayu.model.pagamento;

public final class PagamentoCorreios implements MetodoPagamento {
    private static final long serialVersionUID = 1L;

    @Override
    public String codigo() {
        return "correios";
    }

    @Override
    public String descricao(String endereco) {
        return "Correios, " + endereco;
    }
}
