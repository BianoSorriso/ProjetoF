package com.scmflusao.tools;

import com.scmflusao.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FixQuantidadeProdutoItem {
    // Utilitário robusto: usa a mesma conexão do app e detecta coluna de quantidade

    private static String resolveQuantidadeColumn(Connection conn) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            String tabela = "produto_item";
            String colunaEncontrada = null;
            try (ResultSet rs = meta.getColumns(null, null, tabela, null)) {
                while (rs.next()) {
                    String col = rs.getString("COLUMN_NAME");
                    if (col == null) continue;
                    String lc = col.toLowerCase();
                    if (lc.equals("quantidade_necessaria")) { colunaEncontrada = col; break; }
                    if (lc.equals("quantidade")) { colunaEncontrada = col; }
                }
            }
            return colunaEncontrada != null ? colunaEncontrada : "quantidade";
        } catch (SQLException e) {
            return "quantidade";
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Uso: FixQuantidadeProdutoItem <produto_id> <item_id> <quantidade>");
            System.exit(1);
        }

        long produtoId = Long.parseLong(args[0]);
        long itemId = Long.parseLong(args[1]);
        int quantidade = Integer.parseInt(args[2]);

        try (Connection conn = DatabaseConfig.getConnection()) {
            String quantidadeCol = resolveQuantidadeColumn(conn);
            // Tenta update; se não existir a linha, faz upsert via MERGE
            int updated;
            String updateSql = "UPDATE produto_item SET " + quantidadeCol + " = ? WHERE produto_id = ? AND item_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, quantidade);
                stmt.setLong(2, produtoId);
                stmt.setLong(3, itemId);
                updated = stmt.executeUpdate();
            }

            if (updated == 0) {
                String insertCols = "produto_id, item_id, " + quantidadeCol;
                String mergeSql = "MERGE INTO produto_item (" + insertCols + ") KEY (produto_id, item_id) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(mergeSql)) {
                    stmt.setLong(1, produtoId);
                    stmt.setLong(2, itemId);
                    stmt.setInt(3, quantidade);
                    stmt.executeUpdate();
                }
            }

            System.out.println("OK: produto_id=" + produtoId + ", item_id=" + itemId + ", quantidade=" + quantidade + " (coluna: " + quantidadeCol + ")");
        } catch (SQLException e) {
            System.err.println("Falha ao ajustar quantidade: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
}