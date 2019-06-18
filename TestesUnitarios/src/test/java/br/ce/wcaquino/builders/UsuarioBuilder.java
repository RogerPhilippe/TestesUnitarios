package br.ce.wcaquino.builders;

import br.ce.wcaquino.entidades.Usuario;

public class UsuarioBuilder {

    private Usuario mUsuario;

    private UsuarioBuilder() {}

    public static UsuarioBuilder umUsuario() {

        UsuarioBuilder builder = new UsuarioBuilder();
        builder.mUsuario = new Usuario();
        builder.mUsuario.setNome("Usuário 1");
        return builder;

    }

    public Usuario agora() {
        return mUsuario;
    }

}
