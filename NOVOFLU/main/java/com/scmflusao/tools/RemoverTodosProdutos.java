package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.model.Produto;

import java.util.List;

public class RemoverTodosProdutos {
    public static void main(String[] args) {
        System.out.println("[RemoverTodosProdutos] Listando todos os produtos para remoção...");

        ProdutoController produtoController = new ProdutoController();
        List<Produto> todos = produtoController.listarTodosProdutos();

        if (todos.isEmpty()) {
            System.out.println("Tabela já está vazia. Nenhum produto para remover.");
            return;
        }

        for (Produto p : todos) {
            System.out.println("Preparando remoção do produto ID=" + p.getId() + ", nome='" + p.getNome() + "'.");
            try {
                // Primeiro desassocia todos os itens (evitar erro de FK)
                new com.scmflusao.dao.impl.ProdutoDAOImpl().atualizarItensDoProduto(p.getId(), java.util.Collections.emptyList());
                // Agora remove o produto
                produtoController.removerProduto(p.getId());
                System.out.println("-> Removido com sucesso.");
            } catch (Exception e) {
                System.out.println("-> Falha ao remover ID=" + p.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        int restantes = produtoController.listarTodosProdutos().size();
        System.out.println("[RemoverTodosProdutos] Concluído. Produtos restantes na tabela: " + restantes);
    }
}