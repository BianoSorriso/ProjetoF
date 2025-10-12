package com.scmflusao.tools;

import com.scmflusao.controller.ProdutoController;
import com.scmflusao.controller.TransporteController;
import com.scmflusao.model.Produto;
import com.scmflusao.model.Transporte;

import java.util.List;

public class DesassociarProdutoDosTransportes {
    public static void main(String[] args) {
        String nomeProduto = args.length > 0 ? args[0] : "Computador";
        System.out.println("[DesassociarProdutoDosTransportes] Desassociando produto dos transportes: '" + nomeProduto + "'.");

        ProdutoController produtoController = new ProdutoController();
        TransporteController transporteController = new TransporteController();

        List<Produto> produtos = produtoController.buscarProdutosPorNome(nomeProduto);
        if (produtos.isEmpty()) {
            System.out.println("Produto não encontrado: '" + nomeProduto + "'.");
            return;
        }

        Produto produto = produtos.get(0);
        Long produtoId = produto.getId();

        List<Transporte> transportes = transporteController.buscarPorProduto(produtoId);
        if (transportes.isEmpty()) {
            System.out.println("Nenhum transporte referenciando o produto ID=" + produtoId + ".");
            return;
        }

        int count = 0;
        for (Transporte t : transportes) {
            try {
                t.setProduto(null);
                transporteController.atualizarTransporte(t);
                count++;
            } catch (Exception e) {
                System.out.println("Falha ao desassociar no transporte ID=" + t.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("[DesassociarProdutoDosTransportes] Concluído. Transportes atualizados: " + count);
    }
}