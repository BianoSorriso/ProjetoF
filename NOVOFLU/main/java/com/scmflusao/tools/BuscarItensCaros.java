package com.scmflusao.tools;

import com.scmflusao.controller.ItemController;
import com.scmflusao.model.Item;

import java.math.BigDecimal;
import java.util.List;

public class BuscarItensCaros {
    public static void main(String[] args) {
        String minimoStr = (args != null && args.length > 0) ? args[0] : "4000";
        BigDecimal minimo;
        try {
            minimo = new BigDecimal(minimoStr);
        } catch (Exception e) {
            System.err.println("Valor mínimo inválido: " + minimoStr);
            return;
        }

        System.out.println("=== Itens com valor unitário >= R$ " + minimo + " ===");
        try {
            ItemController itemController = new ItemController();
            List<Item> itens = itemController.buscarItensComValorMaiorOuIgual(minimo);
            if (itens == null || itens.isEmpty()) {
                System.out.println("(Nenhum item encontrado)");
                return;
            }
            for (Item it : itens) {
                System.out.println("- [" + it.getId() + "] " + it.getNome() +
                                   " | Quantidade: " + it.getQuantidade() +
                                   " | Valor Unitário: R$ " + it.getValorUnitario());
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar itens caros: " + e.getMessage());
            e.printStackTrace();
        }
    }
}