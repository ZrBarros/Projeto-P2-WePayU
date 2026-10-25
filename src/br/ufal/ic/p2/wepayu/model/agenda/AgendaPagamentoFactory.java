package br.ufal.ic.p2.wepayu.model.agenda;

import br.ufal.ic.p2.wepayu.exceptions.DescricaoAgendaInvalidaException;

public final class AgendaPagamentoFactory {
    private AgendaPagamentoFactory() {
    }

    public static AgendaPagamento criar(String descricao) throws DescricaoAgendaInvalidaException {
        if (descricao == null || descricao.isEmpty() || !descricao.equals(descricao.trim())) {
            throw new DescricaoAgendaInvalidaException();
        }

        String[] partes = descricao.split(" ", -1);
        if (partes.length == 2 && "mensal".equals(partes[0])) {
            return criarMensal(partes[1]);
        }
        if (partes.length == 2 && "semanal".equals(partes[0])) {
            return new AgendaSemanal(1, numeroNoIntervalo(partes[1], 1, 7));
        }
        if (partes.length == 3 && "semanal".equals(partes[0])) {
            int intervalo = numeroNoIntervalo(partes[1], 1, 52);
            int diaSemana = numeroNoIntervalo(partes[2], 1, 7);
            return new AgendaSemanal(intervalo, diaSemana);
        }
        throw new DescricaoAgendaInvalidaException();
    }

    private static AgendaPagamento criarMensal(String dia) throws DescricaoAgendaInvalidaException {
        if ("$".equals(dia)) {
            return new AgendaMensalUltimoDia();
        }
        return new AgendaMensal(numeroNoIntervalo(dia, 1, 28));
    }

    private static int numeroNoIntervalo(String valor, int minimo, int maximo)
            throws DescricaoAgendaInvalidaException {
        if (!valor.matches("\\d+")) {
            throw new DescricaoAgendaInvalidaException();
        }
        try {
            int numero = Integer.parseInt(valor);
            if (numero < minimo || numero > maximo) {
                throw new DescricaoAgendaInvalidaException();
            }
            return numero;
        } catch (NumberFormatException excecao) {
            throw new DescricaoAgendaInvalidaException();
        }
    }
}
