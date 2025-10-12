package com.scmflusao.dao;

import com.scmflusao.model.Pais;
import java.util.List;
import java.util.Optional;

public interface PaisDAO {
    Pais save(Pais pais);
    Pais update(Pais pais);
    Optional<Pais> findById(Long id);
    List<Pais> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Pais> findByNomeContaining(String nome);
    Optional<Pais> findBySigla(String codigo);
}