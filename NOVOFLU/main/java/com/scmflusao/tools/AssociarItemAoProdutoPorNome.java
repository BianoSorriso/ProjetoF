package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.controller.ItemController;
import com.scmflusao.model.Produto;
import com.scmflusao.model.Item;

import java.util.ArrayList;
import java.util.List;

public class AssociarItemAoProdutoPorNome {
    public static void main(String[] args) {
        String nomeProduto = (args != null && args.length > 0) ? args[0] : "Computador";
        String nomeItem = (args != null && args.length > 1) ? args[1] : "Placa de Video nvidia 5060";

        System.out.println("=== Associar item ao produto ===");
        System.out.println("Produto: '" + nomeProduto + "' | Item: '" + nomeItem + "'");
        try {
            ProdutoController produtoController = new ProdutoController();
            ItemController itemController = new ItemController();

            List<Produto> produtos = produtoController.buscarProdutosPorNome(nomeProduto);
            if (produtos == null || produtos.isEmpty()) {
                System.out.println("(Produto não encontrado)");
                return;
            }
            Produto alvo = produtoController.buscarProdutoPorId(produtos.get(0).getId());

            List<Item> candidatos = itemController.buscarItensPorNome(nomeItem);
            if (candidatos == null || candidatos.isEmpty()) {
                System.out.println("(Item não encontrado)");
                return;
            }
            Item item = candidatos.get(0);

            List<Item> atuais = alvo.getItensComponentes();
            boolean jaAssociado = false;
            for (Item it : atuais) {
                if (it.getId() != null && it.getId().equals(item.getId())) {
                    jaAssociado = true;
                    break;
                }
            }

            if (jaAssociado) {
                System.out.println("Item já estava associado. Nada a fazer.");
            } else {
                List<Item> novos = new ArrayList<>(atuais);
                novos.add(item);
                // Persistir associações
                new com.scmflusao.dao.impl.ProdutoDAOImpl().atualizarItensDoProduto(alvo.getId(), novos);
                System.out.println("Associação realizada com sucesso.");
            }

            // Exibir itens atuais após operação
            Produto atualizado = produtoController.buscarProdutoPorId(alvo.getId());
            System.out.println("\nItens do produto '" + atualizado.getNome() + "':");
            List<Item> itens = atualizado.getItensComponentes();
            if (itens == null || itens.isEmpty()) {
                System.out.println("(Sem itens)");
            } else {
                for (Item it : itens) {
                    System.out.println("- [" + it.getId() + "] " + it.getNome() +
                                       " | Qtd: " + it.getQuantidade() +
                                       " | Valor Unitário: R$ " + it.getValorUnitario());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao associar item ao produto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}