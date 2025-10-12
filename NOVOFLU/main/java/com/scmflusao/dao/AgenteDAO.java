package com.scmflusao.dao;

import com.scmflusao.model.Agente;
import java.util.List;
import java.util.Optional;

public interface AgenteDAO {
    Agente save(Agente agente);
    Optional<Agente> findById(Long id);
    List<Agente> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Agente> findByEmpresaParceiraId(Long empresaParceiraId);
    List<Agente> findByEmpresaParceiraIdAndDisponivelTrue(Long empresaParceiraId);
    List<Agente> findByCargo(String cargo);
    Optional<Agente> findByEmail(String email);
    Optional<Agente> findByCpf(String cpf);
    Optional<Agente> findByTelefone(String telefone);
    List<Agente> findByDisponivelTrue();
    List<Agente> findByDisponivelFalse();
    List<Agente> findByNomeContaining(String nome);
    Agente update(Agente agente);
    
    /**
     * Busca agentes por nome exato
     * @param nome Nome do agente
     * @return Lista de agentes encontrados
     */
    List<Agente> findByNome(String nome);
    
    /**
     * Busca todos os agentes com seus transportes associados
     * @return Lista de agentes com transportes
     */
    List<Agente> findAllWithTransportes();
    
    /**
     * Busca um agente por ID com seus transportes associados
     * @param id ID do agente
     * @return Agente com transportes
     */
    Optional<Agente> findByIdWithTransportes(Long id);
    
    /**
     * Conta o número total de agentes
     * @return Número de agentes
     */
    long count();
}