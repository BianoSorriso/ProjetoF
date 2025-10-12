package com.scmflusao.dao.impl;

import com.scmflusao.config.DatabaseConfig;
import com.scmflusao.dao.AgenteDAO;
import com.scmflusao.model.Agente;
import com.scmflusao.model.EmpresaParceira;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgenteDAOImpl implements AgenteDAO {

    @Override
    public Agente save(Agente agente) {
        String sql = agente.getId() == null ?
            "INSERT INTO agente (nome, cpf, cargo, data_nascimento, telefone, email, disponivel, empresa_parceira_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)" :
            "UPDATE agente SET nome = ?, cpf = ?, cargo = ?, data_nascimento = ?, telefone = ?, email = ?, disponivel = ?, empresa_parceira_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, agente.getNome());
            stmt.setString(2, agente.getCpf());
            stmt.setString(3, agente.getCargo());
            stmt.setDate(4, Date.valueOf(agente.getDataNascimento()));
            stmt.setString(5, agente.getTelefone());
            stmt.setString(6, agente.getEmail());
            stmt.setBoolean(7, agente.isDisponivel());
            stmt.setLong(8, agente.getEmpresaParceira().getId());

            if (agente.getId() != null) {
                stmt.setLong(9, agente.getId());
            }

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar agente, nenhuma linha afetada.");
            }

            if (agente.getId() == null) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    agente.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Falha ao salvar agente, nenhum ID obtido.");
                }
            }

            return agente;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar agente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Agente> findById(Long id) {
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAgente(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agente por ID", e);
        }
    }

    @Override
    public List<Agente> findAll() {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agentes", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM agente WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Agente com ID " + id + " não encontrado.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar agente", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM agente WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência do agente", e);
        }
    }

    @Override
    public List<Agente> findByEmpresaParceiraId(Long empresaParceiraId) {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.empresa_parceira_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaParceiraId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agentes por empresa parceira", e);
        }
    }

    @Override
    public List<Agente> findByEmpresaParceiraIdAndDisponivelTrue(Long empresaParceiraId) {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.empresa_parceira_id = ? AND a.disponivel = true";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, empresaParceiraId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agentes disponíveis por empresa parceira", e);
        }
    }

    @Override
    public List<Agente> findByCargo(String cargo) {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.cargo = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cargo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agentes por cargo", e);
        }
    }
    
    @Override
    public Optional<Agente> findByEmail(String email) {
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAgente(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agente por email", e);
        }
    }
    
    @Override
    public Optional<Agente> findByCpf(String cpf) {
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.cpf = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAgente(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agente por CPF", e);
        }
    }

    @Override
    public List<Agente> findByDisponivelTrue() {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.disponivel = true";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agentes disponíveis", e);
        }
    }

    @Override
    public List<Agente> findByDisponivelFalse() {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.disponivel = false";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agentes indisponíveis", e);
        }
    }

    @Override
    public List<Agente> findByNomeContaining(String nome) {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.nome LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agentes por nome", e);
        }
    }
    
    @Override
    public Optional<Agente> findByTelefone(String telefone) {
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.telefone = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, telefone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAgente(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agente por telefone", e);
        }
    }
    
    @Override
    public Agente update(Agente agente) {
        // Reutiliza o método save para atualizar o agente
        return save(agente);
    }
    
    @Override
    public List<Agente> findByNome(String nome) {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "WHERE a.nome = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agentes por nome", e);
        }
    }
    
    @Override
    public List<Agente> findAllWithTransportes() {
        List<Agente> agentes = new ArrayList<>();
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "LEFT JOIN transporte t ON a.id = t.agente_id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                agentes.add(mapResultSetToAgente(rs));
            }
            return agentes;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar agentes com transportes", e);
        }
    }
    
    @Override
    public Optional<Agente> findByIdWithTransportes(Long id) {
        String sql = "SELECT a.*, ep.nome as empresa_nome, ep.cnpj FROM agente a " +
                     "LEFT JOIN empresa_parceira ep ON a.empresa_parceira_id = ep.id " +
                     "LEFT JOIN transporte t ON a.id = t.agente_id " +
                     "WHERE a.id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAgente(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar agente com transportes por ID", e);
        }
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM agente";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar agentes", e);
        }
    }

    private Agente mapResultSetToAgente(ResultSet rs) throws SQLException {
        Agente agente = new Agente();
        agente.setId(rs.getLong("id"));
        agente.setNome(rs.getString("nome"));
        agente.setCpf(rs.getString("cpf"));
        agente.setCargo(rs.getString("cargo"));
        agente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        agente.setTelefone(rs.getString("telefone"));
        agente.setEmail(rs.getString("email"));
        agente.setDisponivel(rs.getBoolean("disponivel"));

        EmpresaParceira empresaParceira = new EmpresaParceira();
        empresaParceira.setId(rs.getLong("empresa_parceira_id"));
        empresaParceira.setNome(rs.getString("empresa_nome"));
        empresaParceira.setCnpj(rs.getString("cnpj"));
        agente.setEmpresaParceira(empresaParceira);

        return agente;
    }
}