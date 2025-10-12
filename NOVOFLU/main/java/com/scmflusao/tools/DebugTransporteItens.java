package com.scmflusao.tools;

import com.scmflusao.auth.SessionManager;
import com.scmflusao.controller.ProdutoController;
import com.scmflusao.controller.TransporteViewListener;
import com.scmflusao.model.Item;
import com.scmflusao.model.Perfil;
import com.scmflusao.model.Produto;
import com.scmflusao.model.Transporte;
import com.scmflusao.model.Usuario;
import com.scmflusao.view.TransporteView;

import javax.swing.*;
import java.util.List;

/**
 * Utilitário de console para inspecionar os itens do combo da tela de Transporte
 * após selecionar um produto específico. Não abre a UI; apenas cria a view,
 * injeta o listener e imprime os itens do combo no console.
 */
public class DebugTransporteItens {
    public static void main(String[] args) throws Exception {
        String nomeProduto = args.length > 0 ? args[0] : "Computador";
        System.out.println("[DebugTransporteItens] Produto alvo: '" + nomeProduto + "'\n");

        // Inicializa sessão como ADMIN para evitar NPE em permissões
        SessionManager.getInstance().setUsuarioAtual(
                new Usuario(0L, "Debug Admin", "debug@local", "x", Perfil.ADMIN, true)
        );

        // Cria view e listener (popula combos)
        TransporteView view = new TransporteView();
        new TransporteViewListener(view);

        // Busca produto por nome (case-insensitive, contendo)
        ProdutoController produtoController = new ProdutoController();
        java.util.List<Produto> todos = produtoController.listarTodosProdutos();
        Produto alvo = null;
        for (Produto p : todos) {
            if (p.getNome() != null && p.getNome().toLowerCase().contains(nomeProduto.toLowerCase())) {
                alvo = p;
                break;
            }
        }
        if (alvo == null) {
            System.out.println("Produto não encontrado pelo nome informado. Produtos disponíveis:");
            int k = 1;
            for (Produto p : todos) {
                System.out.printf("%2d) ID=%s | Nome=%s | Situação=%s\n", k++, p.getId(), p.getNome(), p.getSituacao());
            }
            return;
        }

        // Relatório: itens componentes e suas quantidades antes de preencher a view
        Produto completo = produtoController.buscarProdutoPorId(alvo.getId());
        System.out.println("Itens componentes do produto antes do filtro/clone:");
        if (completo.getItensComponentes() == null || completo.getItensComponentes().isEmpty()) {
            System.out.println("(nenhum)");
        } else {
            int k = 1;
            for (Item it : completo.getItensComponentes()) {
                Integer qtd = it.getQuantidade() != null ? it.getQuantidade() : 1;
                System.out.printf(" %d) ID=%s | Nome=%s | QtdAssoc=%d\n", k++, it.getId(), it.getNome(), qtd);
            }
        }

        // Seleciona produto na view (dispara listener de filtro de itens)
        Transporte t = new Transporte();
        t.setProduto(alvo);
        // Garantir execução no EDT para eventos do Swing
        SwingUtilities.invokeAndWait(() -> view.preencherFormulario(t));

        // Lê itens atuais do combo
        List<Item> itensCombo = view.getItensCombo();
        System.out.println("Itens no combo de Transporte para o produto '" + alvo.getNome() + "':");
        if (itensCombo.isEmpty()) {
            System.out.println("(vazio)");
        } else {
            int idx = 1;
            for (Item i : itensCombo) {
                System.out.printf("%2d) ID=%s | Nome=%s | Desc=%s | Qtd=%s | Valor=%s | Peso=%s\n",
                        idx++,
                        i.getId(),
                        i.getNome(),
                        i.getDescricao(),
                        i.getQuantidade(),
                        i.getValorUnitario(),
                        i.getPeso()
                );
            }
        }
    }
}