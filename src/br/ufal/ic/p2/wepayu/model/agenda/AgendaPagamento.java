package br.ufal.ic.p2.wepayu.model.agenda;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface AgendaPagamento extends Serializable {
    String descricao();

    boolean devePagar(LocalDate data);

    LocalDate inicioPeriodo(LocalDate pagamento);

    LocalDate pagamentoAnterior(LocalDate pagamento);

    BigDecimal salarioDoPeriodo(BigDecimal salarioMensal);
}
