package com.scmflusao.dao.impl;

import com.scmflusao.config.DatabaseConfig;
import com.scmflusao.dao.EmpresaParceiraDAO;
import com.scmflusao.model.EmpresaParceira;
import com.scmflusao.model.TipoServico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpresaParceiraDAOImpl implements EmpresaParceiraDAO {

    @Override
    public EmpresaParceira save(EmpresaParceira empresaParceira) {
        String sql = empresaParceira.getId() == null ?
            "INSERT INTO empresa_parceira (nome, cnpj, endereco, telefone, email, tipo_servico, ativo) VALUES (?, ?, ?, ?, ?, ?, ?)" :
            "UPDATE empresa_parceira SET nome = ?, cnpj = ?, endereco = ?, telefone = ?, email = ?, tipo_servico = ?, ativo = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, empresaParceira.getNome());
            stmt.setString(2, empresaParceira.getCnpj());
            stmt.setString(3, empresaParceira.getEndereco());
            stmt.setString(4, empresaParceira.getTelefone());
            stmt.setString(5, empresaParceira.getEmail());
            stmt.setString(6, empresaParceira.getTipoServico().name());
            stmt.setBoolean(7, empresaParceira.isAtivo());

            if (empresaParceira.getId() != null) {
                stmt.setLong(8, empresaParceira.getId());
            }

            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Falha ao salvar empresa parceira, nenhuma linha afetada.");
            }

            if (empresaParceira.getId() == null) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    empresaParceira.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Falha ao salvar empresa parceira, nenhum ID obtido.");
                }
            }

            return empresaParceira;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar empresa parceira: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<EmpresaParceira> findById(Long id) {
        String sql = "SELECT * FROM empresa_parceira WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmpresaParceira(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresa parceira por ID", e);
        }
    }

    @Override
    public List<EmpresaParceira> findAll() {
        List<EmpresaParceira> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa_parceira";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresaParceira(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empresas parceiras", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM empresa_parceira WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar empresa parceira", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM empresa_parceira WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência da empresa parceira", e);
        }
    }

    @Override
    public List<EmpresaParceira> findByTipoServico(TipoServico tipoServico) {
        List<EmpresaParceira> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa_parceira WHERE tipo_servico = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoServico.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresaParceira(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresas por tipo de serviço", e);
        }
    }

    @Override
    public List<EmpresaParceira> findByAtivoTrue() {
        List<EmpresaParceira> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa_parceira WHERE ativo = true";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresaParceira(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresas ativas", e);
        }
    }

    @Override
    public List<EmpresaParceira> findByAtivoFalse() {
        List<EmpresaParceira> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa_parceira WHERE ativo = false";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresaParceira(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresas inativas", e);
        }
    }

    @Override
    public List<EmpresaParceira> findByNomeContaining(String nome) {
        List<EmpresaParceira> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa_parceira WHERE nome LIKE ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresaParceira(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresas por nome", e);
        }
    }

    @Override
    public Optional<EmpresaParceira> findByCnpj(String cnpj) {
        String sql = "SELECT * FROM empresa_parceira WHERE cnpj = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmpresaParceira(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresa por CNPJ", e);
        }
    }
    
    @Override
    public Optional<EmpresaParceira> findByEmail(String email) {
        String sql = "SELECT * FROM empresa_parceira WHERE email = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmpresaParceira(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresa por email", e);
        }
    }
    
    @Override
    public Optional<EmpresaParceira> findByTelefone(String telefone) {
        String sql = "SELECT * FROM empresa_parceira WHERE telefone = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, telefone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmpresaParceira(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresa por telefone", e);
        }
    }
    
    @Override
    public EmpresaParceira update(EmpresaParceira empresaParceira) {
        return save(empresaParceira);
    }
    
    @Override
    public List<EmpresaParceira> findByNome(String nome) {
        List<EmpresaParceira> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa_parceira WHERE nome = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresaParceira(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresas parceiras por nome", e);
        }
    }
    
    @Override
    public List<EmpresaParceira> findAllWithAgentes() {
        List<EmpresaParceira> empresas = new ArrayList<>();
        String sql = "SELECT ep.* FROM empresa_parceira ep " +
                     "LEFT JOIN agente a ON ep.id = a.empresa_parceira_id " +
                     "GROUP BY ep.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresaParceira(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empresas parceiras com agentes", e);
        }
    }
    
    @Override
    public Optional<EmpresaParceira> findByIdWithAgentes(Long id) {
        String sql = "SELECT ep.* FROM empresa_parceira ep " +
                     "LEFT JOIN agente a ON ep.id = a.empresa_parceira_id " +
                     "WHERE ep.id = ? " +
                     "GROUP BY ep.id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmpresaParceira(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresa parceira com agentes por ID", e);
        }
    }
    
    @Override
    public List<EmpresaParceira> findAllWithTransportes() {
        List<EmpresaParceira> empresas = new ArrayList<>();
        String sql = "SELECT ep.* FROM empresa_parceira ep " +
                     "LEFT JOIN agente a ON ep.id = a.empresa_parceira_id " +
                     "LEFT JOIN transporte t ON a.id = t.agente_id " +
                     "GROUP BY ep.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                empresas.add(mapResultSetToEmpresaParceira(rs));
            }
            return empresas;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empresas parceiras com transportes", e);
        }
    }
    
    @Override
    public Optional<EmpresaParceira> findByIdWithTransportes(Long id) {
        String sql = "SELECT ep.* FROM empresa_parceira ep " +
                     "LEFT JOIN agente a ON ep.id = a.empresa_parceira_id " +
                     "LEFT JOIN transporte t ON a.id = t.agente_id " +
                     "WHERE ep.id = ? " +
                     "GROUP BY ep.id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmpresaParceira(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresa parceira com transportes por ID", e);
        }
    }
    
    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM empresa_parceira";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar empresas parceiras", e);
        }
    }

    private EmpresaParceira mapResultSetToEmpresaParceira(ResultSet rs) throws SQLException {
        EmpresaParceira empresaParceira = new EmpresaParceira();
        empresaParceira.setId(rs.getLong("id"));
        empresaParceira.setNome(rs.getString("nome"));
        empresaParceira.setCnpj(rs.getString("cnpj"));
        empresaParceira.setEndereco(rs.getString("endereco"));
        empresaParceira.setTelefone(rs.getString("telefone"));
        empresaParceira.setEmail(rs.getString("email"));
        empresaParceira.setTipoServico(TipoServico.valueOf(rs.getString("tipo_servico")));
        empresaParceira.setAtivo(rs.getBoolean("ativo"));
        return empresaParceira;
    }
}