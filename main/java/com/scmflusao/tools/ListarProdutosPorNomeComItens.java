package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.model.Produto;
import com.scmflusao.model.Item;

import java.util.List;

public class ListarProdutosPorNomeComItens {
    public static void main(String[] args) {
        String nomeProduto = (args != null && args.length > 0) ? args[0] : "Computador";
        System.out.println("=== Produtos com nome contendo: '" + nomeProduto + "' e seus itens ===");
        try {
            ProdutoController produtoController = new ProdutoController();
            List<Produto> produtos = produtoController.buscarProdutosPorNome(nomeProduto);
            if (produtos == null || produtos.isEmpty()) {
                System.out.println("(Nenhum produto encontrado)");
                return;
            }

            for (Produto p : produtos) {
                Produto completo = produtoController.buscarProdutoPorId(p.getId());
                System.out.println("\nProduto [" + completo.getId() + "] " + completo.getNome() +
                                   " | Preço: R$ " + completo.getPreco() +
                                   " | Situação: " + completo.getSituacao());
                List<Item> itens = completo.getItensComponentes();
                if (itens == null || itens.isEmpty()) {
                    System.out.println("  - (Sem itens associados)");
                } else {
                    for (Item it : itens) {
                        System.out.println("  - Item [" + it.getId() + "] " + it.getNome() +
                                           " | Qtd: " + it.getQuantidade() +
                                           " | Valor Unitário: R$ " + it.getValorUnitario());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar produtos e itens: " + e.getMessage());
            e.printStackTrace();
        }
    }
}