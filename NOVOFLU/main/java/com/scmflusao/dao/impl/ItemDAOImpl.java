package com.scmflusao.dao.impl;

import com.scmflusao.config.DatabaseConfig;
import com.scmflusao.dao.ItemDAO;
import com.scmflusao.model.Item;
import com.scmflusao.model.SituacaoItem;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.Armazem;
import com.scmflusao.model.Pais;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemDAOImpl implements ItemDAO {

    @Override
    public Item save(Item item) {
        String sql = item.getId() == null ?
            "INSERT INTO item (nome, descricao, peso, quantidade, valor_unitario, situacao, cidade_origem_id, armazem_atual_id, data_entrada, data_atualizacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" :
            "UPDATE item SET nome = ?, descricao = ?, peso = ?, quantidade = ?, valor_unitario = ?, situacao = ?, cidade_origem_id = ?, armazem_atual_id = ?, data_atualizacao = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, item.getNome());
            stmt.setString(2, item.getDescricao());
            stmt.setBigDecimal(3, item.getPeso());
            stmt.setInt(4, item.getQuantidade());
            stmt.setBigDecimal(5, item.getValorUnitario());
            stmt.setString(6, item.getSituacao().name());
            stmt.setLong(7, item.getCidadeOrigem().getId());
            
            if (item.getArmazemAtual() != null) {
                stmt.setLong(8, item.getArmazemAtual().getId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }
            
            LocalDateTime agora = LocalDateTime.now();
            
            if (item.getId() == null) {
                stmt.setTimestamp(9, Timestamp.valueOf(agora));
                stmt.setTimestamp(10, Timestamp.valueOf(agora));
            } else {
                stmt.setTimestamp(9, Timestamp.valueOf(agora));
                stmt.setLong(10, item.getId());
            }

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar item, nenhuma linha afetada.");
            }

            if (item.getId() == null) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getLong(1));
                    item.setDataEntrada(agora);
                } else {
                    throw new SQLException("Falha ao salvar item, nenhum ID obtido.");
                }
            }
            
            item.setDataAtualizacao(agora);
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar item: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Item update(Item item) {
        return save(item);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToItem(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item por ID", e);
        }
    }

    @Override
    public List<Item> findAll() {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM item WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar item", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM item WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência do item", e);
        }
    }

    @Override
    public List<Item> findByNome(String nome) {
        return findByStringField("nome", nome, false);
    }

    @Override
    public List<Item> findByNomeContaining(String nome) {
        return findByStringField("nome", nome, true);
    }

    @Override
    public List<Item> findBySituacao(SituacaoItem situacao) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i.situacao = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, situacao.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por situação", e);
        }
    }

    @Override
    public List<Item> findByCidadeOrigemId(Long cidadeId) {
        return findByLongField("cidade_origem_id", cidadeId);
    }

    @Override
    public List<Item> findByProdutoNome(String nomeProduto) {
        List<Item> itens = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            String colunaQuantidade = resolveProdutoItemQuantidadeColumn(conn);
            String sql = "SELECT i.*, pi." + colunaQuantidade + " AS pi_quantidade, " +
                         "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                         "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                         "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                         "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                         "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                         "FROM item i " +
                         "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                         "LEFT JOIN pais po ON co.pais_id = po.id " +
                         "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                         "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                         "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                         "INNER JOIN produto_item pi ON pi.item_id = i.id " +
                         "INNER JOIN produto p ON pi.produto_id = p.id " +
                         "WHERE UPPER(p.nome) LIKE UPPER(?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + (nomeProduto != null ? nomeProduto.trim() : "") + "%");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    itens.add(mapResultSetToItem(rs));
                }
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por nome do produto", e);
        }
    }

    @Override
    public List<Item> findByProdutoId(Long produtoId) {
        List<Item> itens = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            String colunaQuantidade = resolveProdutoItemQuantidadeColumn(conn);
            String sql = "SELECT i.*, pi." + colunaQuantidade + " AS pi_quantidade, " +
                         "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                         "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                         "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                         "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                         "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                         "FROM item i " +
                         "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                         "LEFT JOIN pais po ON co.pais_id = po.id " +
                         "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                         "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                         "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                         "INNER JOIN produto_item pi ON pi.item_id = i.id " +
                         "WHERE pi.produto_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, produtoId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    itens.add(mapResultSetToItem(rs));
                }
            } catch (SQLException primaryEx) {
                // Fallback para dados legados associados por item.produto_id
                String legacySql = "SELECT i.*, " +
                                   "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                                   "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                                   "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                                   "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                                   "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                                   "FROM item i " +
                                   "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                                   "LEFT JOIN pais po ON co.pais_id = po.id " +
                                   "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                                   "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                                   "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                                   "WHERE i.produto_id = ?";
                try (PreparedStatement legacyStmt = conn.prepareStatement(legacySql)) {
                    legacyStmt.setLong(1, produtoId);
                    ResultSet lrs = legacyStmt.executeQuery();
                    while (lrs.next()) {
                        itens.add(mapResultSetToItem(lrs));
                    }
                } catch (SQLException legacyEx) {
                    // Em último caso, não interrompe o fluxo da UI
                    System.err.println("Falha ao buscar itens do produto (join/fallback): " + primaryEx.getMessage() + " | " + legacyEx.getMessage());
                }
            }
            return itens;
        } catch (SQLException e) {
            // Evita quebrar a UI com exceção; retorna lista vazia
            System.err.println("Erro ao obter conexão para buscar itens: " + e.getMessage());
            return itens;
        } finally {
            DatabaseConfig.closeConnection(conn);
        }
    }

    // Resolve dinamicamente a coluna de quantidade da tabela produto_item
    private String resolveProdutoItemQuantidadeColumn(Connection conn) {
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
        } catch (SQLException ex) {
            return "quantidade";
        }
    }

    @Override
    public List<Item> findByArmazemAtualId(Long armazemId) {
        return findByLongField("armazem_atual_id", armazemId);
    }

    @Override
    public List<Item> findByPesoLessThanEqual(BigDecimal peso) {
        return findByBigDecimalComparison("peso", "<=", peso);
    }

    @Override
    public List<Item> findByPesoGreaterThanEqual(BigDecimal peso) {
        return findByBigDecimalComparison("peso", ">=", peso);
    }

    @Override
    public List<Item> findByPesoBetween(BigDecimal pesoMin, BigDecimal pesoMax) {
        return findByBigDecimalBetween("peso", pesoMin, pesoMax);
    }

    @Override
    public List<Item> findByQuantidadeGreaterThan(Integer quantidade) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i.quantidade > ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por quantidade", e);
        }
    }

    @Override
    public List<Item> findByQuantidadeLessThanEqual(Integer quantidade) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i.quantidade <= ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por quantidade menor ou igual", e);
        }
    }

    @Override
    public List<Item> findByQuantidadeGreaterThanEqual(Integer quantidade) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i.quantidade >= ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantidade);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por quantidade maior ou igual", e);
        }
    }

    @Override
    public List<Item> findByValorUnitarioLessThanEqual(BigDecimal valor) {
        return findByBigDecimalComparison("valor_unitario", "<=", valor);
    }

    @Override
    public List<Item> findByValorUnitarioGreaterThanEqual(BigDecimal valor) {
        return findByBigDecimalComparison("valor_unitario", ">=", valor);
    }

    @Override
    public List<Item> findAllOrderByNome() {
        return findAllWithOrder("nome ASC");
    }

    @Override
    public List<Item> findAllOrderByPesoAsc() {
        return findAllWithOrder("peso ASC");
    }

    @Override
    public List<Item> findAllOrderByPesoDesc() {
        return findAllWithOrder("peso DESC");
    }

    @Override
    public List<Item> findAllOrderByQuantidadeAsc() {
        return findAllWithOrder("quantidade ASC");
    }

    @Override
    public List<Item> findAllOrderByQuantidadeDesc() {
        return findAllWithOrder("quantidade DESC");
    }

    @Override
    public List<Item> findAllOrderByValorUnitarioAsc() {
        return findAllWithOrder("valor_unitario ASC");
    }

    @Override
    public List<Item> findAllOrderByValorUnitarioDesc() {
        return findAllWithOrder("valor_unitario DESC");
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM item";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar itens", e);
        }
    }

    @Override
    public long countBySituacao(SituacaoItem situacao) {
        String sql = "SELECT COUNT(*) FROM item WHERE situacao = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, situacao.name());
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar itens por situação", e);
        }
    }

    @Override
    public long sumQuantidade() {
        String sql = "SELECT COALESCE(SUM(quantidade), 0) FROM item";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao somar quantidade de itens", e);
        }
    }

    @Override
    public BigDecimal sumValorTotal() {
        String sql = "SELECT COALESCE(SUM(quantidade * valor_unitario), 0) FROM item";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular valor total do estoque", e);
        }
    }

    // Métodos auxiliares
    private List<Item> findByStringField(String field, String value, boolean isLike) {
        List<Item> itens = new ArrayList<>();
        String operator = isLike ? "LIKE" : "=";
        String paramValue = isLike ? "%" + value + "%" : value;
        
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i." + field + " " + operator + " ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paramValue);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por " + field, e);
        }
    }

    private List<Item> findByLongField(String field, Long value) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i." + field + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, value);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por " + field, e);
        }
    }

    private List<Item> findByBigDecimalComparison(String field, String operator, BigDecimal value) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i." + field + " " + operator + " ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, value);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por " + field, e);
        }
    }

    private List<Item> findByBigDecimalBetween(String field, BigDecimal min, BigDecimal max) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE i." + field + " BETWEEN ? AND ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, min);
            stmt.setBigDecimal(2, max);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por faixa de " + field, e);
        }
    }

    private List<Item> findAllWithOrder(String orderBy) {
        List<Item> itens = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "co.id as cidade_orig_id, co.nome as cidade_orig_nome, co.estado as cidade_orig_estado, " +
                     "po.id as pais_orig_id, po.nome as pais_orig_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM item i " +
                     "LEFT JOIN cidade co ON i.cidade_origem_id = co.id " +
                     "LEFT JOIN pais po ON co.pais_id = po.id " +
                     "LEFT JOIN armazem a ON i.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "ORDER BY i." + orderBy;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                itens.add(mapResultSetToItem(rs));
            }
            return itens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens ordenados", e);
        }
    }

    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setNome(rs.getString("nome"));
        item.setDescricao(rs.getString("descricao"));
        item.setPeso(rs.getBigDecimal("peso"));
        // Preferir quantidade da associação produto_item quando presente
        int quantidade;
        try {
            quantidade = rs.getInt("pi_quantidade");
        } catch (SQLException ignore) {
            quantidade = rs.getInt("quantidade");
        }
        item.setQuantidade(quantidade);
        item.setValorUnitario(rs.getBigDecimal("valor_unitario"));
        item.setSituacao(SituacaoItem.valueOf(rs.getString("situacao")));
        
        Timestamp dataEntrada = rs.getTimestamp("data_entrada");
        if (dataEntrada != null) {
            item.setDataEntrada(dataEntrada.toLocalDateTime());
        }
        
        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            item.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }

        // Mapear cidade de origem
        Cidade cidadeOrigem = new Cidade();
        cidadeOrigem.setId(rs.getLong("cidade_orig_id"));
        cidadeOrigem.setNome(rs.getString("cidade_orig_nome"));
        cidadeOrigem.setEstado(rs.getString("cidade_orig_estado"));
        
        Pais paisOrigem = new Pais();
        paisOrigem.setId(rs.getLong("pais_orig_id"));
        paisOrigem.setNome(rs.getString("pais_orig_nome"));
        cidadeOrigem.setPais(paisOrigem);
        
        item.setCidadeOrigem(cidadeOrigem);

        // Mapear armazém atual (pode ser null)
        Long armazemId = rs.getLong("armazem_id");
        if (!rs.wasNull()) {
            Armazem armazem = new Armazem();
            armazem.setId(armazemId);
            armazem.setNome(rs.getString("armazem_nome"));
            armazem.setCapacidade(rs.getBigDecimal("armazem_capacidade"));
            
            Cidade cidadeArmazem = new Cidade();
            cidadeArmazem.setId(rs.getLong("cidade_arm_id"));
            cidadeArmazem.setNome(rs.getString("cidade_arm_nome"));
            cidadeArmazem.setEstado(rs.getString("cidade_arm_estado"));
            
            Pais paisArmazem = new Pais();
            paisArmazem.setId(rs.getLong("pais_arm_id"));
            paisArmazem.setNome(rs.getString("pais_arm_nome"));
            cidadeArmazem.setPais(paisArmazem);
            
            armazem.setCidade(cidadeArmazem);
            item.setArmazemAtual(armazem);
        }

        return item;
    }
}