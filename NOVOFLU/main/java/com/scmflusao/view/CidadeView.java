package com.scmflusao.view;

import com.scmflusao.model.Cidade;
import com.scmflusao.model.Pais;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CidadeView extends JFrame {
    private JTextField txtNome;
    private JTextField txtEstado;
    private JComboBox<Pais> cbPais;
    private JCheckBox chkEhFabrica;
    private JCheckBox chkEhOrigem;
    private JTable tblCidades;
    private DefaultTableModel tableModel;
    private JButton btnSalvar;
    private JButton btnConsultar;
    private JButton btnAlterar;
    private JButton btnLimpar;
    private JButton btnExcluir;
    private Long cidadeIdEdicao; // ID da cidade sendo editada

    public CidadeView() {
        setTitle("Gestão de Cidades");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        // Painel de formulário
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel("Estado:"));
        txtEstado = new JTextField();
        formPanel.add(txtEstado);

        formPanel.add(new JLabel("País:"));
        cbPais = new JComboBox<>();
        formPanel.add(cbPais);

        formPanel.add(new JLabel("É Fábrica:"));
        chkEhFabrica = new JCheckBox();
        formPanel.add(chkEhFabrica);

        formPanel.add(new JLabel("É Origem:"));
        chkEhOrigem = new JCheckBox();
        formPanel.add(chkEhOrigem);

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

        // Tabela de cidades
        String[] colunas = {"ID", "Nome", "Estado", "País", "É Fábrica", "É Origem", "Qtd. Armazéns"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 4 || columnIndex == 5) {
                    return Boolean.class;
                }
                return super.getColumnClass(columnIndex);
            }
        };
        tblCidades = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblCidades);

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
        topPanel.setPreferredSize(new Dimension(getWidth(), 200));
        scrollPane.setPreferredSize(new Dimension(getWidth(), 400));
    }

    public void preencherPaises(List<Pais> paises) {
        cbPais.removeAllItems();
        for (Pais pais : paises) {
            cbPais.addItem(pais);
        }
    }

    public void preencherTabela(List<Cidade> cidades) {
        tableModel.setRowCount(0);
        for (Cidade cidade : cidades) {
            Object[] row = {
                cidade.getId(),
                cidade.getNome(),
                cidade.getEstado(),
                cidade.getPais().getNome(),
                cidade.isEhFabrica(),
                cidade.isEhOrigem(),
                cidade.getArmazens() != null ? cidade.getArmazens().size() : 0
            };
            tableModel.addRow(row);
        }
    }

    public Cidade getCidadeFromForm() {
        Cidade cidade = new Cidade();
        cidade.setId(cidadeIdEdicao); // Define o ID se estiver editando
        cidade.setNome(txtNome.getText());
        cidade.setEstado(txtEstado.getText());
        cidade.setPais((Pais) cbPais.getSelectedItem());
        cidade.setEhFabrica(chkEhFabrica.isSelected());
        cidade.setEhOrigem(chkEhOrigem.isSelected());
        return cidade;
    }

    public void limparFormulario() {
        cidadeIdEdicao = null; // Limpa o ID de edição
        txtNome.setText("");
        txtEstado.setText("");
        if (cbPais.getItemCount() > 0) {
            cbPais.setSelectedIndex(0);
        }
        chkEhFabrica.setSelected(false);
        chkEhOrigem.setSelected(false);
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

    public Long getSelectedCidadeId() {
        int selectedRow = tblCidades.getSelectedRow();
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
    
    public void preencherFormulario(Cidade cidade) {
        cidadeIdEdicao = cidade.getId(); // Define o ID para edição
        txtNome.setText(cidade.getNome());
        txtEstado.setText(cidade.getEstado());
        
        for (int i = 0; i < cbPais.getItemCount(); i++) {
            Pais pais = cbPais.getItemAt(i);
            if (pais.getId().equals(cidade.getPais().getId())) {
                cbPais.setSelectedIndex(i);
                break;
            }
        }
        
        chkEhFabrica.setSelected(cidade.isEhFabrica());
        chkEhOrigem.setSelected(cidade.isEhOrigem());
    }
}