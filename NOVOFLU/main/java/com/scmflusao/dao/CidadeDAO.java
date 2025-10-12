package com.scmflusao.dao;

import com.scmflusao.model.Cidade;
import java.util.List;
import java.util.Optional;

public interface CidadeDAO {
    Cidade save(Cidade cidade);
    Cidade update(Cidade cidade);
    Optional<Cidade> findById(Long id);
    List<Cidade> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Cidade> findByPaisId(Long paisId);
    List<Cidade> findByNome(String nome);
    List<Cidade> findByNomeContaining(String nome);
    List<Cidade> findByEhFabricaTrue();
    List<Cidade> findByEhOrigemTrue();
    List<Cidade> findAllWithArmazens();
    Optional<Cidade> findByIdWithArmazens(Long id);
    List<Cidade> findByNomeWithArmazens(String nome);
    List<Cidade> findByEstadoWithArmazens(String estado);
    long count();
    List<Cidade> findByEstado(String estado);
}