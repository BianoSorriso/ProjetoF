package com.scmflusao.dao;

import com.scmflusao.model.Armazem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ArmazemDAO {
    Armazem save(Armazem armazem);
    Armazem update(Armazem armazem);
    Optional<Armazem> findById(Long id);
    List<Armazem> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Armazem> findByCidadeId(Long cidadeId);
    List<Armazem> findByAtivoTrue();
    List<Armazem> findByAtivoFalse();
    List<Armazem> findByNomeContaining(String nome);
    List<Armazem> findByCapacidadeGreaterThan(BigDecimal capacidade);
    List<Armazem> findByOcupacaoLessThan(BigDecimal ocupacao);
    List<Armazem> findByNome(String nome);
    List<Armazem> findByCapacidadeDisponivelGreaterThan(BigDecimal capacidade);
    List<Armazem> findByCapacidadeBetween(BigDecimal capacidadeMin, BigDecimal capacidadeMax);
    List<Armazem> findAllOrderByCapacidadeDisponivelDesc();
    List<Armazem> findAllWithItens();
    Optional<Armazem> findByIdWithItens(Long id);
    List<Armazem> findAllWithProdutos();
    Optional<Armazem> findByIdWithProdutos(Long id);
    long count();
}