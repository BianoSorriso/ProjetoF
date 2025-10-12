package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.model.Item;
import com.scmflusao.model.Produto;

import java.util.List;

/**
 * Lista todos os produtos com sua composição de itens (expandindo por quantidade).
 */
public class ListarComposicaoProdutos {
    public static void main(String[] args) {
        ProdutoController produtoController = new ProdutoController();
        try {
            List<Produto> todos = produtoController.listarTodosProdutos();
            if (todos == null || todos.isEmpty()) {
                System.out.println("(Nenhum produto encontrado)");
                return;
            }
            int i = 1;
            for (Produto p : todos) {
                Produto completo = produtoController.buscarProdutoPorId(p.getId());
                System.out.println(i++ + ". Produto ID=" + completo.getId() + " | Nome=" + completo.getNome());
                List<Item> itens = completo.getItensComponentes();
                if (itens == null || itens.isEmpty()) {
                    System.out.println("   - (sem itens)");
                } else {
                    for (Item it : itens) {
                        int qtd = (it.getQuantidade() != null && it.getQuantidade() > 0) ? it.getQuantidade() : 1;
                        System.out.println("   - [ID=" + it.getId() + "] " + it.getNome() + " | Qtd=" + qtd);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar composição: " + e.getMessage());
            e.printStackTrace();
        }
    }
}