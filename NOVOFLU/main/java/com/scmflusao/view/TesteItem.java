package com.scmflusao.view;

import javax.swing.*;

public class TesteItem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Corrigido: usar o método correto do UIManager
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("Look and Feel configurado com sucesso!");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
                e.printStackTrace();
            }
            
            JFrame frame = new JFrame("Teste - UIManager Corrigido");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 200);
            frame.setLocationRelativeTo(null);
            
            JLabel label = new JLabel("UIManager.getSystemLookAndFeel() funcionando!", JLabel.CENTER);
            frame.add(label);
            frame.setVisible(true);
            
            System.out.println("Teste concluído com sucesso!");
        });
    }
}