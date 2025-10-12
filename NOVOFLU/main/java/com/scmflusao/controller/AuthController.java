package com.scmflusao.controller;

import com.scmflusao.auth.SessionManager;
import com.scmflusao.model.Perfil;
import com.scmflusao.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class AuthController {
    private final List<Usuario> usuarios;

    public AuthController() {
        this.usuarios = new ArrayList<>();
        seed();
    }

    private void seed() {
        usuarios.add(new Usuario(1L, "Admin", "admin@scm.com", "123456", Perfil.ADMIN, true));
        usuarios.add(new Usuario(2L, "Operador", "operador@scm.com", "123456", Perfil.OPERADOR, true));
        usuarios.add(new Usuario(3L, "Parceiro", "parceiro@scm.com", "123456", Perfil.PARCEIRO, true));
    }

    public Usuario login(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.isAtivo() && u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                SessionManager.getInstance().setUsuarioAtual(u);
                return u;
            }
        }
        return null;
    }
}