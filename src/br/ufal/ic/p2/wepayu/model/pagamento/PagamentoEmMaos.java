package br.ufal.ic.p2.wepayu.model.pagamento;

public final class PagamentoEmMaos implements MetodoPagamento {
    private static final long serialVersionUID = 1L;

    @Override
    public String codigo() {
        return "emMaos";
    }

    @Override
    public String descricao(String endereco) {
        return "Em maos";
    }
}
