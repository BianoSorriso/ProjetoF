package com.scmflusao.view;

import com.scmflusao.model.EmpresaParceira;
import com.scmflusao.model.TipoServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.util.List;
import java.text.ParseException;

public class EmpresaParceiraView extends JFrame {
    private JTextField txtNome;
    private JFormattedTextField txtCnpj;
    private JTextField txtEndereco;
    private JFormattedTextField txtTelefone;
    private JTextField txtEmail;
    private JComboBox<TipoServico> cbTipoServico;
    private JComboBox<String> cbAtivo;
    // Filtros (aba Consulta)
    private JTextField txtFiltroNome;
    private JFormattedTextField txtFiltroCnpj;
    private JComboBox<TipoServico> cbFiltroTipoServico;
    private JComboBox<String> cbFiltroAtivo;
    private JTable tblEmpresas;
    private DefaultTableModel tableModel;
    private JButton btnConsultar;
    private JButton btnAlterar;
    private JButton btnSalvar;
    private JButton btnLimpar;
    private JButton btnExcluir;
    private Long empresaIdEdicao; // ID da empresa sendo editada

    public EmpresaParceiraView() {
        setTitle("Gestão de Empresas Parceiras");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();
        // Painel de formulário (Cadastro)
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel("CNPJ:"));
        try {
            txtCnpj = new JFormattedTextField(new MaskFormatter("##.###.###/####-##"));
        } catch (ParseException e) {
            txtCnpj = new JFormattedTextField();
        }
        formPanel.add(txtCnpj);

        formPanel.add(new JLabel("Endereço:"));
        txtEndereco = new JTextField();
        formPanel.add(txtEndereco);

        formPanel.add(new JLabel("Telefone:"));
        try {
            txtTelefone = new JFormattedTextField(new MaskFormatter("(##) #####-####"));
        } catch (ParseException e) {
            txtTelefone = new JFormattedTextField();
        }
        formPanel.add(txtTelefone);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Tipo de Serviço:"));
        cbTipoServico = new JComboBox<>(TipoServico.values());
        formPanel.add(cbTipoServico);

        formPanel.add(new JLabel("Ativo:"));
        cbAtivo = new JComboBox<>(new String[]{"Sim", "Não"});
        formPanel.add(cbAtivo);

        // Botões Cadastro
        JPanel buttonPanelCadastro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSalvar = new JButton("Salvar");
        btnAlterar = new JButton("Alterar");
        btnLimpar = new JButton("Limpar");
        btnExcluir = new JButton("Excluir");
        buttonPanelCadastro.add(btnSalvar);
        buttonPanelCadastro.add(btnAlterar);
        buttonPanelCadastro.add(btnLimpar);
        buttonPanelCadastro.add(btnExcluir);

