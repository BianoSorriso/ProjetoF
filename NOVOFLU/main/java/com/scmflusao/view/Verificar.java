package com.scmflusao.view;

import javax.swing.*;
import com.scmflusao.theme.FluTheme;
import java.awt.*;
import java.awt.event.ActionListener;
import com.scmflusao.controller.LoginViewListener;

/**
 * Classe principal que serve como ponto de entrada para a aplicação SCMFlusão.
 * Esta classe apresenta um menu inicial que permite acessar as diferentes
 * funcionalidades do sistema.
 */
public class Verificar extends JFrame {

    /**
     * Construtor da classe Verificar. Configura a interface gráfica.
     */
    public Verificar() {
        setTitle("SCM Flusão - Sistema de Controle e Monitoramento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Centraliza na tela

        // Painel principal com fundo tricolor
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                int w = getWidth();
                int h = getHeight();
                int stripeHeight = h / 3;

                g2.setColor(new Color(0, 102, 0)); // Verde Fluminense
                g2.fillRect(0, 0, w, stripeHeight);

                g2.setColor(Color.WHITE); // Branco
                g2.fillRect(0, stripeHeight, w, stripeHeight);

                g2.setColor(new Color(128, 0, 0)); // Grená
                g2.fillRect(0, stripeHeight * 2, w, stripeHeight);
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Título no topo
        JLabel titleLabel = new JLabel(
            "<html><div style='text-align:center;'>" +
            "<span style='font-size:20px;'>Bem-vindo ao sistema</span><br>" +
            "<span style='color:green; font-weight:bold; font-size:32px;'>SCM</span> " +
            "<span style='color:#800000; font-weight:bold; font-size:32px;'>Flusão</span>" +
            "</div></html>",
            JLabel.CENTER
        );
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Painel central com os botões
        JPanel menuPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        adicionarBotaoMenu(menuPanel, "Gestão de Agentes", e -> {
            AgenteView view = new AgenteView();
            new com.scmflusao.controller.AgenteViewListener(view);
            view.setVisible(true);
        });

        adicionarBotaoMenu(menuPanel, "Gestão de Empresas Parceiras", e -> {
            EmpresaParceiraView view = new EmpresaParceiraView();
            new com.scmflusao.controller.EmpresaParceiraViewListener(view);
            view.setVisible(true);
        });

        adicionarBotaoMenu(menuPanel, "Gestão de Armazéns", e -> {
            ArmazemView view = new ArmazemView();
            new com.scmflusao.controller.ArmazemViewListener(view);
            view.setVisible(true);
        });

        adicionarBotaoMenu(menuPanel, "Gestão de Cidades", e -> {
            CidadeView view = new CidadeView();
            new com.scmflusao.controller.CidadeViewListener(view);
            view.setVisible(true);
        });

        adicionarBotaoMenu(menuPanel, "Gestão de Produtos", e -> {
            ProdutoView view = new ProdutoView();
            new com.scmflusao.controller.ProdutoViewListener(view);
            view.setVisible(true);
        });

        adicionarBotaoMenu(menuPanel, "Gestão de Itens", e -> {
            ItemView view = new ItemView();
            new com.scmflusao.controller.ItemViewListener(view);
            view.setVisible(true);
        });

        adicionarBotaoMenu(menuPanel, "Gestão de Transportes", e -> {
            TransporteView view = new TransporteView();
            new com.scmflusao.controller.TransporteViewListener(view);
            view.setVisible(true);
        });

        adicionarBotaoMenu(menuPanel, "Relatório de Transportes", e -> {
            RelatorioTransporteView view = new RelatorioTransporteView();
            new com.scmflusao.controller.RelatorioTransporteViewListener(view);
            view.setVisible(true);
        });

        adicionarBotaoMenu(menuPanel, "Relatório de Estoque", e -> {
            RelatorioEstoqueView view = new RelatorioEstoqueView();
            new com.scmflusao.controller.RelatorioEstoqueViewListener(view);
            view.setVisible(true);
        });

        mainPanel.add(menuPanel, BorderLayout.CENTER);

        // Rodapé
        JLabel footerLabel = new JLabel("Grupo: Gustavo 👑, Giulia Baum, Fabiano Bastos", JLabel.CENTER);
        footerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Método principal que inicia a aplicação.
     */
    public static void main(String[] args) {
        // Configura o Look & Feel e aplica paleta do Fluminense
        FluTheme.setupLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            LoginView lv = new LoginView();
            new LoginViewListener(lv);
            lv.setVisible(true);
        });
    }

    /**
     * Método utilitário para adicionar botões ao painel.
     */
    private void adicionarBotaoMenu(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.addActionListener(action);
        panel.add(button);
    }
}
