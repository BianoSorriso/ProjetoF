package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.model.Item;
import com.scmflusao.model.Produto;

public class VerificarProdutoItem {
    public static void main(String[] args) {
        try {
            String nome = args.length > 0 ? args[0] : "pc";
            ProdutoController pc = new ProdutoController();
            java.util.List<Produto> produtos = pc.listarTodosProdutos();
            Produto alvo = null;
            for (Produto p : produtos) {
                if (p.getNome() != null && p.getNome().toLowerCase().contains(nome.toLowerCase())) {
                    alvo = p; break;
                }
            }
            if (alvo == null) {
                System.out.println("Produto não encontrado por nome contendo: " + nome);
                for (Produto p : produtos) {
                    System.out.println("- ID=" + p.getId() + " | Nome=" + p.getNome());
                }
                return;
            }
            Produto completo = pc.buscarProdutoPorId(alvo.getId());
            System.out.println("Produto ID=" + completo.getId() + " | Nome=" + completo.getNome());
            if (completo.getItensComponentes() == null || completo.getItensComponentes().isEmpty()) {
                System.out.println("(Sem itens componentes)");
            } else {
                int k = 1;
                for (Item it : completo.getItensComponentes()) {
                    Integer qtd = it.getQuantidade() != null ? it.getQuantidade() : 1;
                    System.out.printf(" %d) ItemID=%s | Nome=%s | QtdAssoc=%d\n", k++, it.getId(), it.getNome(), qtd);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}