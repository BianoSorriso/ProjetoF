package com.scmflusao.controller;

import com.scmflusao.view.RelatorioEstoqueView;

public class RelatorioEstoqueViewListener {
    private final RelatorioEstoqueView view;
    private final ArmazemController armazemController;

    public RelatorioEstoqueViewListener(RelatorioEstoqueView view) {
        this.view = view;
        this.armazemController = new ArmazemController();
        carregar();
    }

    private void carregar() {
        try {
            view.preencherArmazens(armazemController.listarTodosArmazens());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar relatório: " + e.getMessage());
        }
    }
}