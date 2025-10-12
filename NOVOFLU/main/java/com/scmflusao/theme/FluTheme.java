package com.scmflusao.theme;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

public final class FluTheme {
    public static final Color VERDE = new Color(0, 102, 0);
    // Tons mais suaves para melhorar contraste e reduzir saturação
    public static final Color VERDE_MEDIO = new Color(60, 140, 100);
    public static final Color VERDE_SUAVE = new Color(240, 245, 240);
    public static final Color GRENA = new Color(128, 0, 0);
    public static final Color BRANCO = Color.WHITE;

    private FluTheme() {}

    public static void setupLookAndFeel() {
        boolean flat = false;
        try {
            Class<?> flatClass = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            Method setup = flatClass.getMethod("setup");
            setup.invoke(null);
            flat = true;
        } catch (Throwable ignore) {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) { /* ignore */ }
            }
        }
        applyPalette(flat);
    }

    public static void applyPalette(boolean flat) {
        UIManager.put("control", BRANCO);
        UIManager.put("text", Color.DARK_GRAY);
        // Fonte padrão levemente maior para melhorar legibilidade
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("defaultFont", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", defaultFont);

        if (flat) {
            UIManager.put("Component.focusColor", GRENA);
            UIManager.put("Component.borderColor", new Color(200, 200, 200));
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumb", VERDE_MEDIO);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("ScrollBar.showButtons", false);
            UIManager.put("ScrollBar.thumbInsets", new Insets(2,2,2,2));
            UIManager.put("TabbedPane.selectedBackground", BRANCO);
            // Botões com maior legibilidade
            UIManager.put("Button.background", VERDE_SUAVE);
            UIManager.put("Button.foreground", new Color(40, 40, 40));
            UIManager.put("Button.hoverBackground", new Color(232, 238, 232));
            UIManager.put("Button.pressedBackground", new Color(220, 230, 220));
            UIManager.put("ComboBox.selectionBackground", GRENA);
            UIManager.put("ComboBox.selectionForeground", BRANCO);
            UIManager.put("Table.selectionBackground", new Color(230, 240, 230));
            UIManager.put("Table.selectionForeground", Color.DARK_GRAY);
            UIManager.put("TableHeader.background", BRANCO);
            UIManager.put("TableHeader.foreground", Color.DARK_GRAY);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
        } else {
            UIManager.put("nimbusBase", GRENA);
            // Em Nimbus, usar cinza-esverdeado suave para evitar cabeçalhos muito fortes
            UIManager.put("nimbusBlueGrey", new Color(176, 180, 176));
            UIManager.put("nimbusLightBackground", BRANCO);
            UIManager.put("nimbusSelectionBackground", GRENA);
            UIManager.put("info", VERDE_MEDIO);
            UIManager.put("nimbusFocus", GRENA);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.selectionBackground", new Color(230, 240, 230));
            UIManager.put("Table.selectionForeground", Color.DARK_GRAY);
            // Botões em Nimbus (tendem a seguir "control"), reforça legibilidade
            UIManager.put("Button.background", VERDE_SUAVE);
            UIManager.put("Button.foreground", new Color(40, 40, 40));
            // Cabeçalho de tabela claro (evita verde intenso)
            UIManager.put("TableHeader.background", BRANCO);
            UIManager.put("TableHeader.foreground", Color.DARK_GRAY);
        }
    }

    public static void styleHeader(JPanel panel, JLabel... labels) {
        panel.setBackground(GRENA);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        for (JLabel l : labels) {
            l.setForeground(BRANCO);
        }
    }

    public static void styleSidebar(JPanel sidebar) {
        sidebar.setBackground(VERDE);
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }

    public static void stylePrimaryButton(JButton b) {
        // Estilo com alto contraste: fundo claro e texto escuro
        b.setBackground(new Color(240, 245, 240));
        b.setForeground(new Color(40, 40, 40));
        b.setBorder(BorderFactory.createLineBorder(VERDE));
    }

    public static void styleSecondaryButton(JButton b) {
        b.setBackground(Color.LIGHT_GRAY);
        b.setForeground(Color.DARK_GRAY);
    }
}