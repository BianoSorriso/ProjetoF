package com.scmflusao.dao;

import com.scmflusao.model.EmpresaParceira;
import com.scmflusao.model.TipoServico;
import java.util.List;
import java.util.Optional;

public interface EmpresaParceiraDAO {
    EmpresaParceira save(EmpresaParceira empresaParceira);
    Optional<EmpresaParceira> findById(Long id);
    List<EmpresaParceira> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    List<EmpresaParceira> findByTipoServico(TipoServico tipoServico);
    List<EmpresaParceira> findByAtivoTrue();
    List<EmpresaParceira> findByAtivoFalse();
    List<EmpresaParceira> findByNomeContaining(String nome);
    Optional<EmpresaParceira> findByCnpj(String cnpj);
    Optional<EmpresaParceira> findByEmail(String email);
    Optional<EmpresaParceira> findByTelefone(String telefone);
    EmpresaParceira update(EmpresaParceira empresaParceira);
    
    /**
     * Busca empresas parceiras por nome
     * @param nome Nome da empresa
     * @return Lista de empresas encontradas
     */
    List<EmpresaParceira> findByNome(String nome);
    
    /**
     * Busca todas as empresas parceiras com seus agentes associados
     * @return Lista de empresas com agentes
     */
    List<EmpresaParceira> findAllWithAgentes();
    
    /**
     * Busca uma empresa parceira por ID com seus agentes associados
     * @param id ID da empresa
     * @return Empresa com agentes
     */
    Optional<EmpresaParceira> findByIdWithAgentes(Long id);
    
    /**
     * Busca todas as empresas parceiras com seus transportes associados
     * @return Lista de empresas com transportes
     */
    List<EmpresaParceira> findAllWithTransportes();
    
    /**
     * Busca uma empresa parceira por ID com seus transportes associados
     * @param id ID da empresa
     * @return Empresa com transportes
     */
    Optional<EmpresaParceira> findByIdWithTransportes(Long id);
    
    /**
     * Conta o número total de empresas parceiras
     * @return Número de empresas parceiras
     */
    long count();
}