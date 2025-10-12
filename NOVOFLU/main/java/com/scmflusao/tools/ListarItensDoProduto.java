package com.scmflusao.tools;

import com.scmflusao.controller.ItemController;
import com.scmflusao.model.Item;

import java.util.List;

public class ListarItensDoProduto {
    public static void main(String[] args) {
        String nomeProduto = (args != null && args.length > 0) ? args[0] : "Computador";
        System.out.println("=== Itens que compõem o produto: " + nomeProduto + " ===");
        try {
            ItemController itemController = new ItemController();
            List<Item> itens = itemController.buscarItensPorProduto(nomeProduto);
            if (itens == null || itens.isEmpty()) {
                System.out.println("(Nenhum item associado encontrado)");
                return;
            }
            for (Item it : itens) {
                System.out.println("- [" + it.getId() + "] " + it.getNome() +
                                   " | Quantidade: " + it.getQuantidade() +
                                   " | Valor Unitário: R$ " + it.getValorUnitario() +
                                   " | Peso: " + it.getPeso());
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar itens do produto '" + nomeProduto + "': " + e.getMessage());
            e.printStackTrace();
        }
    }
}