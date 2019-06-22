package br.ce.wcaquino.servicos;

import br.ce.wcaquino.entidades.Usuario;

public interface EmailsService {

    void notificarAtraso(Usuario usuario);

}
