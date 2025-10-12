package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.model.Item;
import com.scmflusao.model.Produto;

import java.util.ArrayList;
import java.util.List;

/**
 * Ferramenta de console para padronizar a composição de itens
 * de TODOS os produtos, copiando da composição de um produto
 * de referência (por padrão, "pc").
 *
 * Uso:
 *   java com.scmflusao.tools.PadronizarItensDosProdutos [nomeProdutoReferencia]
 *
 * Efeito:
 *   - Busca o produto de referência com seus itens (e respectivas quantidades)
 *   - Atualiza a associação de itens de todos os demais produtos para refletir
 *     exatamente a mesma composição
 */
public class PadronizarItensDosProdutos {
    public static void main(String[] args) {
        String referenciaNome = (args != null && args.length > 0) ? args[0] : "pc";
        ProdutoController produtoController = new ProdutoController();

        try {
            // Localiza produto de referência por nome (case-insensitive, contendo)
            List<Produto> todos = produtoController.listarTodosProdutos();
            Produto referencia = null;
            for (Produto p : todos) {
                if (p.getNome() != null && p.getNome().toLowerCase().contains(referenciaNome.toLowerCase())) {
                    referencia = produtoController.buscarProdutoPorId(p.getId()); // carrega com itens
                    break;
                }
            }

            if (referencia == null) {
                System.out.println("Produto de referência não encontrado: '" + referenciaNome + "'. Produtos disponíveis:");
                int k = 1;
                for (Produto p : todos) {
                    System.out.printf("%2d) ID=%s | Nome=%s | Situação=%s\n", k++, p.getId(), p.getNome(), p.getSituacao());
                }
                return;
            }

            List<Item> composicao = referencia.getItensComponentes();
            if (composicao == null || composicao.isEmpty()) {
                System.out.println("Produto de referência não possui itens associados. Nada a padronizar.");
                return;
            }

            // Clona lista para evitar efeitos colaterais
            List<Item> composicaoClone = new ArrayList<>();
            for (Item it : composicao) {
                Item c = new Item();
                c.setId(it.getId());
                c.setNome(it.getNome());
                c.setDescricao(it.getDescricao());
                c.setPeso(it.getPeso());
                c.setQuantidade(it.getQuantidade()); // preserva quantidade do vínculo
                c.setValorUnitario(it.getValorUnitario());
                c.setSituacao(it.getSituacao());
                c.setCidadeOrigem(it.getCidadeOrigem());
                c.setArmazemAtual(it.getArmazemAtual());
                c.setDataEntrada(it.getDataEntrada());
                c.setDataAtualizacao(it.getDataAtualizacao());
                composicaoClone.add(c);
            }

            System.out.println("Padronizando todos os produtos com base em: '" + referencia.getNome() + "' (ID=" + referencia.getId() + ")");

            int atualizados = 0;
            for (Produto p : todos) {
                if (p.getId() == null) continue;
                if (p.getId().equals(referencia.getId())) continue; // pula produto de referência
                try {
                    produtoController.atualizarItensDoProduto(p.getId(), new ArrayList<>(composicaoClone));
                    atualizados++;
                    System.out.println("- Atualizado produto ID=" + p.getId() + " | Nome=" + p.getNome());
                } catch (Exception e) {
                    System.err.println("Falha ao atualizar produto ID=" + p.getId() + ": " + e.getMessage());
                }
            }

            System.out.println("Concluído. Produtos atualizados: " + atualizados);
        } catch (Exception e) {
            System.err.println("Erro ao padronizar itens dos produtos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}