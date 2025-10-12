package com.scmflusao.view;

import com.scmflusao.model.Armazem;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RelatorioEstoqueView extends JFrame {
    private JTable tabela;
    private DefaultTableModel model;

    public RelatorioEstoqueView() {
        setTitle("Relatório de Estoque por Armazém");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        model = new DefaultTableModel(new Object[]{"Armazém", "Capacidade", "Ocupação", "% Ocupação", "Ativo"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(model);
        setLayout(new BorderLayout());
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    public void preencherArmazens(List<Armazem> armazens) {
        model.setRowCount(0);
        for (Armazem a : armazens) {
            BigDecimal capacidade = a.getCapacidade();
            BigDecimal ocupacao = a.getOcupacao();
            double percentual = BigDecimal.ZERO.compareTo(capacidade) < 0
                ? ocupacao.divide(capacidade, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue()
                : 0.0;
            model.addRow(new Object[]{
                a.getNome(),
                capacidade,
                ocupacao,
                String.format("%.1f%%", percentual),
                a.isAtivo() ? "Sim" : "Não"
            });
        }
    }

    public void exibirMensagem(String msg) { JOptionPane.showMessageDialog(this, msg); }
}