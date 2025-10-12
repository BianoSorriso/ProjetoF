package com.scmflusao.dao.impl;

import com.scmflusao.config.DatabaseConfig;
import com.scmflusao.dao.PaisDAO;
import com.scmflusao.model.Pais;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaisDAOImpl implements PaisDAO {

    @Override
    public Pais save(Pais pais) {
        String sql = pais.getId() == null ?
            "INSERT INTO pais (nome, codigo) VALUES (?, ?)" :
            "UPDATE pais SET nome = ?, codigo = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, pais.getNome());
            stmt.setString(2, pais.getCodigo());

            if (pais.getId() != null) {
                stmt.setLong(3, pais.getId());
            }

            stmt.executeUpdate();

            if (pais.getId() == null) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    pais.setId(generatedKeys.getLong(1));
                }
            }

            return pais;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar país: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Pais update(Pais pais) {
        // Reutiliza o método save para atualizar o país
        return save(pais);
    }

    @Override
    public Optional<Pais> findById(Long id) {
        String sql = "SELECT id, nome, codigo FROM pais WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Pais pais = new Pais();
                pais.setId(rs.getLong("id"));
                pais.setNome(rs.getString("nome"));
                pais.setCodigo(rs.getString("codigo"));
                return Optional.of(pais);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar país por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pais> findAll() {
        String sql = "SELECT id, nome, codigo FROM pais";
        List<Pais> paises = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Pais pais = new Pais();
                pais.setId(rs.getLong("id"));
                pais.setNome(rs.getString("nome"));
                pais.setCodigo(rs.getString("codigo"));
                paises.add(pais);
            }

            return paises;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar países: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM pais WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir país: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM pais WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de país: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pais> findByNomeContaining(String nome) {
        String sql = "SELECT id, nome, codigo FROM pais WHERE nome LIKE ?";
        List<Pais> paises = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pais pais = new Pais();
                pais.setId(rs.getLong("id"));
                pais.setNome(rs.getString("nome"));
                pais.setCodigo(rs.getString("codigo"));
                paises.add(pais);
            }

            return paises;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar países por nome: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Pais> findBySigla(String sigla) {
        String sql = "SELECT id, nome, codigo FROM pais WHERE codigo = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sigla);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Pais pais = new Pais();
                pais.setId(rs.getLong("id"));
                pais.setNome(rs.getString("nome"));
                pais.setCodigo(rs.getString("codigo"));
                return Optional.of(pais);
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar país por sigla: " + e.getMessage(), e);
        }
    }
}