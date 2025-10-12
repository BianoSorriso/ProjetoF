package com.scmflusao.view;

import com.scmflusao.model.Armazem;
import com.scmflusao.model.Cidade;

import javax.swing.*;
import java.math.BigDecimal;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class ArmazemView extends JFrame {
    private JTextField txtNome;
    private JTextField txtEndereco;
    private JTextField txtCapacidade;
    private JTextField txtOcupacao;
    private JComboBox<String> cbAtivo;
    private JComboBox<Cidade> cbCidade;
    private JTable tblArmazens;
    private DefaultTableModel tableModel;
    private JButton btnSalvar;
    private JButton btnConsultar;
    private JButton btnAlterar;
    private JButton btnLimpar;
    private JButton btnExcluir;
    private final DecimalFormat decimalFormat;
    private Long armazemIdEdicao; // ID do armazém sendo editado

    public ArmazemView() {
        setTitle("Gestão de Armazéns");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        decimalFormat = new DecimalFormat("#,##0.00");
        initComponents();
    }

    private void initComponents() {
        // Painel de formulário
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel("Endereço:"));
        txtEndereco = new JTextField();
        formPanel.add(txtEndereco);

        formPanel.add(new JLabel("Capacidade (m³):"));
        txtCapacidade = new JTextField();
        formPanel.add(txtCapacidade);

        formPanel.add(new JLabel("Ocupação (m³):"));
        txtOcupacao = new JTextField();
        formPanel.add(txtOcupacao);

        formPanel.add(new JLabel("Ativo:"));
        cbAtivo = new JComboBox<>(new String[]{"Sim", "Não"});
        formPanel.add(cbAtivo);

        formPanel.add(new JLabel("Cidade:"));
        cbCidade = new JComboBox<>();
        formPanel.add(cbCidade);

        // Painel de botões
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

        // Tabela de armazéns
        String[] colunas = {"ID", "Nome", "Endereço", "Capacidade (m³)", "Ocupação (m³)", "Disponível (m³)", "% Ocupado", "Ativo", "Cidade", "Estado"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblArmazens = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblArmazens);

        // Layout principal
        setLayout(new BorderLayout());
        
        // Painel superior para formulário
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Adiciona os painéis ao frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Define tamanhos preferidos
        topPanel.setPreferredSize(new Dimension(getWidth(), 220));
        scrollPane.setPreferredSize(new Dimension(getWidth(), 380));
    }

    public void preencherCidades(List<Cidade> cidades) {
        cbCidade.removeAllItems();
        for (Cidade cidade : cidades) {
            cbCidade.addItem(cidade);
        }
    }

    public void preencherTabela(List<Armazem> armazens) {
        tableModel.setRowCount(0);
        for (Armazem armazem : armazens) {
            BigDecimal espacoDisponivel = armazem.getCapacidadeDisponivel();
            double percentualOcupado = armazem.getOcupacao().doubleValue() / armazem.getCapacidade().doubleValue() * 100;

            Object[] row = {
                armazem.getId(),
                armazem.getNome(),
                armazem.getEndereco(),
                decimalFormat.format(armazem.getCapacidade()),
                decimalFormat.format(armazem.getOcupacao()),
                decimalFormat.format(espacoDisponivel),
                decimalFormat.format(percentualOcupado) + "%",
                armazem.isAtivo() ? "Sim" : "Não",
                armazem.getCidade().getNome(),
                armazem.getCidade().getEstado()
            };
            tableModel.addRow(row);
        }
    }

    public Armazem getArmazemFromForm() throws NumberFormatException {
        Armazem armazem = new Armazem();
        armazem.setId(armazemIdEdicao); // Define o ID se estiver editando
        armazem.setNome(txtNome.getText());
        armazem.setEndereco(txtEndereco.getText());
        armazem.setCapacidade(new BigDecimal(txtCapacidade.getText().replace(",", ".")));
        armazem.setOcupacao(new BigDecimal(txtOcupacao.getText().replace(",", ".")));
        armazem.setAtivo(cbAtivo.getSelectedItem().equals("Sim"));
        armazem.setCidade((Cidade) cbCidade.getSelectedItem());
        return armazem;
    }

    public void limparFormulario() {
        armazemIdEdicao = null; // Limpa o ID de edição
        txtNome.setText("");
        txtEndereco.setText("");
        txtCapacidade.setText("");
        txtOcupacao.setText("");
        cbAtivo.setSelectedIndex(0);
        if (cbCidade.getItemCount() > 0) {
            cbCidade.setSelectedIndex(0);
        }
    }

    public void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
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

    public Long getSelectedArmazemId() {
        int selectedRow = tblArmazens.getSelectedRow();
        if (selectedRow != -1) {
            return (Long) tableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }
    
    public void setConsultarListener(java.awt.event.ActionListener listener) {
        btnConsultar.addActionListener(listener);
    }
    
    public void setAlterarListener(java.awt.event.ActionListener listener) {
        btnAlterar.addActionListener(listener);
    }
    
    public void preencherFormulario(Armazem armazem) {
        if (armazem != null) {
            armazemIdEdicao = armazem.getId(); // Armazena o ID para edição
            txtNome.setText(armazem.getNome());
            txtEndereco.setText(armazem.getEndereco());
            txtCapacidade.setText(String.valueOf(armazem.getCapacidade()));
            txtOcupacao.setText(String.valueOf(armazem.getOcupacao()));
            cbAtivo.setSelectedItem(armazem.isAtivo() ? "Sim" : "Não");
            
            for (int i = 0; i < cbCidade.getItemCount(); i++) {
                Cidade cidade = cbCidade.getItemAt(i);
                if (cidade.getId().equals(armazem.getCidade().getId())) {
                    cbCidade.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}