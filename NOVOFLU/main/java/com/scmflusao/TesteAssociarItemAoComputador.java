package com.scmflusao;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.controller.ItemController;
import com.scmflusao.model.Produto;
import com.scmflusao.model.Item;

import java.util.ArrayList;
import java.util.List;

public class TesteAssociarItemAoComputador {
    public static void main(String[] args) {
        System.out.println("=== Teste: Adicionar 'Placa Mae Asus' ao produto 'Computador' (ID 5) ===");
        try {
            ProdutoController produtoController = new ProdutoController();
            ItemController itemController = new ItemController();

            // Carrega produto 5 com seus itens
            Produto produto = produtoController.buscarProdutoPorId(5L);
            System.out.println("Produto carregado: " + produto.getNome() +
                               " | Preço: R$ " + produto.getPreco());

            // Lista itens atuais
            List<Item> itensAtuais = produto.getItensComponentes();
            System.out.println("Itens atuais do produto: " + itensAtuais.size());
            for (Item it : itensAtuais) {
                System.out.println("  - [" + it.getId() + "] " + it.getNome() +
                                   " | Valor: R$ " + it.getValorUnitario() +
                                   " | Peso: " + it.getPeso());
            }

            // Busca item 'Placa Mae Asus'
            Item asus;
            List<Item> encontrados = itemController.buscarItensPorNome("Placa Mae Asus");
            if (!encontrados.isEmpty()) {
                asus = encontrados.get(0);
            } else {
                // fallback por ID conhecido no seed
                asus = itemController.buscarItemPorId(2L);
            }
            System.out.println("Item alvo: [" + asus.getId() + "] " + asus.getNome() +
                               " | Valor: R$ " + asus.getValorUnitario());

            // Monta nova lista de itens (evitando duplicidade por ID)
            List<Item> novosItens = new ArrayList<>(itensAtuais);
            boolean jaExiste = false;
            for (Item it : novosItens) {
                if (it.getId() != null && asus.getId() != null && it.getId().equals(asus.getId())) {
                    jaExiste = true;
                    break;
                }
            }
            if (!jaExiste) {
                novosItens.add(asus);
            } else {
                System.out.println("Item já estava associado. Prosseguindo com validação.");
            }

            // Persiste associação como a tela faria ao salvar
            Produto atualizado = produtoController.atualizarProdutoComItens(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCidadeFabricacao() != null ? produto.getCidadeFabricacao().getId() : null,
                produto.getSituacao(),
                novosItens
            );
            System.out.println("✓ Associação persistida. Total de itens agora: " + atualizado.getItensComponentes().size());

            // Recarrega para confirmar itens vindos do banco
            Produto relido = produtoController.buscarProdutoPorId(5L);
            System.out.println("Revalidação: itens vinculados no banco: " + relido.getItensComponentes().size());
            boolean asusVinculado = false;
            for (Item it : relido.getItensComponentes()) {
                System.out.println("  - [" + it.getId() + "] " + it.getNome());
                if (it.getId() != null && it.getId().equals(asus.getId())) {
                    asusVinculado = true;
                }
            }

            System.out.println("Resultado: 'Placa Mae Asus' vinculado? " + (asusVinculado ? "SIM" : "NÃO"));
        } catch (Exception e) {
            System.err.println("Falha no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}