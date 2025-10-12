package com.scmflusao.dao;

import com.scmflusao.model.Transporte;
import com.scmflusao.model.SituacaoTransporte;

import java.util.List;
import java.util.Optional;

public interface TransporteDAO {
    Transporte save(Transporte transporte);
    Transporte update(Transporte transporte);
    Optional<Transporte> findById(Long id);
    List<Transporte> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Transporte> findBySituacao(SituacaoTransporte situacao);
    List<Transporte> findByEmpresaParceiraId(Long empresaParceiraId);
    List<Transporte> findByAgenteId(Long agenteId);
    List<Transporte> findByProdutoId(Long produtoId);
    List<Transporte> findByItemId(Long itemId);
}