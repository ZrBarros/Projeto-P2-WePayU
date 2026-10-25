package br.ufal.ic.p2.wepayu.persistence;

import br.ufal.ic.p2.wepayu.exceptions.PersistenciaException;
import br.ufal.ic.p2.wepayu.service.EstadoSistema;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PersistenciaSistema {
    private final Path arquivo;

    public PersistenciaSistema(Path arquivo) {
        this.arquivo = arquivo;
    }

    public EstadoSistema carregar() throws PersistenciaException {
        if (!Files.exists(arquivo)) {
            return new EstadoSistema();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(arquivo))) {
            return (EstadoSistema) entrada.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException excecao) {
            throw new PersistenciaException("Nao foi possivel carregar o sistema.", excecao);
        }
    }

    public void salvar(EstadoSistema estado) throws PersistenciaException {
        try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(arquivo))) {
            saida.writeObject(estado);
        } catch (IOException excecao) {
            throw new PersistenciaException("Nao foi possivel salvar o sistema.", excecao);
        }
    }
}
