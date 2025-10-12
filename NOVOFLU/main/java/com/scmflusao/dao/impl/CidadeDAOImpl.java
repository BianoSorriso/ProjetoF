package com.scmflusao.dao.impl;

import com.scmflusao.config.DatabaseConfig;
import com.scmflusao.dao.CidadeDAO;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.Pais;
import com.scmflusao.model.Armazem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CidadeDAOImpl implements CidadeDAO {

    @Override
    public Cidade save(Cidade cidade) {
        String sql = cidade.getId() == null ?
            "INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES (?, ?, ?, ?, ?)" :
            "UPDATE cidade SET nome = ?, estado = ?, pais_id = ?, eh_fabrica = ?, eh_origem = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cidade.getNome());
            stmt.setString(2, cidade.getEstado());
            stmt.setLong(3, cidade.getPais().getId());
            stmt.setBoolean(4, cidade.isEhFabrica());
            stmt.setBoolean(5, cidade.isEhOrigem());

            if (cidade.getId() != null) {
                stmt.setLong(6, cidade.getId());
            }

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar cidade, nenhuma linha afetada.");
            }

            if (cidade.getId() == null) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    cidade.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Falha ao salvar cidade, nenhum ID obtido.");
                }
            }

            return cidade;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cidade: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Cidade update(Cidade cidade) {
        // Reutiliza o método save para atualizar a cidade
        return save(cidade);
    }

    @Override
    public Optional<Cidade> findById(Long id) {
        String sql = "SELECT c.*, p.nome as pais_nome FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "WHERE c.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToCidade(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidade por ID", e);
        }
    }

    @Override
    public List<Cidade> findAll() {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cidades.add(mapResultSetToCidade(rs));
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cidades", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM cidade WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar cidade", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM cidade WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência da cidade", e);
        }
    }

    @Override
    public List<Cidade> findByPaisId(Long paisId) {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "WHERE c.pais_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, paisId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cidades.add(mapResultSetToCidade(rs));
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidades por país", e);
        }
    }

    @Override
    public List<Cidade> findByNome(String nome) {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "WHERE c.nome = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cidades.add(mapResultSetToCidade(rs));
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidades por nome exato", e);
        }
    }
    

    @Override
    public List<Cidade> findByNomeContaining(String nome) {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "WHERE c.nome LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cidades.add(mapResultSetToCidade(rs));
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidades por nome", e);
        }
    }

    @Override
    public List<Cidade> findByEhFabricaTrue() {
        return findByBoolean("eh_fabrica", true);
    }

    @Override
    public List<Cidade> findByEhOrigemTrue() {
        return findByBoolean("eh_origem", true);
    }

    @Override
    public List<Cidade> findAllWithArmazens() {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome, a.id as armazem_id, a.nome as armazem_nome, " +
                     "a.endereco, a.capacidade, a.ocupacao, a.ativo " +
                     "FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "LEFT JOIN armazem a ON a.cidade_id = c.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Long currentCidadeId = null;
            Cidade currentCidade = null;

            while (rs.next()) {
                Long cidadeId = rs.getLong("id");

                if (currentCidadeId == null || !currentCidadeId.equals(cidadeId)) {
                    currentCidade = mapResultSetToCidade(rs);
                    currentCidadeId = cidadeId;
                    cidades.add(currentCidade);
                }

                Long armazemId = rs.getLong("armazem_id");
                if (!rs.wasNull()) {
                    Armazem armazem = new Armazem();
                    armazem.setId(armazemId);
                    armazem.setNome(rs.getString("armazem_nome"));
                    armazem.setEndereco(rs.getString("endereco"));
                    armazem.setCapacidade(rs.getBigDecimal("capacidade"));
                    armazem.setOcupacao(rs.getBigDecimal("ocupacao"));
                    armazem.setAtivo(rs.getBoolean("ativo"));
                    armazem.setCidade(currentCidade);
                    currentCidade.addArmazem(armazem);
                }
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar cidades com armazéns", e);
        }
    }
    
    @Override
    public Optional<Cidade> findByIdWithArmazens(Long id) {
        String sql = "SELECT c.*, p.nome as pais_nome, a.id as armazem_id, a.nome as armazem_nome, " +
                     "a.endereco, a.capacidade, a.ocupacao, a.ativo " +
                     "FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "LEFT JOIN armazem a ON a.cidade_id = c.id " +
                     "WHERE c.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            Cidade cidade = null;

            while (rs.next()) {
                if (cidade == null) {
                    cidade = mapResultSetToCidade(rs);
                }

                Long armazemId = rs.getLong("armazem_id");
                if (!rs.wasNull()) {
                    Armazem armazem = new Armazem();
                    armazem.setId(armazemId);
                    armazem.setNome(rs.getString("armazem_nome"));
                    armazem.setEndereco(rs.getString("endereco"));
                    armazem.setCapacidade(rs.getBigDecimal("capacidade"));
                    armazem.setOcupacao(rs.getBigDecimal("ocupacao"));
                    armazem.setAtivo(rs.getBoolean("ativo"));
                    armazem.setCidade(cidade);
                    cidade.addArmazem(armazem);
                }
            }
            
            return Optional.ofNullable(cidade);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidade com armazéns por ID", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM cidade";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar cidades", e);
        }
    }

    @Override
    public List<Cidade> findByEstado(String estado) {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "WHERE c.estado = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cidades.add(mapResultSetToCidade(rs));
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidades por estado", e);
        }
    }
    
    @Override
    public List<Cidade> findByNomeWithArmazens(String nome) {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome, a.id as armazem_id, a.nome as armazem_nome, " +
                     "a.endereco, a.capacidade, a.ocupacao, a.ativo " +
                     "FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "LEFT JOIN armazem a ON a.cidade_id = c.id " +
                     "WHERE c.nome = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            Long currentCidadeId = null;
            Cidade currentCidade = null;

            while (rs.next()) {
                Long cidadeId = rs.getLong("id");

                if (currentCidadeId == null || !currentCidadeId.equals(cidadeId)) {
                    currentCidade = mapResultSetToCidade(rs);
                    currentCidadeId = cidadeId;
                    cidades.add(currentCidade);
                }

                Long armazemId = rs.getLong("armazem_id");
                if (!rs.wasNull()) {
                    Armazem armazem = new Armazem();
                    armazem.setId(armazemId);
                    armazem.setNome(rs.getString("armazem_nome"));
                    armazem.setEndereco(rs.getString("endereco"));
                    armazem.setCapacidade(rs.getBigDecimal("capacidade"));
                    armazem.setOcupacao(rs.getBigDecimal("ocupacao"));
                    armazem.setAtivo(rs.getBoolean("ativo"));
                    armazem.setCidade(currentCidade);
                    currentCidade.addArmazem(armazem);
                }
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidades por nome com armazéns", e);
        }
    }
    
    @Override
    public List<Cidade> findByEstadoWithArmazens(String estado) {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome, a.id as armazem_id, a.nome as armazem_nome, " +
                     "a.endereco, a.capacidade, a.ocupacao, a.ativo " +
                     "FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "LEFT JOIN armazem a ON a.cidade_id = c.id " +
                     "WHERE c.estado = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();

            Long currentCidadeId = null;
            Cidade currentCidade = null;

            while (rs.next()) {
                Long cidadeId = rs.getLong("id");

                if (currentCidadeId == null || !currentCidadeId.equals(cidadeId)) {
                    currentCidade = mapResultSetToCidade(rs);
                    currentCidadeId = cidadeId;
                    cidades.add(currentCidade);
                }

                Long armazemId = rs.getLong("armazem_id");
                if (!rs.wasNull()) {
                    Armazem armazem = new Armazem();
                    armazem.setId(armazemId);
                    armazem.setNome(rs.getString("armazem_nome"));
                    armazem.setEndereco(rs.getString("endereco"));
                    armazem.setCapacidade(rs.getBigDecimal("capacidade"));
                    armazem.setOcupacao(rs.getBigDecimal("ocupacao"));
                    armazem.setAtivo(rs.getBoolean("ativo"));
                    armazem.setCidade(currentCidade);
                    currentCidade.addArmazem(armazem);
                }
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidades por estado com armazéns", e);
        }
    }

    private List<Cidade> findByBoolean(String field, boolean value) {
        List<Cidade> cidades = new ArrayList<>();
        String sql = "SELECT c.*, p.nome as pais_nome FROM cidade c " +
                     "LEFT JOIN pais p ON c.pais_id = p.id " +
                     "WHERE c." + field + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, value);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cidades.add(mapResultSetToCidade(rs));
            }
            return cidades;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cidades por " + field, e);
        }
    }

    private Cidade mapResultSetToCidade(ResultSet rs) throws SQLException {
        Cidade cidade = new Cidade();
        cidade.setId(rs.getLong("id"));
        cidade.setNome(rs.getString("nome"));
        cidade.setEstado(rs.getString("estado"));
        

        
        cidade.setEhFabrica(rs.getBoolean("eh_fabrica"));
        cidade.setEhOrigem(rs.getBoolean("eh_origem"));

        Pais pais = new Pais();
        pais.setId(rs.getLong("pais_id"));
        pais.setNome(rs.getString("pais_nome"));
        cidade.setPais(pais);

        return cidade;
    }
}