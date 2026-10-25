package br.ufal.ic.p2.wepayu.model.pagamento;

import br.ufal.ic.p2.wepayu.exceptions.RegraNegocioException;

import java.io.Serializable;

public interface MetodoPagamento extends Serializable {
    String codigo();

    String descricao(String endereco);

    default String banco() throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao recebe em banco.");
    }

    default String agencia() throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao recebe em banco.");
    }

    default String contaCorrente() throws RegraNegocioException {
        throw new RegraNegocioException("Empregado nao recebe em banco.");
    }
}
