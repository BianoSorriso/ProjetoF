package com.scmflusao.view;

import com.scmflusao.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public class TransporteView extends JFrame {
    private JTextField txtId;
    private JComboBox<EmpresaParceira> cbEmpresa;
    private JComboBox<Agente> cbAgente;
    private JComboBox<Cidade> cbOrigemCidade;
    private JComboBox<Cidade> cbDestinoCidade;
    private JComboBox<Armazem> cbOrigemArmazem;
    private JComboBox<Armazem> cbDestinoArmazem;
    private JComboBox<Produto> cbProduto;
    private JComboBox<Item> cbItem;
    private JComboBox<SituacaoTransporte> cbSituacao;
    private JFormattedTextField txtDataInicio;
    private JFormattedTextField txtDataFim;

    private JButton btnSalvar;
    private JButton btnConsultar;
    private JButton btnAlterar;
    private JButton btnExcluir;
    private JButton btnLimpar;
    private JButton btnAtualizarItens;

    private JTable tabela;
    private DefaultTableModel tabelaModel;

    private Long transporteIdEdicao;

    public TransporteView() {
        setTitle("Gestão de Transportes");
        setSize(1000, 700);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        // Aplicar fundo neutro no formulário desta tela
        form.setOpaque(true);
        form.setBackground(Color.WHITE);

        txtId = new JTextField(); txtId.setEditable(false);
        cbEmpresa = new JComboBox<>();
        cbAgente = new JComboBox<>();
        cbOrigemCidade = new JComboBox<>();
        cbDestinoCidade = new JComboBox<>();
        cbOrigemArmazem = new JComboBox<>();
        cbDestinoArmazem = new JComboBox<>();
        cbProduto = new JComboBox<>();
        cbItem = new JComboBox<>();
        cbSituacao = new JComboBox<>(SituacaoTransporte.values());
        // Neutraliza cores para esta tela (sem tema vermelho)
        neutralizeComponent(cbEmpresa);
        neutralizeComponent(cbAgente);
        neutralizeComponent(cbOrigemCidade);
        neutralizeComponent(cbDestinoCidade);
        neutralizeComponent(cbOrigemArmazem);
        neutralizeComponent(cbDestinoArmazem);
        neutralizeComponent(cbProduto);
        neutralizeComponent(cbItem);
        neutralizeComponent(cbSituacao);
        try {
            MaskFormatter mask = new MaskFormatter("##/##/#### ##:##");
            mask.setPlaceholderCharacter('_');
            txtDataInicio = new JFormattedTextField(mask);
        } catch (ParseException e) {
            txtDataInicio = new JFormattedTextField();
        }
        neutralizeComponent(txtDataInicio);
        try {
            MaskFormatter mask = new MaskFormatter("##/##/#### ##:##");
            mask.setPlaceholderCharacter('_');
            txtDataFim = new JFormattedTextField(mask);
        } catch (ParseException e) {
            txtDataFim = new JFormattedTextField();
        }
        neutralizeComponent(txtDataFim);
        txtDataInicio.setToolTipText("Formato: dd/MM/yyyy HH:mm");
        txtDataFim.setToolTipText("Formato: dd/MM/yyyy HH:mm");

        // Renderizadores para exibir nomes amigáveis nos combos
        renderCombo(cbEmpresa, e -> e != null ? e.getNome() : "");
        renderCombo(cbAgente, a -> a != null ? a.getNome() : "");
        renderCombo(cbOrigemCidade, c -> c != null ? (c.getNome() + " - " + c.getEstado()) : "");
        renderCombo(cbDestinoCidade, c -> c != null ? (c.getNome() + " - " + c.getEstado()) : "");
        renderCombo(cbOrigemArmazem, a -> a != null ? a.getNome() : "");
        renderCombo(cbDestinoArmazem, a -> a != null ? a.getNome() : "");
        renderCombo(cbProduto, p -> p != null ? (p.getNome() + " (" + p.getSituacao() + ")") : "");
        // Exibir também o ID para diferenciar entradas duplicadas
        renderCombo(cbItem, i -> i != null ? ("[ID " + i.getId() + "] " + i.getNome() + " (" + i.getSituacao() + ") - Qtd: " + i.getQuantidade()) : "");
        renderCombo(cbSituacao, s -> s != null ? s.name() : "");

        form.add(new JLabel("ID")); form.add(txtId);
        form.add(new JLabel("Empresa Parceira")); form.add(cbEmpresa);
        form.add(new JLabel("Agente")); form.add(cbAgente);
        form.add(new JLabel("Origem (Cidade)")); form.add(cbOrigemCidade);
        form.add(new JLabel("Destino (Cidade)")); form.add(cbDestinoCidade);
        form.add(new JLabel("Origem (Armazém)")); form.add(cbOrigemArmazem);
        form.add(new JLabel("Destino (Armazém)")); form.add(cbDestinoArmazem);
        form.add(new JLabel("Produto")); form.add(cbProduto);
        form.add(new JLabel("Item"));
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        itemPanel.add(cbItem);
        btnAtualizarItens = new JButton("Atualizar Itens");
        neutralizeComponent(btnAtualizarItens);
        itemPanel.add(btnAtualizarItens);
        form.add(itemPanel);
        form.add(new JLabel("Situação")); form.add(cbSituacao);
        form.add(new JLabel("Data Início (dd/MM/yyyy HH:mm)")); form.add(txtDataInicio);
        form.add(new JLabel("Data Fim (dd/MM/yyyy HH:mm)")); form.add(txtDataFim);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnSalvar = new JButton("Salvar");
        btnConsultar = new JButton("Consultar");
        btnAlterar = new JButton("Alterar");
        btnExcluir = new JButton("Excluir");
        btnLimpar = new JButton("Limpar");
        // Botões com estilo neutro nesta tela
        neutralizeComponent(btnSalvar);
        neutralizeComponent(btnConsultar);
        neutralizeComponent(btnAlterar);
        neutralizeComponent(btnExcluir);
        neutralizeComponent(btnLimpar);
        botoes.add(btnSalvar);
        botoes.add(btnConsultar);
        botoes.add(btnAlterar);
        botoes.add(btnExcluir);
        botoes.add(btnLimpar);

        tabelaModel = new DefaultTableModel(new Object[]{"ID", "Empresa", "Agente", "Origem", "Destino", "Situação", "Data Início", "Data Fim"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(tabelaModel);
        tabela.setFillsViewportHeight(true);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // garante exibição de todas as colunas
        // Larguras para cada coluna
        int[] widths = {50, 170, 150, 130, 130, 110, 150, 150};
        for (int i = 0; i < widths.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        setLayout(new BorderLayout(8, 8));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(form), scroll);
        split.setResizeWeight(0.6);
        split.setContinuousLayout(true);
        add(botoes, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }

    // Permissões de botões (para controle por perfil)
    public void setSalvarEnabled(boolean enabled) { if (btnSalvar != null) btnSalvar.setEnabled(enabled); }
    public void setConsultarEnabled(boolean enabled) { if (btnConsultar != null) btnConsultar.setEnabled(enabled); }
    public void setAlterarEnabled(boolean enabled) { if (btnAlterar != null) btnAlterar.setEnabled(enabled); }
    public void setExcluirEnabled(boolean enabled) { if (btnExcluir != null) btnExcluir.setEnabled(enabled); }

    private <T> void renderCombo(JComboBox<T> combo, Function<T, String> labeler) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String text = "";
                if (value != null) {
                    try { text = labeler.apply((T) value); }
                    catch (ClassCastException ex) { text = value.toString(); }
                }
                setText(text);
                return c;
            }
        });
    }

    // Estilo neutro para componentes desta tela (evita vermelho do tema global)
    private void neutralizeComponent(JComponent c) {
        c.setBackground(Color.WHITE);
        c.setForeground(Color.DARK_GRAY);
        c.setOpaque(true);
        if (c instanceof javax.swing.AbstractButton) {
            ((javax.swing.AbstractButton) c).setContentAreaFilled(true);
        }
    }

    public void preencherEmpresas(List<EmpresaParceira> empresas) {
        cbEmpresa.removeAllItems();
        for (EmpresaParceira e : empresas) cbEmpresa.addItem(e);
    }
    public void preencherAgentes(List<Agente> agentes) {
        cbAgente.removeAllItems();
        for (Agente a : agentes) cbAgente.addItem(a);
    }
    public void preencherCidadesOrigem(List<Cidade> cidades) {
        cbOrigemCidade.removeAllItems();
        for (Cidade c : cidades) cbOrigemCidade.addItem(c);
    }
    public void preencherCidadesDestino(List<Cidade> cidades) {
        cbDestinoCidade.removeAllItems();
        for (Cidade c : cidades) cbDestinoCidade.addItem(c);
    }
    public void preencherArmazensOrigem(List<Armazem> armazens) {
        cbOrigemArmazem.removeAllItems();
        for (Armazem a : armazens) cbOrigemArmazem.addItem(a);
    }
    public void preencherArmazensDestino(List<Armazem> armazens) {
        cbDestinoArmazem.removeAllItems();
        for (Armazem a : armazens) cbDestinoArmazem.addItem(a);
    }
    public void preencherProdutos(List<Produto> produtos) {
        cbProduto.removeAllItems();
        for (Produto p : produtos) cbProduto.addItem(p);
    }
    public void preencherItens(List<Item> itens) {
        cbItem.removeAllItems();
        for (Item i : itens) cbItem.addItem(i);
        // Evita seleção automática do primeiro item ao repopular
        cbItem.setSelectedIndex(-1);
    }

    public void preencherTabela(List<Transporte> transportes) {
        tabelaModel.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Transporte t : transportes) {
            tabelaModel.addRow(new Object[]{
                t.getId(),
                t.getEmpresaParceira() != null ? t.getEmpresaParceira().getNome() : "",
                t.getAgente() != null ? t.getAgente().getNome() : "",
                t.getOrigemCidade() != null ? t.getOrigemCidade().getNome() : "",
                t.getDestinoCidade() != null ? t.getDestinoCidade().getNome() : "",
                t.getSituacao() != null ? t.getSituacao().name() : "",
                t.getDataInicio() != null ? t.getDataInicio().format(f) : "",
                t.getDataFim() != null ? t.getDataFim().format(f) : ""
            });
        }
    }

    public Long getSelectedTransporteId() {
        int row = tabela.getSelectedRow();
        if (row >= 0) {
            Object val = tabelaModel.getValueAt(row, 0);
            if (val instanceof Long) return (Long) val;
            try { return Long.parseLong(val.toString()); } catch (Exception ignored) {}
        }
        return null;
    }

    public Transporte getTransporteFromForm() {
        Transporte t = new Transporte();
        t.setId(transporteIdEdicao);
        t.setEmpresaParceira((EmpresaParceira) cbEmpresa.getSelectedItem());
        t.setAgente((Agente) cbAgente.getSelectedItem());
        t.setOrigemCidade((Cidade) cbOrigemCidade.getSelectedItem());
        t.setDestinoCidade((Cidade) cbDestinoCidade.getSelectedItem());
        t.setOrigemArmazem((Armazem) cbOrigemArmazem.getSelectedItem());
        t.setDestinoArmazem((Armazem) cbDestinoArmazem.getSelectedItem());
        t.setProduto((Produto) cbProduto.getSelectedItem());
        t.setItem((Item) cbItem.getSelectedItem());
        t.setSituacao((SituacaoTransporte) cbSituacao.getSelectedItem());
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try { if (!txtDataInicio.getText().isBlank()) t.setDataInicio(LocalDateTime.parse(txtDataInicio.getText(), f)); } catch (Exception ignored) {}
        try { if (!txtDataFim.getText().isBlank()) t.setDataFim(LocalDateTime.parse(txtDataFim.getText(), f)); } catch (Exception ignored) {}
        return t;
    }

    public void preencherFormulario(Transporte t) {
        transporteIdEdicao = t.getId();
        txtId.setText(t.getId() != null ? t.getId().toString() : "");
        selecionarCombo(cbEmpresa, t.getEmpresaParceira());
        selecionarCombo(cbAgente, t.getAgente());
        selecionarCombo(cbOrigemCidade, t.getOrigemCidade());
        selecionarCombo(cbDestinoCidade, t.getDestinoCidade());
        selecionarCombo(cbOrigemArmazem, t.getOrigemArmazem());
        selecionarCombo(cbDestinoArmazem, t.getDestinoArmazem());
        selecionarCombo(cbProduto, t.getProduto());
        selecionarCombo(cbItem, t.getItem());
        cbSituacao.setSelectedItem(t.getSituacao());
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txtDataInicio.setText(t.getDataInicio() != null ? t.getDataInicio().format(f) : "");
        txtDataFim.setText(t.getDataFim() != null ? t.getDataFim().format(f) : "");
    }

    private <T> void selecionarCombo(JComboBox<T> combo, T item) {
        if (item == null) return;
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).equals(item)) { combo.setSelectedIndex(i); break; }
        }
    }

    public void limparFormulario() {
        transporteIdEdicao = null;
        txtId.setText("");
        cbEmpresa.setSelectedIndex(-1);
        cbAgente.setSelectedIndex(-1);
        cbOrigemCidade.setSelectedIndex(-1);
        cbDestinoCidade.setSelectedIndex(-1);
        cbOrigemArmazem.setSelectedIndex(-1);
        cbDestinoArmazem.setSelectedIndex(-1);
        cbProduto.setSelectedIndex(-1);
        cbItem.setSelectedIndex(-1);
        cbSituacao.setSelectedItem(SituacaoTransporte.PLANEJADO);
        txtDataInicio.setText("");
        txtDataFim.setText("");
    }

    public void exibirMensagem(String mensagem) { JOptionPane.showMessageDialog(this, mensagem); }

    public void setSalvarListener(java.awt.event.ActionListener l) { btnSalvar.addActionListener(l); }
    public void setConsultarListener(java.awt.event.ActionListener l) { btnConsultar.addActionListener(l); }
    public void setAlterarListener(java.awt.event.ActionListener l) { btnAlterar.addActionListener(l); }
    public void setExcluirListener(java.awt.event.ActionListener l) { btnExcluir.addActionListener(l); }
    public void setLimparListener(java.awt.event.ActionListener l) { btnLimpar.addActionListener(l); }
    // Listener para mudança de produto, usado para filtrar itens do combo
    public void setProdutoChangeListener(java.awt.event.ActionListener l) { cbProduto.addActionListener(l); }
    // Listener explícito para atualizar itens do produto atual
    public void setAtualizarItensListener(java.awt.event.ActionListener l) { btnAtualizarItens.addActionListener(l); }

    // Utilitário: retorna uma cópia da lista de itens atualmente no combo
    public java.util.List<Item> getItensCombo() {
        java.util.List<Item> itens = new java.util.ArrayList<>();
        for (int i = 0; i < cbItem.getItemCount(); i++) {
            itens.add(cbItem.getItemAt(i));
        }
        return itens;
    }

}