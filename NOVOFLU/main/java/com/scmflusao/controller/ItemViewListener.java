package com.scmflusao.controller;

import com.scmflusao.model.Item;
import com.scmflusao.model.SituacaoItem;
import com.scmflusao.view.ItemView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ItemViewListener {
    private final ItemView view;
    private final ItemController controller;
    private final ProdutoController produtoController;
    private final CidadeController cidadeController;
    
    public ItemViewListener(ItemView view) {
        this.view = view;
        this.controller = new ItemController();
        this.produtoController = new ProdutoController();
        this.cidadeController = new CidadeController();
        
        // Inicializa a view com dados
        carregarCidades();
        carregarProdutos();
        carregarItens();
        
        // Adiciona os listeners aos botões
        view.setSalvarListener(new SalvarListener());
        view.setConsultarListener(new ConsultarListener());
        view.setAlterarListener(new AlterarListener());
        view.setExcluirListener(new ExcluirListener());
        view.setLimparListener(new LimparListener());
        // Botões de busca dedicados e cálculo
        view.setBuscarPorNomeListener(new BuscarPorNomeListener());
        view.setBuscarPorSituacaoListener(new BuscarPorSituacaoListener());
        view.setCalcularValorTotalListener(new CalcularValorTotalListener());
    }
    
    private void carregarProdutos() {
        try {
            view.preencherProdutos(produtoController.listarTodosProdutos());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar produtos: " + e.getMessage());
        }
    }
    
    private void carregarCidades() {
        try {
            view.preencherCidades(cidadeController.listarCidadesOrigem());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar cidades: " + e.getMessage());
        }
    }
    
    private void carregarItens() {
        try {
            List<Item> itens = controller.listarTodosItens();
            view.preencherTabela(itens);
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar itens: " + e.getMessage());
        }
    }
    
    private class SalvarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Item item = view.getItemFromForm();
                boolean sucesso;
                
                if (item.getId() == null) {
                    // Novo item
                    controller.criarItem(
                        item.getNome(),
                        item.getDescricao(),
                        item.getPeso(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getCidadeOrigem().getId(),
                        item.getSituacao()
                    );
                    sucesso = true;
                } else {
                    // Atualização
                    controller.atualizarItem(
                        item.getId(),
                        item.getNome(),
                        item.getDescricao(),
                        item.getPeso(),
                        item.getQuantidade(),
                        item.getValorUnitario(),
                        item.getCidadeOrigem().getId(),
                        item.getSituacao()
                    );
                    sucesso = true;
                }
                
                if (sucesso) {
                    view.exibirMensagem("Item salvo com sucesso!");
                    view.limparFormulario();
                    carregarItens(); // Recarrega a tabela
                } else {
                    view.exibirMensagem("Erro ao salvar item.");
                }
            } catch (NumberFormatException ex) {
                view.exibirMensagem("Erro de formato: Verifique os campos numéricos.");
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao salvar: " + ex.getMessage());
            }
        }
    }
    
    private class ExcluirListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedItemId();
                if (id == null) {
                    view.exibirMensagem("Selecione um item na tabela para excluir.");
                    return;
                }
                
                int confirmacao = JOptionPane.showConfirmDialog(
                    view,
                    "Tem certeza que deseja excluir este item?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    boolean sucesso = controller.removerItem(id);
                    if (sucesso) {
                        view.exibirMensagem("Item excluído com sucesso!");
                        carregarItens(); // Recarrega a tabela
                    } else {
                        view.exibirMensagem("Erro ao excluir item.");
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao excluir: " + ex.getMessage());
            }
        }
    }
    
    private class LimparListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.limparFormulario();
        }
    }
    
    private class ConsultarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String opcoes[] = {"Por Produto", "Por Situação", "Por Quantidade Mínima"};
                String escolha = (String) JOptionPane.showInputDialog(
                    view,
                    "Escolha o tipo de consulta:",
                    "Consultar Itens",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
                );
                
                if (escolha != null) {
                    List<Item> itens = null;
                    
                    switch (escolha) {
                        case "Por Produto":
                            String nomeProduto = JOptionPane.showInputDialog(view, "Digite o nome do produto:");
                            if (nomeProduto != null && !nomeProduto.trim().isEmpty()) {
                                itens = controller.buscarItensPorProduto(nomeProduto);
                            }
                            break;
                            
                        case "Por Situação":
                            String situacao = JOptionPane.showInputDialog(view, "Digite a situação (DISPONIVEL, RESERVADO, VENDIDO):");
                            if (situacao != null && !situacao.trim().isEmpty()) {
                                try {
                                    com.scmflusao.model.SituacaoItem situacaoEnum = com.scmflusao.model.SituacaoItem.valueOf(situacao.toUpperCase());
                                    itens = controller.buscarItensPorSituacao(situacaoEnum);
                                } catch (IllegalArgumentException ex) {
                                    view.exibirMensagem("Situação inválida. Use: DISPONIVEL, RESERVADO ou VENDIDO");
                                    return;
                                }
                            }
                            break;
                            
                        case "Por Quantidade Mínima":
                            String qtdStr = JOptionPane.showInputDialog(view, "Digite a quantidade mínima:");
                            if (qtdStr != null && !qtdStr.trim().isEmpty()) {
                                try {
                                    int quantidade = Integer.parseInt(qtdStr);
                                    itens = controller.buscarItensComQuantidadeMinima(quantidade);
                                } catch (NumberFormatException ex) {
                                    view.exibirMensagem("Quantidade deve ser um número válido.");
                                    return;
                                }
                            }
                            break;
                    }
                    
                    if (itens != null) {
                        if (itens.isEmpty()) {
                            view.exibirMensagem("Nenhum item encontrado com os critérios especificados.");
                        } else {
                            view.preencherTabela(itens);
                            view.exibirMensagem("Consulta realizada com sucesso!");
                        }
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao consultar: " + ex.getMessage());
            }
        }
    }

    private class BuscarPorNomeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String nome = view.solicitarNomeBusca();
                if (nome != null && !nome.trim().isEmpty()) {
                    List<Item> itens = controller.buscarItensPorNomeContendo(nome.trim());
                    if (itens.isEmpty()) {
                        view.exibirMensagem("Nenhum item encontrado contendo '" + nome + "'.");
                    } else {
                        view.preencherTabela(itens);
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao buscar por nome: " + ex.getMessage());
            }
        }
    }

    private class BuscarPorSituacaoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                com.scmflusao.model.SituacaoItem situacao = view.solicitarSituacaoBusca();
                if (situacao != null) {
                    List<Item> itens = controller.buscarItensPorSituacao(situacao);
                    if (itens.isEmpty()) {
                        view.exibirMensagem("Nenhum item na situação '" + situacao.getDescricao() + "'.");
                    } else {
                        view.preencherTabela(itens);
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao buscar por situação: " + ex.getMessage());
            }
        }
    }

    private class CalcularValorTotalListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long idSelecionado = view.getSelectedItemId();
                if (idSelecionado != null) {
                    java.math.BigDecimal valor = controller.calcularValorTotalItem(idSelecionado);
                    view.exibirValorTotal(valor);
                } else {
                    // Se nenhum selecionado, tenta calcular a partir do formulário
                    try {
                        Item item = view.getItemFromForm();
                        java.math.BigDecimal valor = item.calcularValorTotal();
                        view.exibirValorTotal(valor);
                    } catch (NumberFormatException nfe) {
                        view.exibirMensagem("Preencha quantidade e valor unitário válidos ou selecione um item na tabela.");
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao calcular valor: " + ex.getMessage());
            }
        }
    }
    
    private class AlterarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedItemId();
                if (id == null) {
                    view.exibirMensagem("Selecione um item na tabela para alterar.");
                    return;
                }
                
                Item item = controller.buscarItemPorId(id);
                if (item != null) {
                    view.preencherFormulario(item);
                } else {
                    view.exibirMensagem("Item não encontrado.");
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao alterar: " + ex.getMessage());
            }
        }
    }
}