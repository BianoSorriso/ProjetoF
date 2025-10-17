package com.scmflusao.view;

import com.scmflusao.auth.SessionManager;
import com.scmflusao.model.Perfil;
import com.scmflusao.controller.*;

import javax.swing.*;
import java.awt.*;

public class AppShell extends JFrame {
    private JPanel sidebar;
    private JPanel content;
    private JLabel userLabel;
    private final Color VERDE = new Color(0, 102, 0);
    private final Color GRENA = new Color(128, 0, 0);
    private final Color BRANCO = Color.WHITE;

    public AppShell() {
        setTitle("SCM Flusão - Console");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top bar com fundo grená, título em verde/grená
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(GRENA);
        top.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel logo = new JLabel("<html><span style='color: rgb(0,102,0)'>SCM</span> <span style='color: rgb(128,0,0)'>Flusão</span> - Sistema de Controle e Monitoramento</html>", JLabel.LEFT);
        logo.setOpaque(false);
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel subtitle = new JLabel("Democratizando a gestão logística com tecnologia e inovação");
        subtitle.setForeground(BRANCO);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        // "Pílula" branca justa ao texto do título
        JPanel titlePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePill.setOpaque(true);
        titlePill.setBackground(Color.WHITE);
        titlePill.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        titlePill.add(logo);
        titlePill.setMaximumSize(titlePill.getPreferredSize());

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titlePill.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBox.add(titlePill);
        titleBox.add(subtitle);
        userLabel = new JLabel();
        userLabel.setForeground(BRANCO);
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            SessionManager.getInstance().logout();
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginView lv = new LoginView();
                new LoginViewListener(lv);
                lv.setVisible(true);
            });
        });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(userLabel);
        right.add(btnLogout);
        top.add(titleBox, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Sidebar
        sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(0, 1, 8, 8));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        sidebar.setBackground(VERDE);
        add(sidebar, BorderLayout.WEST);

        // Content area
        content = new JPanel(new BorderLayout());
        add(content, BorderLayout.CENTER);

        // Populate
        atualizarUsuario();
        montarMenu();
        abrirDashboard();
    }

    private void atualizarUsuario() {
        var u = SessionManager.getInstance().getUsuarioAtual();
        userLabel.setText(u != null ? (u.getNome() + " - " + u.getPerfil()) : "");
    }

    private void montarMenu() {
        sidebar.removeAll();
        JButton btnAgentes = new JButton("Agentes");
        JButton btnEmpresas = new JButton("Empresas Parceiras");
        JButton btnArmazens = new JButton("Armazéns");
        JButton btnProdutos = new JButton("Produtos");
        JButton btnItens = new JButton("Itens");
        JButton btnCidades = new JButton("Cidades");
        JButton btnTransportes = new JButton("Transportes");
        JButton btnRelatorios = new JButton("Relatórios");

        for (JButton b : new JButton[]{btnAgentes, btnEmpresas, btnArmazens, btnProdutos, btnItens, btnCidades, btnTransportes, btnRelatorios}) {
            b.setBackground(BRANCO);
            b.setForeground(Color.DARK_GRAY);
        }

        btnAgentes.addActionListener(e -> abrirAgentes());
        btnEmpresas.addActionListener(e -> abrirEmpresas());
        btnArmazens.addActionListener(e -> abrirArmazens());
        btnProdutos.addActionListener(e -> abrirProdutos());
        btnItens.addActionListener(e -> abrirItens());
        btnCidades.addActionListener(e -> abrirCidades());
        btnTransportes.addActionListener(e -> abrirTransportes());
        btnRelatorios.addActionListener(e -> abrirRelatorios());

        Perfil p = SessionManager.getInstance().getUsuarioAtual().getPerfil();
        // Controle de acesso básico
        boolean isAdmin = p == Perfil.ADMIN;
        boolean isOperador = p == Perfil.OPERADOR;
        boolean isParceiro = p == Perfil.PARCEIRO;

        sidebar.add(btnAgentes);
        sidebar.add(btnEmpresas);
        sidebar.add(btnArmazens);
        sidebar.add(btnProdutos);
        sidebar.add(btnItens);
        sidebar.add(btnCidades);
        sidebar.add(btnTransportes);
        sidebar.add(btnRelatorios);

        btnAgentes.setEnabled(isAdmin || isOperador);
        btnEmpresas.setEnabled(isAdmin || isOperador);
        btnArmazens.setEnabled(isAdmin || isOperador);
        btnProdutos.setEnabled(isAdmin);
        btnItens.setEnabled(isAdmin);
        btnCidades.setEnabled(isAdmin || isOperador);
        btnTransportes.setEnabled(isAdmin || isOperador || isParceiro);
        btnRelatorios.setEnabled(true);

        sidebar.revalidate();
        sidebar.repaint();
    }

    private void abrirDashboard() {
        content.removeAll();
        content.add(new DashboardView(), BorderLayout.CENTER);
        content.revalidate();
        content.repaint();
    }

    private void abrirAgentes() {
        AgenteView view = new AgenteView();
        new AgenteViewListener(view);
        view.setVisible(true);
    }

    private void abrirEmpresas() {
        EmpresaParceiraView view = new EmpresaParceiraView();
        new EmpresaParceiraViewListener(view);
        view.setVisible(true);
    }

    private void abrirProdutos() {
        ProdutoView view = new ProdutoView();
        new ProdutoViewListener(view);
        view.setVisible(true);
    }

    private void abrirItens() {
        ItemView view = new ItemView();
        new ItemViewListener(view);
        view.setVisible(true);
    }

    private void abrirTransportes() {
        TransporteView view = new TransporteView();
        new TransporteViewListener(view);
        view.setVisible(true);
    }

    private void abrirRelatorios() {
        RelatorioTransporteView view = new RelatorioTransporteView();
        new com.scmflusao.controller.RelatorioTransporteViewListener(view);
        view.setVisible(true);
    }

    private void abrirCidades() {
        CidadeView view = new CidadeView();
        new CidadeViewListener(view);
        view.setVisible(true);
    }

    private void abrirArmazens() {
        ArmazemView view = new ArmazemView();
        new ArmazemViewListener(view);
        view.setVisible(true);
    }
}