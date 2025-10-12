package com.scmflusao.dao.impl;

import com.scmflusao.config.DatabaseConfig;
import com.scmflusao.dao.ProdutoDAO;
import com.scmflusao.model.Produto;
import com.scmflusao.model.SituacaoProduto;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.Armazem;
import com.scmflusao.model.Pais;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoDAOImpl implements ProdutoDAO {

    @Override
    public Produto save(Produto produto) {
        String sql = produto.getId() == null ?
            "INSERT INTO produto (nome, descricao, preco, situacao, cidade_fabricacao_id, armazem_atual_id, data_criacao, data_atualizacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)" :
            "UPDATE produto SET nome = ?, descricao = ?, preco = ?, situacao = ?, cidade_fabricacao_id = ?, armazem_atual_id = ?, data_atualizacao = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setBigDecimal(3, produto.getPreco());
            stmt.setString(4, produto.getSituacao().name());
            stmt.setLong(5, produto.getCidadeFabricacao().getId());
            
            if (produto.getArmazemAtual() != null) {
                stmt.setLong(6, produto.getArmazemAtual().getId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            LocalDateTime agora = LocalDateTime.now();
            
            if (produto.getId() == null) {
                stmt.setTimestamp(7, Timestamp.valueOf(agora));
                stmt.setTimestamp(8, Timestamp.valueOf(agora));
            } else {
                stmt.setTimestamp(7, Timestamp.valueOf(agora));
                stmt.setLong(8, produto.getId());
            }

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar produto, nenhuma linha afetada.");
            }

            if (produto.getId() == null) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    produto.setId(generatedKeys.getLong(1));
                    produto.setDataCriacao(agora);
                } else {
                    throw new SQLException("Falha ao salvar produto, nenhum ID obtido.");
                }
            }
            
            produto.setDataAtualizacao(agora);
            return produto;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Produto update(Produto produto) {
        return save(produto);
    }

    @Override
    public Optional<Produto> findById(Long id) {
        String sql = "SELECT p.*, " +
                     "cf.id as cidade_fab_id, cf.nome as cidade_fab_nome, cf.estado as cidade_fab_estado, " +
                     "pf.id as pais_fab_id, pf.nome as pais_fab_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM produto p " +
                     "LEFT JOIN cidade cf ON p.cidade_fabricacao_id = cf.id " +
                     "LEFT JOIN pais pf ON cf.pais_id = pf.id " +
                     "LEFT JOIN armazem a ON p.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE p.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToProduto(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por ID", e);
        }
    }

    @Override
    public List<Produto> findAll() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "cf.id as cidade_fab_id, cf.nome as cidade_fab_nome, cf.estado as cidade_fab_estado, " +
                     "pf.id as pais_fab_id, pf.nome as pais_fab_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM produto p " +
                     "LEFT JOIN cidade cf ON p.cidade_fabricacao_id = cf.id " +
                     "LEFT JOIN pais pf ON cf.pais_id = pf.id " +
                     "LEFT JOIN armazem a ON p.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String limparAssociacoesProdutoItem = "DELETE FROM produto_item WHERE produto_id = ?";
        String desassociarItensLegados = "UPDATE item SET produto_id = NULL WHERE produto_id = ?";
        String desalocarTransporteProduto = "UPDATE transporte SET produto_id = NULL WHERE produto_id = ?";
        String sqlDeleteProduto = "DELETE FROM produto WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            // Limpa associações na tabela de junção, se existir
            try (PreparedStatement stmtLimparPI = conn.prepareStatement(limparAssociacoesProdutoItem)) {
                stmtLimparPI.setLong(1, id);
                try {
                    stmtLimparPI.executeUpdate();
                } catch (SQLException e) {
                    // Ignora erro caso a tabela produto_item não exista no schema atual
                }
            }

            // Desassocia itens legados que referenciam o produto diretamente
            try (PreparedStatement stmtDesassociarItens = conn.prepareStatement(desassociarItensLegados)) {
                stmtDesassociarItens.setLong(1, id);
                stmtDesassociarItens.executeUpdate();
            }

            // Remove referências de transporte ao produto
            try (PreparedStatement stmtTransporte = conn.prepareStatement(desalocarTransporteProduto)) {
                stmtTransporte.setLong(1, id);
                stmtTransporte.executeUpdate();
            }

            // Remove o produto
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDeleteProduto)) {
                stmtDelete.setLong(1, id);
                stmtDelete.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM produto WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência do produto", e);
        }
    }

    @Override
    public List<Produto> findByNome(String nome) {
        return findByStringField("nome", nome, false);
    }

    @Override
    public List<Produto> findByNomeContaining(String nome) {
        return findByStringField("nome", nome, true);
    }

    @Override
    public List<Produto> findBySituacao(SituacaoProduto situacao) {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "cf.id as cidade_fab_id, cf.nome as cidade_fab_nome, cf.estado as cidade_fab_estado, " +
                     "pf.id as pais_fab_id, pf.nome as pais_fab_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM produto p " +
                     "LEFT JOIN cidade cf ON p.cidade_fabricacao_id = cf.id " +
                     "LEFT JOIN pais pf ON cf.pais_id = pf.id " +
                     "LEFT JOIN armazem a ON p.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE p.situacao = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, situacao.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por situação", e);
        }
    }

    @Override
    public List<Produto> findByCidadeFabricacaoId(Long cidadeId) {
        return findByLongField("cidade_fabricacao_id", cidadeId);
    }

    @Override
    public List<Produto> findByArmazemAtualId(Long armazemId) {
        return findByLongField("armazem_atual_id", armazemId);
    }

    @Override
    public List<Produto> findByPrecoLessThanEqual(BigDecimal preco) {
        return findByPrecoComparison("<=", preco);
    }

    @Override
    public List<Produto> findByPrecoGreaterThanEqual(BigDecimal preco) {
        return findByPrecoComparison(">=", preco);
    }

    @Override
    public List<Produto> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax) {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "cf.id as cidade_fab_id, cf.nome as cidade_fab_nome, cf.estado as cidade_fab_estado, " +
                     "pf.id as pais_fab_id, pf.nome as pais_fab_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM produto p " +
                     "LEFT JOIN cidade cf ON p.cidade_fabricacao_id = cf.id " +
                     "LEFT JOIN pais pf ON cf.pais_id = pf.id " +
                     "LEFT JOIN armazem a ON p.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE p.preco BETWEEN ? AND ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, precoMin);
            stmt.setBigDecimal(2, precoMax);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por faixa de preço", e);
        }
    }

    @Override
    public List<Produto> findAllWithItens() {
        List<Produto> produtos = findAll();
        ItemDAOImpl itemDAO = new ItemDAOImpl();
        for (Produto p : produtos) {
            try {
                List<com.scmflusao.model.Item> itens = itemDAO.findByProdutoId(p.getId());
                p.setItensComponentes(itens);
            } catch (Exception ignored) {
                // Em caso de falha ao carregar itens de um produto específico, mantemos a execução
            }
        }
        return produtos;
    }

    @Override
    public Optional<Produto> findByIdWithItens(Long id) {
        Optional<Produto> produtoOpt = findById(id);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            ItemDAOImpl itemDAO = new ItemDAOImpl();
            List<com.scmflusao.model.Item> itens = itemDAO.findByProdutoId(id);
            produto.setItensComponentes(itens);
            return Optional.of(produto);
        }
        return produtoOpt;
    }

    @Override
    public void atualizarItensDoProduto(Long produtoId, List<com.scmflusao.model.Item> itens) {
        String limparSql = "DELETE FROM produto_item WHERE produto_id = ?";
        // Vamos resolver dinamicamente qual coluna de quantidade existe (quantidade_necessaria ou quantidade)
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            String colunaQuantidade = resolveProdutoItemQuantidadeColumn(conn);
            String vincularSql = "INSERT INTO produto_item (produto_id, item_id, " + colunaQuantidade + ") VALUES (?, ?, ?)";

            try (PreparedStatement limparStmt = conn.prepareStatement(limparSql)) {
                limparStmt.setLong(1, produtoId);
                limparStmt.executeUpdate();
            }

            // Agrega quantidades por item_id para evitar duplicidades e refletir a soma correta
            java.util.Map<Long, Integer> quantidadesPorItem = new java.util.LinkedHashMap<>();
            for (com.scmflusao.model.Item item : itens) {
                if (item != null && item.getId() != null) {
                    int qtd = (item.getQuantidade() != null && item.getQuantidade() > 0) ? item.getQuantidade() : 1;
                    quantidadesPorItem.merge(item.getId(), qtd, Integer::sum);
                }
            }

            try (PreparedStatement vincularStmt = conn.prepareStatement(vincularSql)) {
                for (java.util.Map.Entry<Long, Integer> e : quantidadesPorItem.entrySet()) {
                    vincularStmt.setLong(1, produtoId);
                    vincularStmt.setLong(2, e.getKey());
                    vincularStmt.setInt(3, e.getValue());
                    vincularStmt.addBatch();
                }
                vincularStmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar itens do produto: " + e.getMessage(), e);
        }
    }

    // Determina a coluna de quantidade da tabela produto_item de forma robusta
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
            // Fallback seguro
            return "quantidade";
        }
    }

    @Override
    public List<Produto> findAllOrderByPrecoAsc() {
        return findAllWithOrder("preco ASC");
    }

    @Override
    public List<Produto> findAllOrderByPrecoDesc() {
        return findAllWithOrder("preco DESC");
    }

    @Override
    public List<Produto> findAllOrderByNome() {
        return findAllWithOrder("nome ASC");
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM produto";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar produtos", e);
        }
    }

    @Override
    public long countBySituacao(SituacaoProduto situacao) {
        String sql = "SELECT COUNT(*) FROM produto WHERE situacao = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, situacao.name());
            ResultSet rs = stmt.executeQuery();
            
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar produtos por situação", e);
        }
    }

    // Métodos auxiliares
    private List<Produto> findByStringField(String field, String value, boolean isLike) {
        List<Produto> produtos = new ArrayList<>();
        String operator = isLike ? "LIKE" : "=";
        String paramValue = isLike ? "%" + value + "%" : value;
        
        String sql = "SELECT p.*, " +
                     "cf.id as cidade_fab_id, cf.nome as cidade_fab_nome, cf.estado as cidade_fab_estado, " +
                     "pf.id as pais_fab_id, pf.nome as pais_fab_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM produto p " +
                     "LEFT JOIN cidade cf ON p.cidade_fabricacao_id = cf.id " +
                     "LEFT JOIN pais pf ON cf.pais_id = pf.id " +
                     "LEFT JOIN armazem a ON p.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE p." + field + " " + operator + " ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paramValue);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por " + field, e);
        }
    }

    private List<Produto> findByLongField(String field, Long value) {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "cf.id as cidade_fab_id, cf.nome as cidade_fab_nome, cf.estado as cidade_fab_estado, " +
                     "pf.id as pais_fab_id, pf.nome as pais_fab_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM produto p " +
                     "LEFT JOIN cidade cf ON p.cidade_fabricacao_id = cf.id " +
                     "LEFT JOIN pais pf ON cf.pais_id = pf.id " +
                     "LEFT JOIN armazem a ON p.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE p." + field + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, value);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por " + field, e);
        }
    }

    private List<Produto> findByPrecoComparison(String operator, BigDecimal preco) {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "cf.id as cidade_fab_id, cf.nome as cidade_fab_nome, cf.estado as cidade_fab_estado, " +
                     "pf.id as pais_fab_id, pf.nome as pais_fab_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM produto p " +
                     "LEFT JOIN cidade cf ON p.cidade_fabricacao_id = cf.id " +
                     "LEFT JOIN pais pf ON cf.pais_id = pf.id " +
                     "LEFT JOIN armazem a ON p.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "WHERE p.preco " + operator + " ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, preco);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por preço", e);
        }
    }

    private List<Produto> findAllWithOrder(String orderBy) {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "cf.id as cidade_fab_id, cf.nome as cidade_fab_nome, cf.estado as cidade_fab_estado, " +
                     "pf.id as pais_fab_id, pf.nome as pais_fab_nome, " +
                     "a.id as armazem_id, a.nome as armazem_nome, a.capacidade as armazem_capacidade, " +
                     "ca.id as cidade_arm_id, ca.nome as cidade_arm_nome, ca.estado as cidade_arm_estado, " +
                     "pa.id as pais_arm_id, pa.nome as pais_arm_nome " +
                     "FROM produto p " +
                     "LEFT JOIN cidade cf ON p.cidade_fabricacao_id = cf.id " +
                     "LEFT JOIN pais pf ON cf.pais_id = pf.id " +
                     "LEFT JOIN armazem a ON p.armazem_atual_id = a.id " +
                     "LEFT JOIN cidade ca ON a.cidade_id = ca.id " +
                     "LEFT JOIN pais pa ON ca.pais_id = pa.id " +
                     "ORDER BY p." + orderBy;

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                produtos.add(mapResultSetToProduto(rs));
            }
            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos ordenados", e);
        }
    }

    private Produto mapResultSetToProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getLong("id"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setPreco(rs.getBigDecimal("preco"));
        produto.setSituacao(SituacaoProduto.valueOf(rs.getString("situacao")));
        
        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        if (dataCriacao != null) {
            produto.setDataCriacao(dataCriacao.toLocalDateTime());
        }
        
        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            produto.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }

        // Mapear cidade de fabricação
        Cidade cidadeFabricacao = new Cidade();
        cidadeFabricacao.setId(rs.getLong("cidade_fab_id"));
        cidadeFabricacao.setNome(rs.getString("cidade_fab_nome"));
        cidadeFabricacao.setEstado(rs.getString("cidade_fab_estado"));
        
        Pais paisFabricacao = new Pais();
        paisFabricacao.setId(rs.getLong("pais_fab_id"));
        paisFabricacao.setNome(rs.getString("pais_fab_nome"));
        cidadeFabricacao.setPais(paisFabricacao);
        
        produto.setCidadeFabricacao(cidadeFabricacao);

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
            produto.setArmazemAtual(armazem);
        }

        return produto;
    }
}