package com.scmflusao.view;

import com.scmflusao.model.Transporte;
import com.scmflusao.model.SituacaoTransporte;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatorioTransporteView extends JFrame {
    private final JTextField txtInicio = new JTextField();
    private final JTextField txtFim = new JTextField();
    private final JComboBox<SituacaoTransporte> cbSituacao = new JComboBox<>(SituacaoTransporte.values());
    private final JButton btnGerar = new JButton("Gerar");
    private final JButton btnExportar = new JButton("Exportar CSV");
    private final JButton btnExportarTxt = new JButton("Exportar TXT");

    private final DefaultTableModel model;
    private final JTable tabela;

    public RelatorioTransporteView() {
        setTitle("Relatório de Transportes");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel filtros = new JPanel(new GridLayout(2, 4, 8, 8));
        filtros.add(new JLabel("Data Início (dd/MM/yyyy HH:mm)"));
        filtros.add(txtInicio);
        filtros.add(new JLabel("Data Fim (dd/MM/yyyy HH:mm)"));
        filtros.add(txtFim);
        filtros.add(new JLabel("Situação"));
        filtros.add(cbSituacao);
        filtros.add(new JLabel());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnGerar);
        botoes.add(btnExportar);
        botoes.add(btnExportarTxt);

        String[] cols = {"ID", "Empresa", "Agente", "Origem", "Destino", "Situação", "Data Início", "Data Fim"};
        model = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r,int c){return false;} };
        tabela = new JTable(model);
        tabela.setFillsViewportHeight(true);

        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new BorderLayout());
        top.add(filtros, BorderLayout.CENTER);
        top.add(botoes, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    public void setGerarListener(java.awt.event.ActionListener l) { btnGerar.addActionListener(l); }
    public void setExportarListener(java.awt.event.ActionListener l) { btnExportar.addActionListener(l); }
    public void setExportarTxtListener(java.awt.event.ActionListener l) { btnExportarTxt.addActionListener(l); }

    public SituacaoTransporte getSituacaoSelecionada() { return (SituacaoTransporte) cbSituacao.getSelectedItem(); }
    public LocalDateTime getInicio() {
        try {
            String s = txtInicio.getText().trim();
            if (s.isEmpty()) return null;
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) { return null; }
    }
    public LocalDateTime getFim() {
        try {
            String s = txtFim.getText().trim();
            if (s.isEmpty()) return null;
            return LocalDateTime.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) { return null; }
    }

    public void preencherTabela(List<Transporte> lista) {
        model.setRowCount(0);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (Transporte t : lista) {
            model.addRow(new Object[]{
                t.getId(),
                t.getEmpresaParceira()!=null? t.getEmpresaParceira().getNome():"",
                t.getAgente()!=null? t.getAgente().getNome():"",
                t.getOrigemCidade()!=null? t.getOrigemCidade().getNome():"",
                t.getDestinoCidade()!=null? t.getDestinoCidade().getNome():"",
                t.getSituacao()!=null? t.getSituacao().name():"",
                t.getDataInicio()!=null? t.getDataInicio().format(f):"",
                t.getDataFim()!=null? t.getDataFim().format(f):""
            });
        }
    }

    public void exibirMensagem(String m) { JOptionPane.showMessageDialog(this, m); }
}