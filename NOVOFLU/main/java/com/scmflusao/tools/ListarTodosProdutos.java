package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.model.Produto;

import java.util.List;

public class ListarTodosProdutos {
    public static void main(String[] args) {
        ProdutoController produtoController = new ProdutoController();
        List<Produto> todos = produtoController.listarTodosProdutos();
        System.out.println("[ListarTodosProdutos] Total de produtos: " + todos.size());
        for (Produto p : todos) {
            System.out.println("ID=" + p.getId() + ", nome='" + p.getNome() + "'.");
        }
    }
}