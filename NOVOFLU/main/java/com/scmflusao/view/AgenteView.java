package com.scmflusao.view;

import com.scmflusao.model.Agente;
import com.scmflusao.model.EmpresaParceira;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

public class AgenteView extends JFrame {
    private JFormattedTextField txtCpf;
    private JFormattedTextField txtDataNascimento;
    private JFormattedTextField txtTelefone;
    private JTextField txtNome;
    private JTextField txtCargo;
    private JTextField txtEmail;
    private JComboBox<String> cbDisponivel;
    private JComboBox<EmpresaParceira> cbEmpresaParceira;
    // Filtros (aba Consulta)
    private JTextField txtFiltroNome;
    private JTextField txtFiltroCargo;
    private JComboBox<EmpresaParceira> cbFiltroEmpresa;
    private JComboBox<String> cbFiltroDisponivel;
    private JTable tblAgentes;
    private DefaultTableModel tableModel;
    private JButton btnSalvar;
    private JButton btnConsultar;
    private JButton btnAlterar;
    private JButton btnLimpar;
    private JButton btnExcluir;
    private Long agenteIdEdicao; // ID do agente sendo editado

    public AgenteView() {
        setTitle("Gestão de Agentes");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabs = new JTabbedPane();
        // Painel de formulário (Cadastro)
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        formPanel.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        formPanel.add(txtNome);

        formPanel.add(new JLabel("CPF:"));
        try {
            txtCpf = new JFormattedTextField(new MaskFormatter("###.###.###-##"));
        } catch (ParseException e) {
            txtCpf = new JFormattedTextField();
        }
        formPanel.add(txtCpf);

        formPanel.add(new JLabel("Cargo:"));
        txtCargo = new JTextField();
        formPanel.add(txtCargo);

        formPanel.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
        try {
            txtDataNascimento = new JFormattedTextField(new MaskFormatter("##/##/####"));
        } catch (ParseException e) {
            txtDataNascimento = new JFormattedTextField();
        }
        formPanel.add(txtDataNascimento);

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

        formPanel.add(new JLabel("Disponível:"));
        cbDisponivel = new JComboBox<>(new String[]{"Sim", "Não"});
        formPanel.add(cbDisponivel);

        formPanel.add(new JLabel("Empresa Parceira:"));
        cbEmpresaParceira = new JComboBox<>();
        formPanel.add(cbEmpresaParceira);

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

        // Tabela de agentes (Consulta)
        String[] colunas = {"ID", "Nome", "CPF", "Cargo", "Data Nasc.", "Telefone", "Email", "Disponível", "Empresa"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblAgentes = new JTable(tableModel);
        tblAgentes.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(tblAgentes);

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
        filtrosPanel.add(new JLabel("Cargo:"));
        txtFiltroCargo = new JTextField();
        filtrosPanel.add(txtFiltroCargo);
        filtrosPanel.add(new JLabel("Empresa Parceira:"));
        cbFiltroEmpresa = new JComboBox<>();
        filtrosPanel.add(cbFiltroEmpresa);
        filtrosPanel.add(new JLabel("Disponível:"));
        cbFiltroDisponivel = new JComboBox<>(new String[]{"Todos", "Sim", "Não"});
        filtrosPanel.add(cbFiltroDisponivel);

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

    public void preencherEmpresasParceiras(List<EmpresaParceira> empresas) {
        cbEmpresaParceira.removeAllItems();
        if (cbFiltroEmpresa != null) {
            cbFiltroEmpresa.removeAllItems();
            cbFiltroEmpresa.addItem(null);
        }
        for (EmpresaParceira empresa : empresas) {
            cbEmpresaParceira.addItem(empresa);
            if (cbFiltroEmpresa != null) cbFiltroEmpresa.addItem(empresa);
        }
    }

    public void preencherTabela(List<Agente> agentes) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Agente agente : agentes) {
            Object[] row = {
                agente.getId(),
                agente.getNome(),
                agente.getCpf(),
                agente.getCargo(),
                agente.getDataNascimento() != null ? agente.getDataNascimento().format(formatter) : "",
                agente.getTelefone(),
                agente.getEmail(),
                agente.isDisponivel() ? "Sim" : "Não",
                agente.getEmpresaParceira() != null ? agente.getEmpresaParceira().getNome() : ""
            };
            tableModel.addRow(row);
        }
    }

