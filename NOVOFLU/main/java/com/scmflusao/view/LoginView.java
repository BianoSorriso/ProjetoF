package com.scmflusao.view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnCancelar;
    private final Color VERDE = new Color(0, 102, 0);
    private final Color GRENA = new Color(128, 0, 0);
    private final Color BRANCO = Color.WHITE;

    public LoginView() {
        setTitle("SCM Flusão - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 380);
        setMinimumSize(new Dimension(520, 360));
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(GRENA); // fundo grená, como solicitado
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("<html><span style='color: rgb(0,102,0)'>SCM</span> <span style='color: rgb(128,0,0)'>Flusão</span></html>", JLabel.LEFT);
        title.setOpaque(false);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel subtitle = new JLabel("Seja bem-vindo! Faça login para continuar.");
        subtitle.setForeground(BRANCO);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        // "Pílula" branca apenas em volta do texto SCM Flusão
        JPanel titlePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePill.setOpaque(true);
        titlePill.setBackground(Color.WHITE);
        titlePill.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        titlePill.add(title);
        titlePill.setMaximumSize(titlePill.getPreferredSize());

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titlePill.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleBox.add(titlePill);
        titleBox.add(subtitle);
        JPanel stripe = new JPanel();
        stripe.setBackground(VERDE); // faixa verde
        stripe.setPreferredSize(new Dimension(10, 6));
        header.add(titleBox, BorderLayout.CENTER);
        header.add(stripe, BorderLayout.SOUTH);
        container.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 1, 8, 8));

        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.add(new JLabel("Email:"), BorderLayout.NORTH);
        txtEmail = new JTextField();
        emailPanel.add(txtEmail, BorderLayout.CENTER);
        form.add(emailPanel);

        JPanel senhaPanel = new JPanel(new BorderLayout());
        senhaPanel.add(new JLabel("Senha:"), BorderLayout.NORTH);
        txtSenha = new JPasswordField();
        senhaPanel.add(txtSenha, BorderLayout.CENTER);
        form.add(senhaPanel);

        // Rodapé com nomes do grupo, ocupando o espaço em branco abaixo dos campos
        JPanel grupoPanel = new JPanel(new BorderLayout());
        JLabel grupoLabel = new JLabel("Grupo: Gustavo \uD83D\uDC51, Giulia Baum, Fabiano Bastos", JLabel.CENTER);
        grupoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        grupoLabel.setForeground(Color.DARK_GRAY);
        grupoPanel.add(grupoLabel, BorderLayout.CENTER);
        form.add(grupoPanel);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEntrar = new JButton("Entrar");
        btnCancelar = new JButton("Cancelar");
        btnEntrar.setBackground(VERDE);
        btnEntrar.setForeground(BRANCO);
        btnCancelar.setBackground(Color.LIGHT_GRAY);
        buttons.add(btnCancelar);
        buttons.add(btnEntrar);

        container.add(form, BorderLayout.CENTER);
        container.add(buttons, BorderLayout.SOUTH);
        setContentPane(container);
    }

    public String getEmail() { return txtEmail.getText(); }
    public String getSenha() { return new String(txtSenha.getPassword()); }
    public void setEntrarListener(java.awt.event.ActionListener l) { btnEntrar.addActionListener(l); }
    public void setCancelarListener(java.awt.event.ActionListener l) { btnCancelar.addActionListener(l); }
    public void exibirMensagem(String m) { JOptionPane.showMessageDialog(this, m); }
}