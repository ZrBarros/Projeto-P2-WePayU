package br.ufal.ic.p2.wepayu.service;

import br.ufal.ic.p2.wepayu.exceptions.AtributoNaoExisteException;
import br.ufal.ic.p2.wepayu.exceptions.RegraNegocioException;
import br.ufal.ic.p2.wepayu.exceptions.WePayUException;
import br.ufal.ic.p2.wepayu.model.Empregado;
import br.ufal.ic.p2.wepayu.model.FiliacaoSindical;
import br.ufal.ic.p2.wepayu.model.TaxaServico;
import br.ufal.ic.p2.wepayu.util.ConversorEntrada;
import br.ufal.ic.p2.wepayu.util.Formatador;

import java.math.BigDecimal;
import java.time.LocalDate;

final class ServicoSindical {
    void alterarFiliacao(EstadoSistema estado, String id, String atributo, String valor,
                         String idSindicato, String taxaSindical) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        if (!"sindicalizado".equals(atributo)) {
            throw new AtributoNaoExisteException();
        }
        CadastroEmpregados.validarBooleano(valor);
        if ("false".equals(valor)) {
            empregado.dessindicalizar();
            return;
        }

        String identificacao = ConversorEntrada.textoObrigatorio(idSindicato,
                "Identificacao do sindicato nao pode ser nula.");
        BigDecimal taxa = ConversorEntrada.taxaSindical(taxaSindical);
        garantirIdentificacaoUnica(estado, id, identificacao);
        empregado.sindicalizar(new FiliacaoSindical(identificacao, taxa));
    }

    void lancarTaxa(EstadoSistema estado, String membro, String data, String valor) throws WePayUException {
        String identificacao = ConversorEntrada.textoObrigatorio(membro,
                "Identificacao do membro nao pode ser nula.");
        LocalDate dia = ConversorEntrada.data(data, "Data invalida.");
        BigDecimal montante = ConversorEntrada.positivo(valor, "Valor deve ser positivo.");
        localizarMembro(estado, identificacao).lancarTaxaServico(new TaxaServico(dia, montante));
    }

    String consultarTaxas(EstadoSistema estado, String id, String inicio, String fim) throws WePayUException {
        Empregado empregado = estado.buscar(id);
        LocalDate[] periodo = ConversorEntrada.periodo(inicio, fim);
        return Formatador.dinheiro(empregado.taxasServico(periodo[0], periodo[1]));
    }

    private void garantirIdentificacaoUnica(EstadoSistema estado, String idEmpregado, String idSindicato)
            throws RegraNegocioException {
        for (Empregado empregado : estado.listarEmpregados()) {
            if (!empregado.getId().equals(idEmpregado) && empregado.isSindicalizado()
                    && idSindicato.equals(empregado.getIdSindicato())) {
                throw new RegraNegocioException("Ha outro empregado com esta identificacao de sindicato");
            }
        }
    }

    private Empregado localizarMembro(EstadoSistema estado, String identificacao) throws WePayUException {
        for (Empregado empregado : estado.listarEmpregados()) {
            if (empregado.isSindicalizado() && identificacao.equals(empregado.getIdSindicato())) {
                return empregado;
            }
        }
        throw new RegraNegocioException("Membro nao existe.");
    }
}
