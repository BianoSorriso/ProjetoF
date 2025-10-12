package com.scmflusao.auth;

import com.scmflusao.model.Usuario;
import com.scmflusao.model.Perfil;

public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioAtual;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void setUsuarioAtual(Usuario usuario) { this.usuarioAtual = usuario; }
    public Usuario getUsuarioAtual() { return usuarioAtual; }
    public void logout() { usuarioAtual = null; }

    public boolean hasPerfil(Perfil perfil) {
        return usuarioAtual != null && usuarioAtual.getPerfil() == perfil;
    }
}