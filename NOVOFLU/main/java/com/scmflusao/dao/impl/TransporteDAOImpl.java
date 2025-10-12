package com.scmflusao.dao.impl;

import com.scmflusao.config.DatabaseConfig;
import com.scmflusao.dao.TransporteDAO;
import com.scmflusao.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransporteDAOImpl implements TransporteDAO {

    @Override
    public Transporte save(Transporte t) {
        String sql = t.getId() == null ?
            "INSERT INTO transporte (empresa_parceira_id, agente_id, origem_cidade_id, destino_cidade_id, origem_armazem_id, destino_armazem_id, produto_id, item_id, situacao, data_inicio, data_fim) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" :
            "UPDATE transporte SET empresa_parceira_id = ?, agente_id = ?, origem_cidade_id = ?, destino_cidade_id = ?, origem_armazem_id = ?, destino_armazem_id = ?, produto_id = ?, item_id = ?, situacao = ?, data_inicio = ?, data_fim = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, t.getEmpresaParceira().getId());
            if (t.getAgente() != null) stmt.setLong(2, t.getAgente().getId()); else stmt.setNull(2, Types.BIGINT);
            stmt.setLong(3, t.getOrigemCidade().getId());
            stmt.setLong(4, t.getDestinoCidade().getId());
            if (t.getOrigemArmazem() != null) stmt.setLong(5, t.getOrigemArmazem().getId()); else stmt.setNull(5, Types.BIGINT);
            if (t.getDestinoArmazem() != null) stmt.setLong(6, t.getDestinoArmazem().getId()); else stmt.setNull(6, Types.BIGINT);
            if (t.getProduto() != null) stmt.setLong(7, t.getProduto().getId()); else stmt.setNull(7, Types.BIGINT);
            if (t.getItem() != null) stmt.setLong(8, t.getItem().getId()); else stmt.setNull(8, Types.BIGINT);
            stmt.setString(9, t.getSituacao().name());
            Timestamp inicio = t.getDataInicio() != null ? Timestamp.valueOf(t.getDataInicio()) : null;
            Timestamp fim = t.getDataFim() != null ? Timestamp.valueOf(t.getDataFim()) : null;
            if (inicio != null) stmt.setTimestamp(10, inicio); else stmt.setNull(10, Types.TIMESTAMP);
            if (fim != null) stmt.setTimestamp(11, fim); else stmt.setNull(11, Types.TIMESTAMP);

            if (t.getId() != null) {
                stmt.setLong(12, t.getId());
            }

            int affected = stmt.executeUpdate();
            if (affected == 0) throw new SQLException("Falha ao salvar transporte");

            if (t.getId() == null) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    t.setId(keys.getLong(1));
                }
            }
            return t;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar transporte: " + e.getMessage(), e);
        }
    }

    @Override
    public Transporte update(Transporte t) {
        return save(t);
    }

    @Override
    public Optional<Transporte> findById(Long id) {
        String sql = baseSelect() + " WHERE t.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transporte por ID", e);
        }
    }

    @Override
    public List<Transporte> findAll() {
        List<Transporte> list = new ArrayList<>();
        String sql = baseSelect();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar transportes", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM transporte WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar transporte", e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM transporte WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência do transporte", e);
        }
    }

    @Override
    public List<Transporte> findBySituacao(SituacaoTransporte situacao) {
        return findByField("t.situacao = ?", ps -> ps.setString(1, situacao.name()));
    }

    @Override
    public List<Transporte> findByEmpresaParceiraId(Long empresaParceiraId) {
        return findByField("t.empresa_parceira_id = ?", ps -> ps.setLong(1, empresaParceiraId));
    }

    @Override
    public List<Transporte> findByAgenteId(Long agenteId) {
        return findByField("t.agente_id = ?", ps -> ps.setLong(1, agenteId));
    }

    @Override
    public List<Transporte> findByProdutoId(Long produtoId) {
        return findByField("t.produto_id = ?", ps -> ps.setLong(1, produtoId));
    }

    @Override
    public List<Transporte> findByItemId(Long itemId) {
        return findByField("t.item_id = ?", ps -> ps.setLong(1, itemId));
    }

    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Transporte> findByField(String where, Binder binder) {
        List<Transporte> list = new ArrayList<>();
        String sql = baseSelect() + " WHERE " + where;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            binder.bind(ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar transportes", e);
        }
    }

    private String baseSelect() {
        return "SELECT t.*, " +
               "ep.nome AS empresa_nome, a.nome AS agente_nome, " +
               "co.nome AS origem_nome, co.estado AS origem_estado, cd.nome AS destino_nome, cd.estado AS destino_estado, " +
               "ao.nome AS origem_armazem_nome, ad.nome AS destino_armazem_nome, " +
               "p.nome AS produto_nome, i.nome AS item_nome " +
               "FROM transporte t " +
               "LEFT JOIN empresa_parceira ep ON t.empresa_parceira_id = ep.id " +
               "LEFT JOIN agente a ON t.agente_id = a.id " +
               "LEFT JOIN cidade co ON t.origem_cidade_id = co.id " +
               "LEFT JOIN cidade cd ON t.destino_cidade_id = cd.id " +
               "LEFT JOIN armazem ao ON t.origem_armazem_id = ao.id " +
               "LEFT JOIN armazem ad ON t.destino_armazem_id = ad.id " +
               "LEFT JOIN produto p ON t.produto_id = p.id " +
               "LEFT JOIN item i ON t.item_id = i.id";
    }

    private Transporte map(ResultSet rs) throws SQLException {
        Transporte t = new Transporte();
        t.setId(rs.getLong("id"));
        EmpresaParceira ep = new EmpresaParceira(); ep.setId(rs.getLong("empresa_parceira_id")); ep.setNome(rs.getString("empresa_nome")); t.setEmpresaParceira(ep);
        Long agenteId = rs.getLong("agente_id"); if (!rs.wasNull()) { Agente ag = new Agente(); ag.setId(agenteId); ag.setNome(rs.getString("agente_nome")); t.setAgente(ag);} 
        Cidade origem = new Cidade(); origem.setId(rs.getLong("origem_cidade_id")); origem.setNome(rs.getString("origem_nome")); origem.setEstado(rs.getString("origem_estado")); t.setOrigemCidade(origem);
        Cidade destino = new Cidade(); destino.setId(rs.getLong("destino_cidade_id")); destino.setNome(rs.getString("destino_nome")); destino.setEstado(rs.getString("destino_estado")); t.setDestinoCidade(destino);
        Long aoId = rs.getLong("origem_armazem_id"); if (!rs.wasNull()) { Armazem ao = new Armazem(); ao.setId(aoId); ao.setNome(rs.getString("origem_armazem_nome")); t.setOrigemArmazem(ao);} 
        Long adId = rs.getLong("destino_armazem_id"); if (!rs.wasNull()) { Armazem ad = new Armazem(); ad.setId(adId); ad.setNome(rs.getString("destino_armazem_nome")); t.setDestinoArmazem(ad);} 
        Long pId = rs.getLong("produto_id"); if (!rs.wasNull()) { Produto p = new Produto(); p.setId(pId); p.setNome(rs.getString("produto_nome")); t.setProduto(p);} 
        Long iId = rs.getLong("item_id"); if (!rs.wasNull()) { Item i = new Item(); i.setId(iId); i.setNome(rs.getString("item_nome")); t.setItem(i);} 
        String sit = rs.getString("situacao"); t.setSituacao(SituacaoTransporte.valueOf(sit));
        Timestamp inicio = rs.getTimestamp("data_inicio"); if (inicio != null) t.setDataInicio(inicio.toLocalDateTime());
        Timestamp fim = rs.getTimestamp("data_fim"); if (fim != null) t.setDataFim(fim.toLocalDateTime());
        return t;
    }
}
