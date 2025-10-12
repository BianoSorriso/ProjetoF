package com.scmflusao.tools;

import com.scmflusao.controller.ItemController;
import com.scmflusao.model.Item;

import java.util.List;

public class BuscarItensPorNomeContendo {
    public static void main(String[] args) {
        String termo = (args != null && args.length > 0) ? args[0] : "Placa";
        System.out.println("=== Itens com nome contendo: '" + termo + "' ===");
        try {
            ItemController itemController = new ItemController();
            List<Item> itens = itemController.buscarItensPorNomeContendo(termo);
            if (itens == null || itens.isEmpty()) {
                System.out.println("(Nenhum item encontrado)");
                return;
            }
            for (Item it : itens) {
                System.out.println("- [" + it.getId() + "] " + it.getNome() +
                                   " | Valor Unitário: R$ " + it.getValorUnitario());
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar itens por nome: " + e.getMessage());
            e.printStackTrace();
        }
    }
}