package com.scmflusao.dao.impl;

import com.scmflusao.config.DatabaseConfig;
import com.scmflusao.dao.ArmazemDAO;
import com.scmflusao.model.Armazem;
import com.scmflusao.model.Cidade;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ArmazemDAOImpl implements ArmazemDAO {

    @Override
    public Armazem save(Armazem armazem) {
        String sql = armazem.getId() == null ?
            "INSERT INTO armazem (nome, endereco, capacidade, ocupacao, ativo, cidade_id) VALUES (?, ?, ?, ?, ?, ?)" :
            "UPDATE armazem SET nome = ?, endereco = ?, capacidade = ?, ocupacao = ?, ativo = ?, cidade_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, armazem.getNome());
            stmt.setString(2, armazem.getEndereco());
            stmt.setBigDecimal(3, armazem.getCapacidade());
            stmt.setBigDecimal(4, armazem.getOcupacao());
            stmt.setBoolean(5, armazem.isAtivo());
            stmt.setLong(6, armazem.getCidade().getId());

            if (armazem.getId() != null) {
                stmt.setLong(7, armazem.getId());
            }

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar armazém, nenhuma linha afetada.");
            }

            if (armazem.getId() == null) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    armazem.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Falha ao salvar armazém, nenhum ID obtido.");
                }
            }

            return armazem;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar armazém: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Armazem update(Armazem armazem) {
        // Reutiliza o método save para atualizar o armazém
        return save(armazem);
    }

    @Override
    public Optional<Armazem> findById(Long id) {
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToArmazem(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazém por ID", e);
        }
    }

    @Override
    public List<Armazem> findAll() {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar armazéns", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM armazem WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar armazém", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM armazem WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência do armazém", e);
        }
    }

    @Override
    public List<Armazem> findByCidadeId(Long cidadeId) {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.cidade_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, cidadeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazéns por cidade", e);
        }
    }

    @Override
    public List<Armazem> findByAtivoTrue() {
        return findByAtivo(true);
    }

    @Override
    public List<Armazem> findByAtivoFalse() {
        return findByAtivo(false);
    }

    @Override
    public List<Armazem> findByNomeContaining(String nome) {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.nome LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazéns por nome", e);
        }
    }

    @Override
    public List<Armazem> findByCapacidadeGreaterThan(BigDecimal capacidade) {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.capacidade > ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, capacidade);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazéns por capacidade", e);
        }
    }

    @Override
    public List<Armazem> findByOcupacaoLessThan(BigDecimal ocupacao) {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.ocupacao < ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, ocupacao);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazéns por ocupação", e);
        }
    }

    private List<Armazem> findByAtivo(boolean ativo) {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.ativo = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, ativo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazéns por status ativo", e);
        }
    }

    private Armazem mapResultSetToArmazem(ResultSet rs) throws SQLException {
        Armazem armazem = new Armazem();
        armazem.setId(rs.getLong("id"));
        armazem.setNome(rs.getString("nome"));
        armazem.setEndereco(rs.getString("endereco"));
        armazem.setCapacidade(rs.getBigDecimal("capacidade"));
        armazem.setOcupacao(rs.getBigDecimal("ocupacao"));
        armazem.setAtivo(rs.getBoolean("ativo"));

        Cidade cidade = new Cidade();
        cidade.setId(rs.getLong("cidade_id"));
        cidade.setNome(rs.getString("cidade_nome"));
        cidade.setEstado(rs.getString("estado"));
        armazem.setCidade(cidade);

        return armazem;
    }
    
    @Override
    public List<Armazem> findByNome(String nome) {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.nome = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazéns por nome", e);
        }
    }
    
    @Override
    public List<Armazem> findByCapacidadeDisponivelGreaterThan(java.math.BigDecimal capacidade) {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.capacidade - a.ocupacao > ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, capacidade);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazéns por capacidade disponível", e);
        }
    }
    
    @Override
    public List<Armazem> findByCapacidadeBetween(java.math.BigDecimal capacidadeMin, java.math.BigDecimal capacidadeMax) {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "WHERE a.capacidade BETWEEN ? AND ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, capacidadeMin);
            stmt.setBigDecimal(2, capacidadeMax);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazéns por intervalo de capacidade", e);
        }
    }
    
    @Override
    public List<Armazem> findAllOrderByCapacidadeDisponivelDesc() {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "ORDER BY (a.capacidade - a.ocupacao) DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                armazens.add(mapResultSetToArmazem(rs));
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar armazéns ordenados por capacidade disponível", e);
        }
    }
    
    @Override
    public List<Armazem> findAllWithItens() {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado, i.id as item_id, i.nome as item_nome, " +
                     "i.quantidade, i.valor_unitario, i.data_entrada " +
                     "FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "LEFT JOIN item i ON i.armazem_id = a.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Long currentArmazemId = null;
            Armazem currentArmazem = null;

            while (rs.next()) {
                Long armazemId = rs.getLong("id");

                if (currentArmazemId == null || !currentArmazemId.equals(armazemId)) {
                    currentArmazem = mapResultSetToArmazem(rs);
                    currentArmazemId = armazemId;
                    armazens.add(currentArmazem);
                }

                // Aqui você adicionaria os itens ao armazém, se existirem
                // Isso depende da sua estrutura de modelo
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar armazéns com itens", e);
        }
    }
    
    @Override
    public Optional<Armazem> findByIdWithItens(Long id) {
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado, i.id as item_id, i.nome as item_nome, " +
                     "i.quantidade, i.valor_unitario, i.data_entrada " +
                     "FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "LEFT JOIN item i ON i.armazem_id = a.id " +
                     "WHERE a.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Armazem armazem = mapResultSetToArmazem(rs);
                
                // Aqui você adicionaria os itens ao armazém, se existirem
                // Isso depende da sua estrutura de modelo
                
                return Optional.of(armazem);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazém com itens por ID", e);
        }
    }
    
    @Override
    public List<Armazem> findAllWithProdutos() {
        List<Armazem> armazens = new ArrayList<>();
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado, p.id as produto_id, p.nome as produto_nome, " +
                     "p.descricao, p.categoria, p.valor " +
                     "FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "LEFT JOIN armazem_produto ap ON ap.armazem_id = a.id " +
                     "LEFT JOIN produto p ON p.id = ap.produto_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Long currentArmazemId = null;
            Armazem currentArmazem = null;

            while (rs.next()) {
                Long armazemId = rs.getLong("id");

                if (currentArmazemId == null || !currentArmazemId.equals(armazemId)) {
                    currentArmazem = mapResultSetToArmazem(rs);
                    currentArmazemId = armazemId;
                    armazens.add(currentArmazem);
                }

                // Aqui você adicionaria os produtos ao armazém, se existirem
                // Isso depende da sua estrutura de modelo
            }
            return armazens;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar armazéns com produtos", e);
        }
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM armazem";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar armazéns", e);
        }
    }
    
    @Override
    public Optional<Armazem> findByIdWithProdutos(Long id) {
        String sql = "SELECT a.*, c.nome as cidade_nome, c.estado, p.id as produto_id, p.nome as produto_nome, " +
                     "p.descricao, p.categoria, p.valor " +
                     "FROM armazem a " +
                     "LEFT JOIN cidade c ON a.cidade_id = c.id " +
                     "LEFT JOIN armazem_produto ap ON ap.armazem_id = a.id " +
                     "LEFT JOIN produto p ON p.id = ap.produto_id " +
                     "WHERE a.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Armazem armazem = mapResultSetToArmazem(rs);
                
                // Aqui você adicionaria os produtos ao armazém, se existirem
                // Isso depende da sua estrutura de modelo
                
                return Optional.of(armazem);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar armazém com produtos por ID", e);
        }
    }
}