package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.controller.ItemController;
import com.scmflusao.controller.CidadeController;
import com.scmflusao.model.Produto;
import com.scmflusao.model.Item;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.SituacaoProduto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CriarComputadorComItens {
    public static void main(String[] args) {
        System.out.println("=== Criar produto 'Computador' com dois itens ===");
        try {
            ProdutoController produtoController = new ProdutoController();
            ItemController itemController = new ItemController();
            CidadeController cidadeController = new CidadeController();

            // Escolhe uma cidade existente para fabricar (pega a primeira)
            List<Cidade> cidades = cidadeController.listarTodasCidades();
            if (cidades == null || cidades.isEmpty()) {
                System.out.println("(Nenhuma cidade encontrada no banco. Não é possível criar produto.)");
                return;
            }
            Long cidadeId = cidades.get(0).getId();
            System.out.println("Usando cidade ID=" + cidadeId + " para fabricação.");

            // Verifica se já existe produto com nome exato 'Computador'
            List<Produto> existentes = produtoController.buscarProdutosPorNome("Computador");
            if (existentes != null && !existentes.isEmpty()) {
                System.out.println("Produto 'Computador' já existe (ID=" + existentes.get(0).getId() + ").");
            } else {
                // Busca os dois itens desejados
                List<Item> itensParaAssociar = new ArrayList<>();

                List<Item> placaMae = itemController.buscarItensPorNome("Placa Mae Asus");
                if (placaMae != null && !placaMae.isEmpty()) {
                    itensParaAssociar.add(placaMae.get(0));
                } else {
                    System.out.println("(Item 'Placa Mae Asus' não encontrado)");
                }

                List<Item> placaVideo = itemController.buscarItensPorNome("Placa de Video nvidia 5060");
                if (placaVideo != null && !placaVideo.isEmpty()) {
                    itensParaAssociar.add(placaVideo.get(0));
                } else {
                    System.out.println("(Item 'Placa de Video nvidia 5060' não encontrado)");
                }

                // Cria o produto e associa itens (se houver)
                Produto criado = produtoController.criarProdutoComItens(
                    "Computador",
                    "Computador completo",
                    new BigDecimal("4600.00"),
                    cidadeId,
                    SituacaoProduto.CRIADO,
                    itensParaAssociar
                );
                System.out.println("✓ Produto criado: ID=" + criado.getId() + ", nome='" + criado.getNome() + "'.");
                System.out.println("  Itens associados: " + (criado.getItensComponentes() != null ? criado.getItensComponentes().size() : 0));
            }

            // Listagem de confirmação
            List<Produto> produtos = produtoController.buscarProdutosPorNome("Computador");
            if (produtos == null || produtos.isEmpty()) {
                System.out.println("(Falha: produto 'Computador' não encontrado após criação)");
                return;
            }
            Produto completo = produtoController.buscarProdutoPorId(produtos.get(0).getId());
            System.out.println("Releitura: produto ID=" + completo.getId() + ", itens vinculados=" + (completo.getItensComponentes() != null ? completo.getItensComponentes().size() : 0));
            if (completo.getItensComponentes() != null) {
                for (Item it : completo.getItensComponentes()) {
                    System.out.println("  - [" + it.getId() + "] " + it.getNome() + " | Valor: R$ " + it.getValorUnitario());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar 'Computador' com itens: " + e.getMessage());
            e.printStackTrace();
        }
    }
}