    public Agente getAgenteFromForm() throws ParseException {
        Agente agente = new Agente();
        agente.setId(agenteIdEdicao); // Define o ID se estiver editando
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String cargo = txtCargo.getText();
        String dataStr = txtDataNascimento.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();
        boolean disponivel = cbDisponivel.getSelectedItem().equals("Sim");
        EmpresaParceira empresa = (EmpresaParceira) cbEmpresaParceira.getSelectedItem();

        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome é obrigatório");
        String cpfDigits = cpf.replaceAll("\\D", "");
        if (cpfDigits.length() != 11) throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        if (cargo == null || cargo.trim().isEmpty()) throw new IllegalArgumentException("Cargo é obrigatório");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataNascimento = LocalDate.parse(dataStr, formatter);
        if (telefone == null || telefone.replaceAll("\\D"," ").trim().isEmpty()) throw new IllegalArgumentException("Telefone é obrigatório");
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email é obrigatório");
        Pattern emailPattern = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
        if (!emailPattern.matcher(email).matches()) throw new IllegalArgumentException("Email inválido");
        if (empresa == null) throw new IllegalArgumentException("Empresa parceira é obrigatória");

        agente.setNome(nome.trim());
        agente.setCpf(cpf);
        agente.setCargo(cargo.trim());
        agente.setDataNascimento(dataNascimento);
        agente.setTelefone(telefone);
        agente.setEmail(email.trim());
        agente.setDisponivel(disponivel);
        agente.setEmpresaParceira(empresa);
        return agente;
    }

    public void limparFormulario() {
        agenteIdEdicao = null; // Limpa o ID de edição
        txtNome.setText("");
        txtCpf.setValue(null);
        txtCargo.setText("");
        txtDataNascimento.setValue(null);
        txtTelefone.setValue(null);
        txtEmail.setText("");
        cbDisponivel.setSelectedIndex(0);
        if (cbEmpresaParceira.getItemCount() > 0) {
            cbEmpresaParceira.setSelectedIndex(0);
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

    public Long getSelectedAgenteId() {
        int selectedRow = tblAgentes.getSelectedRow();
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
    
    public void preencherFormulario(Agente agente) {
        if (agente != null) {
            agenteIdEdicao = agente.getId(); // Armazena o ID para edição
            txtNome.setText(agente.getNome());
            txtCpf.setText(agente.getCpf());
            txtCargo.setText(agente.getCargo());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            txtDataNascimento.setText(agente.getDataNascimento() != null ? agente.getDataNascimento().format(formatter) : "");
            
            txtTelefone.setText(agente.getTelefone());
            txtEmail.setText(agente.getEmail());
            cbDisponivel.setSelectedItem(agente.isDisponivel() ? "Sim" : "Não");
            
            for (int i = 0; i < cbEmpresaParceira.getItemCount(); i++) {
                EmpresaParceira empresa = cbEmpresaParceira.getItemAt(i);
                if (agente.getEmpresaParceira() != null && empresa.getId().equals(agente.getEmpresaParceira().getId())) {
                    cbEmpresaParceira.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    // Filtros getters
    public String getFiltroNome() { return txtFiltroNome != null ? txtFiltroNome.getText() : ""; }
    public String getFiltroCargo() { return txtFiltroCargo != null ? txtFiltroCargo.getText() : ""; }
    public EmpresaParceira getFiltroEmpresa() { return cbFiltroEmpresa != null ? (EmpresaParceira) cbFiltroEmpresa.getSelectedItem() : null; }
    public String getFiltroDisponivel() { return cbFiltroDisponivel != null ? (String) cbFiltroDisponivel.getSelectedItem() : "Todos"; }
}