package com.scmflusao.view;

import com.scmflusao.model.Item;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.SituacaoItem;

import javax.swing.*;
import java.math.BigDecimal;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class ItemView extends JFrame {
    private JTextField txtNome;
    private JTextField txtDescricao;
    private JTextField txtPeso;
    private JTextField txtQuantidade;
    private JTextField txtValorUnitario;
    private JComboBox<SituacaoItem> cbSituacao;
    private JComboBox<Cidade> cbCidadeOrigem;
    private JTable tblItens;
    private DefaultTableModel tableModel;
    private JButton btnSalvar;
    private JButton btnConsultar;
    private JButton btnAlterar;
    private JButton btnLimpar;
    private JButton btnExcluir;
    private JButton btnBuscarPorNome;
    private JButton btnBuscarPorSituacao;
    private JButton btnCalcularValorTotal;
    private final DecimalFormat decimalFormat;
    private final DecimalFormat pesoFormat;
    private Long itemIdEdicao; // ID do item sendo editado

    public ItemView() {
        setTitle("Gestão de Itens");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        decimalFormat = new DecimalFormat("#,##0.00");
        pesoFormat = new DecimalFormat("#,##0.000");
        initComponents();
    }

    private void initComponents() {
        // Painel de formulário
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel("Descrição:"));
        txtDescricao = new JTextField();
        formPanel.add(txtDescricao);

        formPanel.add(new JLabel("Peso (kg):"));
        txtPeso = new JTextField();
        formPanel.add(txtPeso);

        formPanel.add(new JLabel("Quantidade:"));
        txtQuantidade = new JTextField();
        formPanel.add(txtQuantidade);

        formPanel.add(new JLabel("Valor Unitário (R$):"));
        txtValorUnitario = new JTextField();
        formPanel.add(txtValorUnitario);

        formPanel.add(new JLabel("Situação:"));
        cbSituacao = new JComboBox<>(SituacaoItem.values());
        formPanel.add(cbSituacao);

        formPanel.add(new JLabel("Cidade de Origem:"));
        cbCidadeOrigem = new JComboBox<>();
        formPanel.add(cbCidadeOrigem);

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

        // Painel de botões de busca e ações
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnBuscarPorNome = new JButton("Buscar por Nome");
        btnBuscarPorSituacao = new JButton("Buscar por Situação");
        btnCalcularValorTotal = new JButton("Calcular Valor Total");
        searchPanel.add(btnBuscarPorNome);
        searchPanel.add(btnBuscarPorSituacao);
        searchPanel.add(btnCalcularValorTotal);

        // Tabela de itens
        String[] colunas = {"ID", "Nome", "Descrição", "Peso (kg)", "Quantidade", "Valor Unit. (R$)", "Valor Total (R$)", "Situação", "Cidade Origem", "Estado", "Data Entrada"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblItens = new JTable(tableModel);
        tblItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblItens);

        // Layout principal
        setLayout(new BorderLayout());
        
        // Painel superior para formulário e botões
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonsContainer = new JPanel(new BorderLayout());
        buttonsContainer.add(buttonPanel, BorderLayout.NORTH);
        buttonsContainer.add(searchPanel, BorderLayout.SOUTH);
        topPanel.add(buttonsContainer, BorderLayout.SOUTH);
        
        // Adiciona os painéis ao frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Define tamanhos preferidos
        topPanel.setPreferredSize(new Dimension(getWidth(), 300));
        scrollPane.setPreferredSize(new Dimension(getWidth(), 450));
    }

    public void preencherCidades(List<Cidade> cidades) {
        cbCidadeOrigem.removeAllItems();
        for (Cidade cidade : cidades) {
            cbCidadeOrigem.addItem(cidade);
        }
    }

    public void preencherTabela(List<Item> itens) {
        tableModel.setRowCount(0);
        for (Item item : itens) {
            BigDecimal valorTotal = item.calcularValorTotal();
            
            Object[] row = {
                item.getId(),
                item.getNome(),
                item.getDescricao(),
                pesoFormat.format(item.getPeso()) + " kg",
                item.getQuantidade(),
                "R$ " + decimalFormat.format(item.getValorUnitario()),
                "R$ " + decimalFormat.format(valorTotal),
                item.getSituacao().getDescricao(),
                item.getCidadeOrigem().getNome(),
                item.getCidadeOrigem().getEstado(),
                item.getDataEntrada() != null ? item.getDataEntrada().toString() : ""
            };
            tableModel.addRow(row);
        }
    }

    public Item getItemFromForm() throws NumberFormatException {
        Item item = new Item();
        item.setId(itemIdEdicao); // Define o ID se estiver editando
        item.setNome(txtNome.getText().trim());
        item.setDescricao(txtDescricao.getText().trim());
        item.setPeso(new BigDecimal(txtPeso.getText().replace(",", ".")));
        item.setQuantidade(Integer.parseInt(txtQuantidade.getText()));
        item.setValorUnitario(new BigDecimal(txtValorUnitario.getText().replace(",", ".")));
        item.setSituacao((SituacaoItem) cbSituacao.getSelectedItem());
        item.setCidadeOrigem((Cidade) cbCidadeOrigem.getSelectedItem());
        return item;
    }

    public void limparFormulario() {
        itemIdEdicao = null; // Limpa o ID de edição
        txtNome.setText("");
        txtDescricao.setText("");
        txtPeso.setText("");
        txtQuantidade.setText("");
        txtValorUnitario.setText("");
        cbSituacao.setSelectedIndex(0);
        if (cbCidadeOrigem.getItemCount() > 0) {
            cbCidadeOrigem.setSelectedIndex(0);
        }
    }

    public void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }

    public void exibirErro(String erro) {
        JOptionPane.showMessageDialog(this, erro, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public void exibirValorTotal(BigDecimal valorTotal) {
        JOptionPane.showMessageDialog(this, 
            "Valor Total do Item: R$ " + decimalFormat.format(valorTotal), 
            "Valor Total", 
            JOptionPane.INFORMATION_MESSAGE);
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

    public void setCalcularValorTotalListener(java.awt.event.ActionListener listener) {
        btnCalcularValorTotal.addActionListener(listener);
    }

    public Long getSelectedItemId() {
        int selectedRow = tblItens.getSelectedRow();
        if (selectedRow != -1) {
            return (Long) tableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }
    
    public void preencherFormulario(Item item) {
        if (item != null) {
            itemIdEdicao = item.getId(); // Armazena o ID para edição
            txtNome.setText(item.getNome());
            txtDescricao.setText(item.getDescricao());
            txtPeso.setText(String.valueOf(item.getPeso()));
            txtQuantidade.setText(String.valueOf(item.getQuantidade()));
            txtValorUnitario.setText(String.valueOf(item.getValorUnitario()));
            cbSituacao.setSelectedItem(item.getSituacao());
            
            for (int i = 0; i < cbCidadeOrigem.getItemCount(); i++) {
                Cidade cidade = cbCidadeOrigem.getItemAt(i);
                if (cidade.getId().equals(item.getCidadeOrigem().getId())) {
                    cbCidadeOrigem.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public String solicitarNomeBusca() {
        return JOptionPane.showInputDialog(this, "Digite o nome do item para busca:", "Buscar Item", JOptionPane.QUESTION_MESSAGE);
    }

    public SituacaoItem solicitarSituacaoBusca() {
        return (SituacaoItem) JOptionPane.showInputDialog(
            this,
            "Selecione a situação do item:",
            "Buscar por Situação",
            JOptionPane.QUESTION_MESSAGE,
            null,
            SituacaoItem.values(),
            SituacaoItem.REGISTRADO
        );
    }

    public boolean confirmarExclusao(String nomeItem) {
        int opcao = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja excluir o item '" + nomeItem + "'?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return opcao == JOptionPane.YES_OPTION;
    }

    public Integer solicitarNovaQuantidade() {
        String input = JOptionPane.showInputDialog(
            this,
            "Digite a nova quantidade:",
            "Atualizar Quantidade",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                exibirErro("Quantidade deve ser um número inteiro válido.");
            }
        }
        return null;
    }

    public void habilitarBotaoAlterar(boolean habilitar) {
        btnAlterar.setEnabled(habilitar);
    }

    public void habilitarBotaoExcluir(boolean habilitar) {
        btnExcluir.setEnabled(habilitar);
    }

    public void habilitarBotaoCalcularValor(boolean habilitar) {
        btnCalcularValorTotal.setEnabled(habilitar);
    }

    public void selecionarLinhaPorId(Long id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long idLinha = (Long) tableModel.getValueAt(i, 0);
            if (idLinha.equals(id)) {
                tblItens.setRowSelectionInterval(i, i);
                tblItens.scrollRectToVisible(tblItens.getCellRect(i, 0, true));
                break;
            }
        }
    }

    public void adicionarSelectionListener(javax.swing.event.ListSelectionListener listener) {
        tblItens.getSelectionModel().addListSelectionListener(listener);
    }

    public boolean temItemSelecionado() {
        return tblItens.getSelectedRow() != -1;
    }

    public String getNomeItemSelecionado() {
        int selectedRow = tblItens.getSelectedRow();
        if (selectedRow != -1) {
            return (String) tableModel.getValueAt(selectedRow, 1);
        }
        return null;
    }

    public void configurarTabelaParaSelecao() {
        tblItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblItens.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean temSelecao = temItemSelecionado();
                habilitarBotaoAlterar(temSelecao);
                habilitarBotaoExcluir(temSelecao);
                habilitarBotaoCalcularValor(temSelecao);
            }
        });
        
        // Inicialmente desabilita os botões
        habilitarBotaoAlterar(false);
        habilitarBotaoExcluir(false);
        habilitarBotaoCalcularValor(false);
    }

    public void mostrarEstatisticas(long totalItens, BigDecimal valorTotalEstoque) {
        String mensagem = String.format(
            "Estatísticas dos Itens:\n\n" +
            "Total de Itens: %d\n" +
            "Valor Total do Estoque: R$ %s",
            totalItens,
            decimalFormat.format(valorTotalEstoque)
        );
        
        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Estatísticas",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    public BigDecimal solicitarPesoMaximo() {
        String input = JOptionPane.showInputDialog(
            this,
            "Digite o peso máximo (kg):",
            "Filtrar por Peso",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                return new BigDecimal(input.replace(",", "."));
            } catch (NumberFormatException e) {
                exibirErro("Peso deve ser um número válido.");
            }
        }
        return null;
    }

    public Integer solicitarQuantidadeMinima() {
        String input = JOptionPane.showInputDialog(
            this,
            "Digite a quantidade mínima:",
            "Filtrar por Quantidade",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                exibirErro("Quantidade deve ser um número inteiro válido.");
            }
        }
        return null;
    }

    public void preencherProdutos(List<?> produtos) {
        // Método para preencher produtos - implementação depende dos requisitos específicos
        // Por enquanto, apenas um método vazio para resolver o erro de compilação
        if (produtos != null) {
            System.out.println("Produtos carregados: " + produtos.size());
        }
    }
}