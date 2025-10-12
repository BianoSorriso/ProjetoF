package com.scmflusao.controller;

import com.scmflusao.view.LoginView;
import com.scmflusao.view.AppShell;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginViewListener {
    private final LoginView view;
    private final AuthController authController;

    public LoginViewListener(LoginView view) {
        this.view = view;
        this.authController = new AuthController();
        bind();
    }

    private void bind() {
        view.setEntrarListener(new EntrarListener());
        view.setCancelarListener(e -> System.exit(0));
    }

    private class EntrarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = view.getEmail();
            String senha = view.getSenha();
            var usuario = authController.login(email, senha);
            if (usuario != null) {
                view.dispose();
                SwingUtilities.invokeLater(() -> {
                    AppShell shell = new AppShell();
                    shell.setVisible(true);
                });
            } else {
                view.exibirMensagem("Credenciais inválidas ou usuário inativo.");
            }
        }
    }
}