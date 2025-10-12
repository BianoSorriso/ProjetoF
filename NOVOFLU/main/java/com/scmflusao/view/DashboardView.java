package com.scmflusao.view;

import com.scmflusao.auth.SessionManager;
import com.scmflusao.controller.AgenteController;
import com.scmflusao.controller.EmpresaParceiraController;
import com.scmflusao.controller.TransporteController;
import com.scmflusao.model.SituacaoTransporte;
import com.scmflusao.events.DashboardEventBus;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JPanel {
    private final Color VERDE = new Color(0, 102, 0);
    private final Color GRENA = new Color(128, 0, 0);
    private final Color BRANCO = Color.WHITE;
    // Labels que exibem contagens
    private JLabel lblAgentes;
    private JLabel lblEmpresas;
    private JLabel lblTransportes;
    private JLabel lblPlanejados;
    private JLabel lblEmTransporte;
    private JLabel lblEntregues;
    public DashboardView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        var u = SessionManager.getInstance().getUsuarioAtual();
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(GRENA);
        banner.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("<html>Seja bem-vindo ao sistema <span style='color: rgb(0,102,0)'>SCM</span> <span style='color: rgb(128,0,0)'>Flusão</span></html>", JLabel.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JLabel subtitle = new JLabel("Usuário: " + (u != null ? u.getNome() : ""));
        subtitle.setForeground(BRANCO);
        banner.add(title, BorderLayout.WEST);
        banner.add(subtitle, BorderLayout.EAST);
        JPanel faixaVerde = new JPanel();
        faixaVerde.setBackground(VERDE);
        faixaVerde.setPreferredSize(new Dimension(10, 6));
        banner.add(faixaVerde, BorderLayout.SOUTH);
        add(banner, BorderLayout.NORTH);

        // Cards com informações (totais dentro das caixas e textos mais acima)
        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 12));

        // Card Agentes
        JPanel cardAgentes = new JPanel(new BorderLayout());
        cardAgentes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel tAg = new JLabel("Agentes");
        tAg.setFont(new Font("SansSerif", Font.BOLD, 16));
        cardAgentes.add(tAg, BorderLayout.NORTH);
        JPanel cAg = new JPanel();
        cAg.setLayout(new BoxLayout(cAg, BoxLayout.Y_AXIS));
        JLabel sAg = new JLabel("<html><i>Gerencie equipes e disponibilidade</i></html>");
        sAg.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAgentes = new JLabel();
        lblAgentes.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblAgentes.setAlignmentX(Component.LEFT_ALIGNMENT);
        cAg.add(sAg);
        cAg.add(Box.createVerticalStrut(8));
        cAg.add(lblAgentes);
        cardAgentes.add(cAg, BorderLayout.CENTER);

        // Card Empresas
        JPanel cardEmpresas = new JPanel(new BorderLayout());
        cardEmpresas.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel tEm = new JLabel("Empresas");
        tEm.setFont(new Font("SansSerif", Font.BOLD, 16));
        cardEmpresas.add(tEm, BorderLayout.NORTH);
        JPanel cEm = new JPanel();
        cEm.setLayout(new BoxLayout(cEm, BoxLayout.Y_AXIS));
        JLabel sEm = new JLabel("<html><i>Cadastro e consulta de parceiras</i></html>");
        sEm.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEmpresas = new JLabel();
        lblEmpresas.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblEmpresas.setAlignmentX(Component.LEFT_ALIGNMENT);
        cEm.add(sEm);
        cEm.add(Box.createVerticalStrut(8));
        cEm.add(lblEmpresas);
        cardEmpresas.add(cEm, BorderLayout.CENTER);

        // Card Transportes
        JPanel cardTransp = new JPanel(new BorderLayout());
        cardTransp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel tTr = new JLabel("Transportes");
        tTr.setFont(new Font("SansSerif", Font.BOLD, 16));
        cardTransp.add(tTr, BorderLayout.NORTH);
        JPanel cTr = new JPanel();
        cTr.setLayout(new BoxLayout(cTr, BoxLayout.Y_AXIS));
        JLabel sTr = new JLabel("<html><i>Fluxo logístico e status</i></html>");
        sTr.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTransportes = new JLabel();
        lblTransportes.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTransportes.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPlanejados = new JLabel();
        lblEmTransporte = new JLabel();
        lblEntregues = new JLabel();
        lblPlanejados.setForeground(new Color(60, 60, 60));
        lblEmTransporte.setForeground(new Color(60, 60, 60));
        lblEntregues.setForeground(new Color(60, 60, 60));
        lblPlanejados.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEmTransporte.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEntregues.setAlignmentX(Component.LEFT_ALIGNMENT);
        cTr.add(sTr);
        cTr.add(Box.createVerticalStrut(8));
        cTr.add(lblTransportes);
        cTr.add(Box.createVerticalStrut(4));
        cTr.add(lblPlanejados);
        cTr.add(lblEmTransporte);
        cTr.add(lblEntregues);
        cardTransp.add(cTr, BorderLayout.CENTER);

        cards.add(cardAgentes);
        cards.add(cardEmpresas);
        cards.add(cardTransp);
        add(cards, BorderLayout.CENTER);

        // Carregar valores inicialmente
        atualizarDados();

        // Registrar atualização automática quando houver mudanças em transportes
        DashboardEventBus.getInstance().addTransporteChangeListener(this::atualizarDados);
    }

    /** Recalcula e atualiza as contagens exibidas nos cards. */
    private void atualizarDados() {
        try {
            AgenteController agenteController = new AgenteController();
            EmpresaParceiraController empresaController = new EmpresaParceiraController();
            TransporteController transporteController = new TransporteController();

            int qtdAgentes = agenteController.listarTodosAgentes().size();
            int qtdEmpresas = empresaController.listarTodasEmpresasParceiras().size();
            var transportes = transporteController.listarTodos();
            int qtdTransportes = transportes.size();
            long planejados = transportes.stream().filter(t -> t.getSituacao() == SituacaoTransporte.PLANEJADO).count();
            long emTransporte = transportes.stream().filter(t -> t.getSituacao() == SituacaoTransporte.EM_TRANSITO).count();
            long entregues = transportes.stream().filter(t -> t.getSituacao() == SituacaoTransporte.ENTREGUE).count();

            lblAgentes.setText("Total de Agentes: " + qtdAgentes);
            lblEmpresas.setText("Total de Empresas: " + qtdEmpresas);
            lblTransportes.setText("Total de Transportes: " + qtdTransportes);
            lblPlanejados.setText("Planejados: " + planejados);
            lblEmTransporte.setText("Em Trânsito: " + emTransporte);
            lblEntregues.setText("Entregues: " + entregues);
        } catch (Exception ex) {
            // Evitar quebra do dashboard se houver falha
        }
    }

    private JPanel makeCard(String title, String subtitle) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel s = new JLabel("<html><i>" + subtitle + "</i></html>");
        p.add(t, BorderLayout.NORTH);
        p.add(s, BorderLayout.CENTER);
        return p;
    }
}