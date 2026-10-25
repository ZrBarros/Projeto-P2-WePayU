package br.ufal.ic.p2.wepayu.util;

import br.ufal.ic.p2.wepayu.exceptions.CampoObrigatorioException;
import br.ufal.ic.p2.wepayu.exceptions.DataInvalidaException;
import br.ufal.ic.p2.wepayu.exceptions.NumeroInvalidoException;
import br.ufal.ic.p2.wepayu.exceptions.TipoEmpregadoInvalidoException;
import br.ufal.ic.p2.wepayu.model.TipoEmpregado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public final class ConversorEntrada {
    private static final DateTimeFormatter FORMATO_DATA = new DateTimeFormatterBuilder()
            .appendPattern("d/M/uuuu")
            .toFormatter()
            .withResolverStyle(ResolverStyle.STRICT);

    private ConversorEntrada() {
    }

    public static String textoObrigatorio(String valor, String mensagem) throws CampoObrigatorioException {
        if (valor == null || valor.isEmpty()) {
            throw new CampoObrigatorioException(mensagem);
        }
        return valor;
    }

    public static BigDecimal salario(String valor) throws CampoObrigatorioException, NumeroInvalidoException {
        return naoNegativo(valor, "Salario nao pode ser nulo.", "Salario deve ser numerico.",
                "Salario deve ser nao-negativo.");
    }

    public static BigDecimal comissao(String valor) throws CampoObrigatorioException, NumeroInvalidoException {
        return naoNegativo(valor, "Comissao nao pode ser nula.", "Comissao deve ser numerica.",
                "Comissao deve ser nao-negativa.");
    }

    public static BigDecimal taxaSindical(String valor) throws CampoObrigatorioException, NumeroInvalidoException {
        return naoNegativo(valor, "Taxa sindical nao pode ser nula.", "Taxa sindical deve ser numerica.",
                "Taxa sindical deve ser nao-negativa.");
    }

    public static BigDecimal positivo(String valor, String mensagem) throws NumeroInvalidoException {
        BigDecimal numero;
        try {
            numero = new BigDecimal(valor.replace(',', '.'));
        } catch (RuntimeException excecao) {
            throw new NumeroInvalidoException(mensagem);
        }
        if (numero.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NumeroInvalidoException(mensagem);
        }
        return numero;
    }

    public static LocalDate data(String valor, String mensagem) throws DataInvalidaException {
        try {
            return LocalDate.parse(valor, FORMATO_DATA);
        } catch (DateTimeParseException | NullPointerException excecao) {
            throw new DataInvalidaException(mensagem);
        }
    }

    public static LocalDate[] periodo(String inicio, String fim) throws DataInvalidaException {
        LocalDate dataInicial = data(inicio, "Data inicial invalida.");
        LocalDate dataFinal = data(fim, "Data final invalida.");
        if (dataInicial.isAfter(dataFinal)) {
            throw new DataInvalidaException("Data inicial nao pode ser posterior aa data final.");
        }
        return new LocalDate[]{dataInicial, dataFinal};
    }

    public static TipoEmpregado tipo(String valor) throws TipoEmpregadoInvalidoException {
        return TipoEmpregado.porDescricao(valor);
    }

    private static BigDecimal naoNegativo(String texto, String mensagemNulo, String mensagemNumero,
                                          String mensagemNegativo)
            throws CampoObrigatorioException, NumeroInvalidoException {
        textoObrigatorio(texto, mensagemNulo);
        BigDecimal numero;
        try {
            numero = new BigDecimal(texto.replace(',', '.'));
        } catch (NumberFormatException excecao) {
            throw new NumeroInvalidoException(mensagemNumero);
        }
        if (numero.compareTo(BigDecimal.ZERO) < 0) {
            throw new NumeroInvalidoException(mensagemNegativo);
        }
        return numero;
    }
}
