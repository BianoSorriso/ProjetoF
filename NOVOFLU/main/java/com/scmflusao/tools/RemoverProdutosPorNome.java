package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.model.Produto;

import java.util.List;

public class RemoverProdutosPorNome {
    public static void main(String[] args) {
        String nome = args.length > 0 ? args[0] : "Computador";
        System.out.println("[RemoverProdutosPorNome] Procurando produtos com nome: " + nome);

        ProdutoController produtoController = new ProdutoController();
        List<Produto> produtos = produtoController.buscarProdutosPorNome(nome);

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado com o nome '" + nome + "'. Nada a remover.");
            return;
        }

        for (Produto p : produtos) {
            System.out.println("Removendo produto ID=" + p.getId() + ", nome='" + p.getNome() + "'.");
            try {
                produtoController.removerProduto(p.getId());
                System.out.println("-> Removido com sucesso.");
            } catch (Exception e) {
                System.out.println("-> Falha ao remover ID=" + p.getId() + ": " + e.getMessage());
            }
        }

        int restantes = produtoController.listarTodosProdutos().size();
        System.out.println("[RemoverProdutosPorNome] Concluído. Produtos restantes na tabela: " + restantes);
    }
}