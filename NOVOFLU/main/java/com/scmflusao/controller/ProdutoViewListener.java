package com.scmflusao.controller;

import com.scmflusao.model.Produto;
import com.scmflusao.model.Item;
import com.scmflusao.view.ProdutoView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class ProdutoViewListener {
    private final ProdutoView view;
    private final ProdutoController controller;
    private final CidadeController cidadeController;
    private final ItemController itemController;
    private List<Item> itensComponentesSelecionados;
    // Removido controle de quantidade agregada; lista exibe itens duplicados individualmente
    
    public ProdutoViewListener(ProdutoView view) {
        this.view = view;
        this.controller = new ProdutoController();
        this.cidadeController = new CidadeController();
        this.itemController = new ItemController();
        this.itensComponentesSelecionados = new ArrayList<>();
        
        // Inicializa a view com dados
        carregarCidades();
        carregarProdutos();
        carregarItensDisponiveis();
        
        // Adiciona os listeners aos botões
        view.setSalvarListener(new SalvarListener());
        view.setConsultarListener(new ConsultarListener());
        view.setAlterarListener(new AlterarListener());
        view.setExcluirListener(new ExcluirListener());
        view.setLimparListener(new LimparListener());
        view.setAdicionarItemListener(new AdicionarItemListener());
        view.setRemoverItemListener(new RemoverItemListener());
        view.setListarItensListener(new ListarItensListener());
        // Botões de busca dedicados
        view.setBuscarPorNomeListener(new BuscarPorNomeListener());
        view.setBuscarPorSituacaoListener(new BuscarPorSituacaoListener());
        // Carrega automaticamente detalhes e itens ao selecionar um produto na tabela
        view.adicionarSelectionListener(new ProdutoSelectionListener());
    }
    
    private void carregarCidades() {
        try {
            view.preencherCidades(cidadeController.listarTodasCidades());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar cidades: " + e.getMessage());
        }
    }

    // Listener para seleção de linhas na tabela de produtos
    private class ProdutoSelectionListener implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(javax.swing.event.ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) return; // evita múltiplos disparos
            try {
                Long id = view.getSelectedProdutoId();
                if (id == null) return;

                Produto produto = controller.buscarProdutoPorId(id);
                if (produto != null) {
                    // Preenche formulário e itens do produto selecionado
                    view.preencherFormulario(produto);
                    itensComponentesSelecionados.clear();
                    if (produto.getItensComponentes() != null) {
                        for (Item it : produto.getItensComponentes()) {
                            int qtd = (it.getQuantidade() != null && it.getQuantidade() > 0) ? it.getQuantidade() : 1;
                            for (int i = 0; i < qtd; i++) {
                                Item clone = new Item();
                                clone.setId(it.getId());
                                clone.setNome(it.getNome());
                                clone.setDescricao(it.getDescricao());
                                clone.setPeso(it.getPeso());
                                clone.setQuantidade(1);
                                clone.setValorUnitario(it.getValorUnitario());
                                itensComponentesSelecionados.add(clone);
                            }
                        }
                    }
                    atualizarCamposCalculados();
                }
            } catch (Exception ex) {
                view.exibirErro("Erro ao carregar produto selecionado: " + ex.getMessage());
            }
        }
    }
    
    private void carregarProdutos() {
        try {
            List<Produto> produtos = controller.listarTodosProdutos();
            view.preencherTabela(produtos);
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar produtos: " + e.getMessage());
        }
    }
    
    private void carregarItensDisponiveis() {
        try {
            List<Item> itens = itemController.listarTodosItens();
            view.preencherItensDisponiveis(itens);
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar itens: " + e.getMessage());
        }
    }
    
    private void atualizarCamposCalculados() {
        // Exibe todos os itens, inclusive duplicados
        view.preencherTabelaItens(itensComponentesSelecionados);
        // Recalcula preço automaticamente pela soma dos itens (considerando quantidade)
        view.calcularPrecoAutomatico();
    }
    
    private class SalvarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Produto produto = view.getProdutoFromForm();
                // Consolida duplicados: conta ocorrências por ID e atualiza quantidade
                java.util.Map<Long, Integer> contagemPorId = new java.util.HashMap<>();
                java.util.Map<Long, Item> primeiroItemPorId = new java.util.HashMap<>();
                for (Item it : itensComponentesSelecionados) {
                    if (it.getId() == null) continue;
                    contagemPorId.put(it.getId(), contagemPorId.getOrDefault(it.getId(), 0) + 1);
                    primeiroItemPorId.putIfAbsent(it.getId(), it);
                }

                // Associa apenas um exemplar de cada ID ao produto; a UI expande pela quantidade
                java.util.List<Item> itensUnicosParaAssociar = new java.util.ArrayList<>();
                for (Long idItem : primeiroItemPorId.keySet()) {
                    Item base = primeiroItemPorId.get(idItem);
                    Integer qtdAssoc = contagemPorId.getOrDefault(idItem, 1);
                    base.setQuantidade(qtdAssoc);
                    itensUnicosParaAssociar.add(base);
                }

                // Define itens consolidados no produto (para coerência do objeto local)
                produto.setItensComponentes(itensUnicosParaAssociar);
                
                boolean sucesso;
                Produto salvo = null;
                
                if (produto.getId() == null) {
                    // Novo produto com itens
                    salvo = controller.criarProdutoComItens(
                        produto.getNome(),
                        produto.getDescricao(),
                        produto.getPrecoUnitario(),
                        produto.getCidadeOrigem().getId(),
                        produto.getSituacao(),
                        new ArrayList<>(itensUnicosParaAssociar)
                    );
                    sucesso = true;
                } else {
                    // Atualização com itens
                    salvo = controller.atualizarProdutoComItens(
                        produto.getId(),
                        produto.getNome(),
                        produto.getDescricao(),
                        produto.getPrecoUnitario(),
                        produto.getCidadeOrigem().getId(),
                        produto.getSituacao(),
                        new ArrayList<>(itensUnicosParaAssociar)
                    );
                    sucesso = true;
                }
                
                if (sucesso) {
                    view.exibirMensagem("Produto salvo com sucesso!");
                    view.limparFormulario();
                    itensComponentesSelecionados.clear();
                    carregarProdutos(); // Recarrega a tabela
                    // Seleciona o produto salvo para carregar itens automaticamente
                    if (salvo != null) {
                        view.selecionarProdutoPorId(salvo.getId());
                    }
                } else {
                    view.exibirMensagem("Erro ao salvar produto.");
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
                Long id = view.getSelectedProdutoId();
                if (id == null) {
                    view.exibirMensagem("Selecione um produto na tabela para excluir.");
                    return;
                }
                
                int confirmacao = JOptionPane.showConfirmDialog(
                    view,
                    "Tem certeza que deseja excluir este produto?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    boolean sucesso = controller.removerProduto(id);
                    if (sucesso) {
                        view.exibirMensagem("Produto excluído com sucesso!");
                        // Limpa estado da UI para não manter itens do produto excluído
                        itensComponentesSelecionados.clear();
                        view.limparTabelaItens();
                        view.limparFormulario();
                        view.calcularPrecoAutomatico();
                        carregarProdutos(); // Recarrega a tabela
                    } else {
                        view.exibirMensagem("Erro ao excluir produto.");
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
            itensComponentesSelecionados.clear();
            view.limparTabelaItens();
            view.calcularPrecoAutomatico();
        }
    }
    
    private class ConsultarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String nome = JOptionPane.showInputDialog(view, "Digite o nome do produto para consultar:");
                if (nome != null && !nome.trim().isEmpty()) {
                    List<Produto> produtos = controller.buscarProdutosPorNome(nome);
                    if (produtos.isEmpty()) {
                        view.exibirMensagem("Nenhum produto encontrado com o nome '" + nome + "'.");
                    } else {
                        view.preencherTabela(produtos);
                        view.selecionarPrimeiroProdutoSeExistir();
                        view.exibirMensagem("Consulta realizada com sucesso!");
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
                    // Busca mais amigável: por trecho do nome
                    List<Produto> produtos = controller.buscarProdutosPorNomeContendo(nome.trim());
                    if (produtos.isEmpty()) {
                        view.exibirMensagem("Nenhum produto encontrado contendo '" + nome + "'.");
                    } else {
                        view.preencherTabela(produtos);
                        view.selecionarPrimeiroProdutoSeExistir();
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
                com.scmflusao.model.SituacaoProduto situacao = view.solicitarSituacaoBusca();
                if (situacao != null) {
                    List<Produto> produtos = controller.buscarProdutosPorSituacao(situacao);
                    if (produtos.isEmpty()) {
                        view.exibirMensagem("Nenhum produto na situação '" + situacao.getDescricao() + "'.");
                    } else {
                        view.preencherTabela(produtos);
                        view.selecionarPrimeiroProdutoSeExistir();
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao buscar por situação: " + ex.getMessage());
            }
        }
    }
    
    private class AlterarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedProdutoId();
                if (id == null) {
                    view.exibirMensagem("Selecione um produto na tabela para alterar.");
                    return;
                }
                
                Produto produto = controller.buscarProdutoPorId(id);
                if (produto != null) {
                    view.preencherFormulario(produto);
                    // Carrega os itens componentes do produto, expandindo pela quantidade
                    itensComponentesSelecionados.clear();
                    if (produto.getItensComponentes() != null) {
                        for (Item it : produto.getItensComponentes()) {
                            int qtd = (it.getQuantidade() != null && it.getQuantidade() > 0) ? it.getQuantidade() : 1;
                            for (int i = 0; i < qtd; i++) {
                                Item clone = new Item();
                                clone.setId(it.getId());
                                clone.setNome(it.getNome());
                                clone.setDescricao(it.getDescricao());
                                clone.setPeso(it.getPeso());
                                clone.setQuantidade(1);
                                clone.setValorUnitario(it.getValorUnitario());
                                itensComponentesSelecionados.add(clone);
                            }
                        }
                    }
                    atualizarCamposCalculados();
                    // Recalcula preço com base exclusivamente na soma dos itens
                    view.calcularPrecoAutomatico();
                } else {
                    view.exibirMensagem("Produto não encontrado.");
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao alterar: " + ex.getMessage());
            }
        }
    }
    
    private class AdicionarItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Item itemSelecionado = view.getItemSelecionado();
                if (itemSelecionado == null) {
                    view.exibirMensagem("Selecione um item para adicionar.");
                    return;
                }

                // Adiciona item diretamente, permitindo duplicados na lista
                itensComponentesSelecionados.add(itemSelecionado);

                atualizarCamposCalculados();
                // Recalcula preço apenas ao adicionar item
                view.calcularPrecoAutomatico();
                // Persistir imediatamente quando um produto existente estiver selecionado
                Long produtoId = view.getSelectedProdutoId();
                if (produtoId != null) {
                    try {
                        java.util.Map<Long, Integer> contagemPorId = new java.util.HashMap<>();
                        java.util.Map<Long, Item> primeiroItemPorId = new java.util.HashMap<>();
                        for (Item it : itensComponentesSelecionados) {
                            if (it.getId() == null) continue;
                            contagemPorId.put(it.getId(), contagemPorId.getOrDefault(it.getId(), 0) + 1);
                            primeiroItemPorId.putIfAbsent(it.getId(), it);
                        }
                        java.util.List<Item> itensUnicosParaAssociar = new java.util.ArrayList<>();
                        for (Long idItem : primeiroItemPorId.keySet()) {
                            Item base = primeiroItemPorId.get(idItem);
                            Integer qtdAssoc = contagemPorId.getOrDefault(idItem, 1);
                            base.setQuantidade(qtdAssoc);
                            itensUnicosParaAssociar.add(base);
                        }
                        controller.atualizarItensDoProduto(produtoId, new java.util.ArrayList<>(itensUnicosParaAssociar));
                        view.exibirMensagem("Item adicionado e quantidade persistida no produto.");
                    } catch (Exception persistEx) {
                        view.exibirErro("Falha ao persistir adição de item: " + persistEx.getMessage());
                    }
                } else {
                    view.exibirMensagem("Item adicionado. Salve o produto para persistir.");
                }

            } catch (Exception ex) {
                view.exibirErro("Erro ao adicionar item: " + ex.getMessage());
            }
        }
    }
    
        private class RemoverItemListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer rowIndex = view.getLinhaSelecionadaTabela();
                    if (rowIndex == null) {
                        view.exibirMensagem("Selecione uma linha na tabela para remover.");
                        return;
                    }
                    if (rowIndex < 0 || rowIndex >= itensComponentesSelecionados.size()) {
                        view.exibirMensagem("Linha selecionada inválida.");
                        return;
                    }
                    Item itemSelecionado = itensComponentesSelecionados.get(rowIndex);
                    Long itemId = itemSelecionado.getId();
                    // Remove exatamente a ocorrência selecionada
                    itensComponentesSelecionados.remove((int) rowIndex);

                    atualizarCamposCalculados();
                    // Recalcula preço apenas ao remover item
                    view.calcularPrecoAutomatico();

                    // Se um produto existente estiver selecionado, persistir imediatamente as associações de itens
                    Long produtoId = view.getSelectedProdutoId();
                    if (produtoId != null) {
                        try {
                            controller.atualizarItensDoProduto(produtoId, new ArrayList<>(itensComponentesSelecionados));
                            view.exibirMensagem("Item removido/quantidade ajustada e mudanças persistidas.");
                        } catch (Exception persistEx) {
                            view.exibirErro("Falha ao persistir mudança de itens: " + persistEx.getMessage());
                        }
                    } else {
                        view.exibirMensagem("Item ajustado na lista. Salve para persistir no novo produto.");
                    }
                
                } catch (Exception ex) {
                    view.exibirErro("Erro ao remover item: " + ex.getMessage());
                }
            }
        }

    private class ListarItensListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedProdutoId();
                if (id == null) {
                    view.exibirMensagem("Selecione um produto na tabela para listar itens.");
                    return;
                }

                Produto produto = controller.buscarProdutoPorId(id); // carrega com itens
                itensComponentesSelecionados.clear();
                if (produto.getItensComponentes() != null) {
                    for (Item it : produto.getItensComponentes()) {
                        int qtd = (it.getQuantidade() != null && it.getQuantidade() > 0) ? it.getQuantidade() : 1;
                        for (int i = 0; i < qtd; i++) {
                            Item clone = new Item();
                            clone.setId(it.getId());
                            clone.setNome(it.getNome());
                            clone.setDescricao(it.getDescricao());
                            clone.setPeso(it.getPeso());
                            clone.setQuantidade(1);
                            clone.setValorUnitario(it.getValorUnitario());
                            itensComponentesSelecionados.add(clone);
                        }
                    }
                }
                atualizarCamposCalculados();
                view.exibirMensagem("Itens listados com sucesso!");
            } catch (Exception ex) {
                view.exibirErro("Erro ao listar itens: " + ex.getMessage());
            }
        }
    }
}