package com.scmflusao.view;

import com.scmflusao.model.Produto;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.SituacaoProduto;
import com.scmflusao.model.Item;

import javax.swing.*;
import java.math.BigDecimal;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class ProdutoView extends JFrame {
    private JTextField txtNome;
    private JTextField txtDescricao;
    private JTextField txtPreco;
    private JComboBox<SituacaoProduto> cbSituacao;
    private JComboBox<Cidade> cbCidadeFabricacao;
    private JTable tblProdutos;
    private DefaultTableModel tableModel;
    // Componentes para itens componentes
  private JTable tblItensComponentes;
  private DefaultTableModel itensTableModel;
  private JComboBox<com.scmflusao.model.Item> cbItensDisponiveis;
  private JButton btnAdicionarItem;
  private JButton btnRemoverItem;
  private JButton btnListarItens;
  private JButton btnSalvar;
  private JButton btnConsultar;
  private JButton btnAlterar;
  private JButton btnLimpar;
  private JButton btnExcluir;
    private JButton btnBuscarPorNome;
    private JButton btnBuscarPorSituacao;
    private final DecimalFormat decimalFormat;
    private Long produtoIdEdicao; // ID do produto sendo editado
    private BigDecimal precoBase; // Preço base para somar/remover itens

    public ProdutoView() {
        setTitle("Gestão de Produtos");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Formatação compatível com pt-BR (milhar '.' e decimal ',')
        DecimalFormatSymbols brSymbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        brSymbols.setGroupingSeparator('.');
        brSymbols.setDecimalSeparator(',');
        decimalFormat = new DecimalFormat("#,##0.00", brSymbols);
        initComponents();
    }

    private void initComponents() {
        // Painel de formulário
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        formPanel.add(txtDescricao);

        formPanel.add(new JLabel("Preço (R$):"));
        txtPreco = new JTextField();
        txtPreco.setEditable(false);
        txtPreco.setBackground(Color.LIGHT_GRAY);
        formPanel.add(txtPreco);

        formPanel.add(new JLabel("Situação:"));
        cbSituacao = new JComboBox<>(SituacaoProduto.values());
        formPanel.add(cbSituacao);

        formPanel.add(new JLabel("Cidade de Fabricação:"));
        cbCidadeFabricacao = new JComboBox<>();
        formPanel.add(cbCidadeFabricacao);

        // Painel de botões principais
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSalvar = new JButton("Salvar");
        btnConsultar = new JButton("Consultar");
        btnAlterar = new JButton("Alterar");
        btnLimpar = new JButton("Limpar");
        btnExcluir = new JButton("Excluir");
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnConsultar);
        buttonPanel.add(btnAlterar);
        buttonPanel.add(btnLimpar);
        buttonPanel.add(btnExcluir);

        // Painel de botões de busca
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnBuscarPorNome = new JButton("Buscar por Nome");
        btnBuscarPorSituacao = new JButton("Buscar por Situação");
        searchPanel.add(btnBuscarPorNome);
        searchPanel.add(btnBuscarPorSituacao);

        // Tabela de produtos
        String[] colunas = {"ID", "Nome", "Descrição", "Preço (R$)", "Situação", "Cidade Fabricação", "Estado", "Data Criação", "Data Atualização"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProdutos = new JTable(tableModel);
        tblProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblProdutos);

        // Seção de Itens Componentes
        JPanel itensPanel = new JPanel(new BorderLayout());
        itensPanel.setBorder(BorderFactory.createTitledBorder("Itens Componentes"));
        
        // Painel de controles para itens
        JPanel itensControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itensControlPanel.add(new JLabel("Item:"));
        cbItensDisponiveis = new JComboBox<>();
        // Aumenta a largura para visualizar melhor o nome/descrição do item
        cbItensDisponiveis.setPreferredSize(new Dimension(350, 28));
        itensControlPanel.add(cbItensDisponiveis);
        
        btnAdicionarItem = new JButton("Adicionar Item");
        btnRemoverItem = new JButton("Remover Item");
        btnListarItens = new JButton("Listar Itens");
        itensControlPanel.add(btnAdicionarItem);
        itensControlPanel.add(btnRemoverItem);
        itensControlPanel.add(btnListarItens);
        
        // Tabela de itens componentes
        String[] colunasItens = {"ID", "Nome", "Descrição", "Quantidade", "Valor Unitário (R$)", "Peso (kg)"};
        itensTableModel = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblItensComponentes = new JTable(itensTableModel);
        tblItensComponentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Removido cálculo automático ao mudar itens:
        // O preço só será recalculado explicitamente pelos botões Adicionar/Remover.
        JScrollPane itensScrollPane = new JScrollPane(tblItensComponentes);
        itensScrollPane.setPreferredSize(new Dimension(0, 150));
        
        itensPanel.add(itensControlPanel, BorderLayout.NORTH);
        itensPanel.add(itensScrollPane, BorderLayout.CENTER);

        // Layout principal
        setLayout(new BorderLayout());
        
        // Painel superior para formulário e botões
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonsContainer = new JPanel(new BorderLayout());
        buttonsContainer.add(buttonPanel, BorderLayout.NORTH);
        buttonsContainer.add(searchPanel, BorderLayout.SOUTH);
        topPanel.add(buttonsContainer, BorderLayout.SOUTH);
        
        // Painel central com itens e tabela de produtos
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(itensPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Adiciona os painéis ao frame
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        
        // Define tamanhos preferidos
        topPanel.setPreferredSize(new Dimension(getWidth(), 250));
        scrollPane.setPreferredSize(new Dimension(getWidth(), 450));
    }

    public void preencherCidades(List<Cidade> cidades) {
        cbCidadeFabricacao.removeAllItems();
        for (Cidade cidade : cidades) {
            cbCidadeFabricacao.addItem(cidade);
        }
    }

    public void preencherTabela(List<Produto> produtos) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (Produto produto : produtos) {
            Object[] row = {
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                "R$ " + decimalFormat.format(produto.getPreco()),
                produto.getSituacao().getDescricao(),
                produto.getCidadeFabricacao().getNome(),
                produto.getCidadeFabricacao().getEstado(),
                produto.getDataCriacao() != null ? produto.getDataCriacao().format(formatter) : "",
                produto.getDataAtualizacao() != null ? produto.getDataAtualizacao().format(formatter) : ""
            };
            tableModel.addRow(row);
        }
    }

    public Produto getProdutoFromForm() throws NumberFormatException {
        Produto produto = new Produto();
        produto.setId(produtoIdEdicao); // Define o ID se estiver editando
        produto.setNome(txtNome.getText().trim());
        produto.setDescricao(txtDescricao.getText().trim());
        
        // Trata o campo preço removendo formatação de moeda
        String precoStr = txtPreco.getText().trim();
        if (precoStr.isEmpty()) {
            precoStr = "0";
        }
        // Remove pontos de milhares e substitui vírgula por ponto decimal
        precoStr = precoStr.replace(".", "").replace(",", ".");
        produto.setPreco(new BigDecimal(precoStr));
        
        produto.setSituacao((SituacaoProduto) cbSituacao.getSelectedItem());
        produto.setCidadeFabricacao((Cidade) cbCidadeFabricacao.getSelectedItem());
        return produto;
    }

    public void limparFormulario() {
        produtoIdEdicao = null; // Limpa o ID de edição
        precoBase = null; // Reseta o preço base
        txtNome.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        cbSituacao.setSelectedIndex(0);
        if (cbCidadeFabricacao.getItemCount() > 0) {
            cbCidadeFabricacao.setSelectedIndex(0);
        }
        limparTabelaItens();
    }

    public void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }

    public void exibirErro(String erro) {
        JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public void setSalvarListener(java.awt.event.ActionListener listener) {
        btnSalvar.addActionListener(listener);
    }

    public void setExcluirListener(java.awt.event.ActionListener listener) {
        btnExcluir.addActionListener(listener);
    }

    public void setLimparListener(java.awt.event.ActionListener listener) {
        btnLimpar.addActionListener(listener);
    }

    public void setConsultarListener(java.awt.event.ActionListener listener) {
        btnConsultar.addActionListener(listener);
    }
    
    public void setAlterarListener(java.awt.event.ActionListener listener) {
        btnAlterar.addActionListener(listener);
    }

    public void setBuscarPorNomeListener(java.awt.event.ActionListener listener) {
        btnBuscarPorNome.addActionListener(listener);
    }

    public void setBuscarPorSituacaoListener(java.awt.event.ActionListener listener) {
        btnBuscarPorSituacao.addActionListener(listener);
    }

    public Long getSelectedProdutoId() {
        int selectedRow = tblProdutos.getSelectedRow();
        if (selectedRow != -1) {
            return (Long) tableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }

    // Seleciona a primeira linha da tabela de produtos, se existir
    public void selecionarPrimeiroProdutoSeExistir() {
        if (tableModel.getRowCount() > 0) {
            tblProdutos.setRowSelectionInterval(0, 0);
        }
    }

    // Seleciona a linha do produto pelo ID (após salvar/atualizar)
    public void selecionarProdutoPorId(Long id) {
        if (id == null) return;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object val = tableModel.getValueAt(i, 0);
            if (val instanceof Long && id.equals((Long) val)) {
                tblProdutos.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
    
    public void preencherFormulario(Produto produto) {
        if (produto != null) {
            produtoIdEdicao = produto.getId(); // Armazena o ID para edição
            // Base do preço = preço salvo menos custo atual dos itens
            try {
                java.math.BigDecimal precoSalvo = produto.getPreco() != null ? produto.getPreco() : java.math.BigDecimal.ZERO;
                java.math.BigDecimal custoItens = produto.getCustoTotal();
                if (custoItens == null) custoItens = java.math.BigDecimal.ZERO;
                precoBase = precoSalvo.subtract(custoItens);
                if (precoBase.compareTo(java.math.BigDecimal.ZERO) < 0) {
                    precoBase = java.math.BigDecimal.ZERO;
                }
            } catch (Exception ignore) {
                precoBase = java.math.BigDecimal.ZERO;
            }
            txtNome.setText(produto.getNome());
            txtDescricao.setText(produto.getDescricao());
            // Usa a mesma formatação do campo para evitar perda do separador decimal
            txtPreco.setText(decimalFormat.format(produto.getPreco()));
            
            cbSituacao.setSelectedItem(produto.getSituacao());
            
            for (int i = 0; i < cbCidadeFabricacao.getItemCount(); i++) {
                Cidade cidade = cbCidadeFabricacao.getItemAt(i);
                if (cidade.getId().equals(produto.getCidadeFabricacao().getId())) {
                    cbCidadeFabricacao.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public String solicitarNomeBusca() {
        return JOptionPane.showInputDialog(this, "Digite o nome do produto para busca:", "Buscar Produto", JOptionPane.QUESTION_MESSAGE);
    }

    public SituacaoProduto solicitarSituacaoBusca() {
        return (SituacaoProduto) JOptionPane.showInputDialog(
            this,
            "Selecione a situação do produto:",
            "Buscar por Situação",
            JOptionPane.QUESTION_MESSAGE,
            null,
            SituacaoProduto.values(),
            SituacaoProduto.CRIADO
        );
    }

    public boolean confirmarExclusao(String nomeProduto) {
        int opcao = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir o produto '" + nomeProduto + "'?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return opcao == JOptionPane.YES_OPTION;
    }

    public void habilitarBotaoAlterar(boolean habilitar) {
        btnAlterar.setEnabled(habilitar);
    }

    public void habilitarBotaoExcluir(boolean habilitar) {
        btnExcluir.setEnabled(habilitar);
    }

    public void selecionarLinhaPorId(Long id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long idLinha = (Long) tableModel.getValueAt(i, 0);
            if (idLinha.equals(id)) {
                tblProdutos.setRowSelectionInterval(i, i);
                tblProdutos.scrollRectToVisible(tblProdutos.getCellRect(i, 0, true));
                break;
            }
        }
    }

    public void adicionarSelectionListener(javax.swing.event.ListSelectionListener listener) {
        tblProdutos.getSelectionModel().addListSelectionListener(listener);
    }

    public boolean temProdutoSelecionado() {
        return tblProdutos.getSelectedRow() != -1;
    }

    public String getNomeProdutoSelecionado() {
        int selectedRow = tblProdutos.getSelectedRow();
        if (selectedRow != -1) {
            return (String) tableModel.getValueAt(selectedRow, 1);
        }
        return null;
    }

    public void configurarTabelaParaSelecao() {
        tblProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblProdutos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean temSelecao = temProdutoSelecionado();
                habilitarBotaoAlterar(temSelecao);
                habilitarBotaoExcluir(temSelecao);
            }
        });
        
        // Inicialmente desabilita os botões
        habilitarBotaoAlterar(false);
        habilitarBotaoExcluir(false);
    }
    
    // Métodos para gerenciar itens componentes
    public void preencherItensDisponiveis(List<Item> itens) {
        cbItensDisponiveis.removeAllItems();
        for (Item item : itens) {
            cbItensDisponiveis.addItem(item);
        }
    }
    
    public void preencherTabelaItens(List<Item> itens) {
        itensTableModel.setRowCount(0);
        if (itens == null) return;
        for (Item item : itens) {
            Integer qtd = item.getQuantidade() != null ? item.getQuantidade() : 1;
            java.math.BigDecimal valor = item.getValorUnitario() != null ? item.getValorUnitario() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal peso = item.getPesoUnitario() != null ? item.getPesoUnitario() : java.math.BigDecimal.ZERO;
            Object[] row = {
                item.getId(),
                item.getNome(),
                item.getDescricao(),
                qtd,
                decimalFormat.format(valor),
                decimalFormat.format(peso)
            };
            itensTableModel.addRow(row);
        }
    }
    
    public Item getItemSelecionado() {
        return (Item) cbItensDisponiveis.getSelectedItem();
    }
    
    public Long getItemSelecionadoTabela() {
        int selectedRow = tblItensComponentes.getSelectedRow();
        if (selectedRow != -1) {
            return (Long) itensTableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }

    // Novo: retorna o índice da linha selecionada para remoção precisa
    public Integer getLinhaSelecionadaTabela() {
        int selectedRow = tblItensComponentes.getSelectedRow();
        return selectedRow >= 0 ? selectedRow : null;
    }
    
    public void setAdicionarItemListener(java.awt.event.ActionListener listener) {
        btnAdicionarItem.addActionListener(listener);
    }
    
    public void setRemoverItemListener(java.awt.event.ActionListener listener) {
        btnRemoverItem.addActionListener(listener);
    }
    
    public void setListarItensListener(java.awt.event.ActionListener listener) {
        btnListarItens.addActionListener(listener);
    }
    
    public void limparTabelaItens() {
        itensTableModel.setRowCount(0);
    }
    
    // Método público para calcular o preço automaticamente (soma dos itens)
    public void calcularPrecoAutomatico() {
        BigDecimal somaItens = BigDecimal.ZERO;

        for (int i = 0; i < itensTableModel.getRowCount(); i++) {
            String valorStr = itensTableModel.getValueAt(i, 4).toString();
            valorStr = valorStr.replace(".", "").replace(",", ".");
            try {
                BigDecimal valorItem = new BigDecimal(valorStr);
                int quantidade = 1;
                Object qtdObj = itensTableModel.getValueAt(i, 3);
                if (qtdObj != null) {
                    try { quantidade = Integer.parseInt(qtdObj.toString()); } catch (NumberFormatException ignore) {}
                }
                somaItens = somaItens.add(valorItem.multiply(BigDecimal.valueOf(quantidade)));
            } catch (NumberFormatException e) {
                // Ignora itens com valores inválidos
            }
        }

        // Preço passa a ser exclusivamente a soma dos itens
        txtPreco.setText(decimalFormat.format(somaItens));
    }
    
    // Método para obter o preço calculado dos itens
    public BigDecimal getPrecoCalculado() {
        BigDecimal precoTotal = BigDecimal.ZERO;
        
        for (int i = 0; i < itensTableModel.getRowCount(); i++) {
            String valorStr = itensTableModel.getValueAt(i, 4).toString();
            // Remove formatação de moeda e converte para BigDecimal
            valorStr = valorStr.replace(".", "").replace(",", ".");
            try {
                BigDecimal valorItem = new BigDecimal(valorStr);
                int quantidade = 1;
                Object qtdObj = itensTableModel.getValueAt(i, 3);
                if (qtdObj != null) {
                    try { quantidade = Integer.parseInt(qtdObj.toString()); } catch (NumberFormatException ignore) {}
                }
                precoTotal = precoTotal.add(valorItem.multiply(BigDecimal.valueOf(quantidade)));
            } catch (NumberFormatException e) {
                // Ignora itens com valores inválidos
            }
        }
        
        return precoTotal;
    }
}