        // Tabela de empresas (Consulta)
        String[] colunas = {"ID", "Nome", "CNPJ", "Endereço", "Telefone", "Email", "Tipo de Serviço", "Ativo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblEmpresas = new JTable(tableModel);
        tblEmpresas.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(tblEmpresas);

        // Abas
        JPanel cadastroTab = new JPanel(new BorderLayout());
        cadastroTab.add(formPanel, BorderLayout.CENTER);
        cadastroTab.add(buttonPanelCadastro, BorderLayout.SOUTH);

        // Filtros (Consulta)
        JPanel filtrosPanel = new JPanel(new GridLayout(2, 4, 8, 8));
        filtrosPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        filtrosPanel.add(new JLabel("Nome:"));
        txtFiltroNome = new JTextField();
        filtrosPanel.add(txtFiltroNome);
        filtrosPanel.add(new JLabel("CNPJ:"));
        try {
            txtFiltroCnpj = new JFormattedTextField(new MaskFormatter("##.###.###/####-##"));
        } catch (ParseException e) {
            txtFiltroCnpj = new JFormattedTextField();
        }
        filtrosPanel.add(txtFiltroCnpj);
        filtrosPanel.add(new JLabel("Tipo de Serviço:"));
        cbFiltroTipoServico = new JComboBox<>(TipoServico.values());
        filtrosPanel.add(cbFiltroTipoServico);
        filtrosPanel.add(new JLabel("Ativo:"));
        cbFiltroAtivo = new JComboBox<>(new String[]{"Todos", "Sim", "Não"});
        filtrosPanel.add(cbFiltroAtivo);

        JPanel buttonPanelConsulta = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConsultar = new JButton("Consultar");
        buttonPanelConsulta.add(btnConsultar);

        JPanel consultaTop = new JPanel(new BorderLayout());
        consultaTop.add(filtrosPanel, BorderLayout.CENTER);
        consultaTop.add(buttonPanelConsulta, BorderLayout.SOUTH);

        JPanel consultaTab = new JPanel(new BorderLayout());
        consultaTab.add(consultaTop, BorderLayout.NORTH);
        consultaTab.add(scrollPane, BorderLayout.CENTER);

        tabs.addTab("Cadastro", cadastroTab);
        tabs.addTab("Consulta", consultaTab);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    public void preencherTabela(List<EmpresaParceira> empresas) {
        tableModel.setRowCount(0);
        for (EmpresaParceira empresa : empresas) {
            Object[] row = {
                empresa.getId(),
                empresa.getNome(),
                empresa.getCnpj(),
                empresa.getEndereco(),
                empresa.getTelefone(),
                empresa.getEmail(),
                empresa.getTipoServico().toString(),
                empresa.isAtivo() ? "Sim" : "Não"
            };
            tableModel.addRow(row);
        }
    }

    public EmpresaParceira getEmpresaFromForm() {
        EmpresaParceira empresa = new EmpresaParceira();
        empresa.setId(empresaIdEdicao); // Define o ID se estiver editando
        empresa.setNome(txtNome.getText());
        empresa.setCnpj(txtCnpj.getText());
        empresa.setEndereco(txtEndereco.getText());
        empresa.setTelefone(txtTelefone.getText());
        empresa.setEmail(txtEmail.getText());
        empresa.setTipoServico((TipoServico) cbTipoServico.getSelectedItem());
        empresa.setAtivo(cbAtivo.getSelectedItem().equals("Sim"));
        return empresa;
    }

    public void limparFormulario() {
        empresaIdEdicao = null; // Limpa o ID de edição
        txtNome.setText("");
        txtCnpj.setValue(null);
        txtEndereco.setText("");
        txtTelefone.setValue(null);
        txtEmail.setText("");
        cbTipoServico.setSelectedIndex(0);
        cbAtivo.setSelectedIndex(0);
    }

    public void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }

    public void setSalvarListener(java.awt.event.ActionListener listener) {
        btnSalvar.addActionListener(listener);
    }

    public void setConsultarListener(java.awt.event.ActionListener listener) {
        btnConsultar.addActionListener(listener);
    }

    public void setAlterarListener(java.awt.event.ActionListener listener) {
        btnAlterar.addActionListener(listener);
    }

    public void setExcluirListener(java.awt.event.ActionListener listener) {
        btnExcluir.addActionListener(listener);
    }

    public void setLimparListener(java.awt.event.ActionListener listener) {
        btnLimpar.addActionListener(listener);
    }

    public Long getSelectedEmpresaId() {
        int selectedRow = tblEmpresas.getSelectedRow();
        if (selectedRow != -1) {
            return (Long) tableModel.getValueAt(selectedRow, 0);
        }
        return null;
    }
    
    public void preencherFormulario(EmpresaParceira empresa) {
        if (empresa != null) {
            empresaIdEdicao = empresa.getId(); // Armazena o ID para edição
            txtNome.setText(empresa.getNome());
            txtCnpj.setText(empresa.getCnpj());
            txtEndereco.setText(empresa.getEndereco());
            txtTelefone.setText(empresa.getTelefone());
            txtEmail.setText(empresa.getEmail());
            cbTipoServico.setSelectedItem(empresa.getTipoServico());
            cbAtivo.setSelectedItem(empresa.isAtivo() ? "Sim" : "Não");
        }
    }

    // Getters de filtros
    public String getFiltroNome() { return txtFiltroNome != null ? txtFiltroNome.getText() : ""; }
    public String getFiltroCnpj() { return txtFiltroCnpj != null ? txtFiltroCnpj.getText() : ""; }
    public TipoServico getFiltroTipoServico() { return cbFiltroTipoServico != null ? (TipoServico) cbFiltroTipoServico.getSelectedItem() : null; }
    public String getFiltroAtivo() { return cbFiltroAtivo != null ? (String) cbFiltroAtivo.getSelectedItem() : "Todos"; }
}