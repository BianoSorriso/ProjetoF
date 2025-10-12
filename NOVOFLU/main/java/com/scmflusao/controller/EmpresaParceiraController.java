package com.scmflusao.controller;

import com.scmflusao.dao.EmpresaParceiraDAO;
import com.scmflusao.dao.impl.EmpresaParceiraDAOImpl;
import com.scmflusao.model.EmpresaParceira;
import com.scmflusao.model.TipoServico;

import java.util.List;
import java.util.Optional;

/**
 * Controller para operações relacionadas a Empresa Parceira
 */
public class EmpresaParceiraController {
    
    private final EmpresaParceiraDAO empresaParceiraDAO;
    
    public EmpresaParceiraController() {
        this.empresaParceiraDAO = new EmpresaParceiraDAOImpl();
    }
    
    /**
     * Cria uma nova empresa parceira
     * @param nome Nome da empresa
     * @param cnpj CNPJ da empresa
     * @param endereco Endereço da empresa
     * @param telefone Telefone da empresa
     * @param email Email da empresa
     * @param tipoServico Tipo de serviço
     * @param ativo Se a empresa está ativa
     * @return Empresa parceira criada
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public EmpresaParceira criarEmpresaParceira(String nome, String cnpj, String endereco, 
                                               String telefone, String email, TipoServico tipoServico, boolean ativo) {
        validarDadosEmpresaParceira(nome, cnpj, endereco, telefone, email, tipoServico);
        
        // Verifica se já existe uma empresa com o mesmo CNPJ
        Optional<EmpresaParceira> empresaComMesmoCnpj = empresaParceiraDAO.findByCnpj(cnpj);
        if (empresaComMesmoCnpj.isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa com o CNPJ: " + cnpj);
        }
        
        // Verifica se já existe uma empresa com o mesmo email
        Optional<EmpresaParceira> empresaComMesmoEmail = empresaParceiraDAO.findByEmail(email);
        if (empresaComMesmoEmail.isPresent()) {
            throw new IllegalArgumentException("Já existe uma empresa com o email: " + email);
        }
        
        EmpresaParceira empresa = new EmpresaParceira();
        empresa.setNome(nome);
        empresa.setCnpj(cnpj);
        empresa.setEndereco(endereco);
        empresa.setTelefone(telefone);
        empresa.setEmail(email);
        empresa.setTipoServico(tipoServico);
        empresa.setAtivo(ativo);
        return empresaParceiraDAO.save(empresa);
    }
    
    /**
     * Atualiza uma empresa parceira existente
     * @param id ID da empresa
     * @param nome Novo nome
     * @param cnpj Novo CNPJ
     * @param endereco Novo endereço
     * @param telefone Novo telefone
     * @param email Novo email
     * @param tipoServico Novo tipo de serviço
     * @param ativo Se a empresa está ativa
     * @return Empresa parceira atualizada
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public EmpresaParceira atualizarEmpresaParceira(Long id, String nome, String cnpj, String endereco, 
                                                   String telefone, String email, TipoServico tipoServico, boolean ativo) {
        validarDadosEmpresaParceira(nome, cnpj, endereco, telefone, email, tipoServico);
        
        Optional<EmpresaParceira> empresaExistente = empresaParceiraDAO.findById(id);
        if (empresaExistente.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + id);
        }
        
        EmpresaParceira empresa = empresaExistente.get();
        
        // Verifica se o novo CNPJ já existe em outra empresa
        Optional<EmpresaParceira> empresaComMesmoCnpj = empresaParceiraDAO.findByCnpj(cnpj);
        if (empresaComMesmoCnpj.isPresent() && !empresaComMesmoCnpj.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outra empresa com o CNPJ: " + cnpj);
        }
        
        // Verifica se o novo email já existe em outra empresa
        Optional<EmpresaParceira> empresaComMesmoEmail = empresaParceiraDAO.findByEmail(email);
        if (empresaComMesmoEmail.isPresent() && !empresaComMesmoEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outra empresa com o email: " + email);
        }
        
        empresa.setNome(nome);
        empresa.setCnpj(cnpj);
        empresa.setEndereco(endereco);
        empresa.setTelefone(telefone);
        empresa.setEmail(email);
        empresa.setTipoServico(tipoServico);
        
        return empresaParceiraDAO.update(empresa);
    }
    
    /**
     * Remove uma empresa parceira
     * @param id ID da empresa
     * @return true se removida com sucesso
     * @throws IllegalArgumentException Se a empresa não existir
     */
    public boolean removerEmpresaParceira(Long id) {
        if (!empresaParceiraDAO.existsById(id)) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + id);
        }
        
        empresaParceiraDAO.deleteById(id);
        return true;
    }
    
    /**
     * Busca uma empresa parceira por ID
     * @param id ID da empresa
     * @return Empresa parceira encontrada
     * @throws IllegalArgumentException Se a empresa não existir
     */
    public EmpresaParceira buscarEmpresaParceiraPorId(Long id) {
        Optional<EmpresaParceira> empresa = empresaParceiraDAO.findById(id);
        if (empresa.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + id);
        }
        return empresa.get();
    }
    
    /**
     * Busca empresas parceiras por nome
     * @param nome Nome da empresa
     * @return Lista de empresas encontradas
     */
    public List<EmpresaParceira> buscarEmpresasParceirasPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return empresaParceiraDAO.findByNome(nome.trim());
    }
    
    /**
     * Busca empresa parceira por CNPJ
     * @param cnpj CNPJ da empresa
     * @return Empresa encontrada
     * @throws IllegalArgumentException Se a empresa não existir
     */
    public EmpresaParceira buscarEmpresaParceiraPorCnpj(String cnpj) {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("CNPJ não pode ser vazio");
        }
        
        Optional<EmpresaParceira> empresa = empresaParceiraDAO.findByCnpj(cnpj.trim());
        if (empresa.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com CNPJ: " + cnpj);
        }
        return empresa.get();
    }
    
    /**
     * Busca empresa parceira por email
     * @param email Email da empresa
     * @return Empresa encontrada
     * @throws IllegalArgumentException Se a empresa não existir
     */
    public EmpresaParceira buscarEmpresaParceiraPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        
        Optional<EmpresaParceira> empresa = empresaParceiraDAO.findByEmail(email.trim());
        if (empresa.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com email: " + email);
        }
        return empresa.get();
    }
    
    /**
     * Busca empresa parceira por telefone
     * @param telefone Telefone da empresa
     * @return Empresa encontrada
     * @throws IllegalArgumentException Se a empresa não existir
     */
    public EmpresaParceira buscarEmpresaParceiraPorTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone não pode ser vazio");
        }
        
        Optional<EmpresaParceira> empresa = empresaParceiraDAO.findByTelefone(telefone.trim());
        if (empresa.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com telefone: " + telefone);
        }
        return empresa.get();
    }
    
    /**
     * Busca empresas parceiras por tipo de serviço
     * @param tipoServico Tipo de serviço
     * @return Lista de empresas encontradas
     */
    public List<EmpresaParceira> buscarEmpresasParceirasPorTipoServico(TipoServico tipoServico) {
        if (tipoServico == null) {
            throw new IllegalArgumentException("Tipo de serviço não pode ser nulo");
        }
        return empresaParceiraDAO.findByTipoServico(tipoServico);
    }
    
    /**
     * Lista todas as empresas parceiras
     * @return Lista de empresas parceiras
     */
    public List<EmpresaParceira> listarTodasEmpresasParceiras() {
        return empresaParceiraDAO.findAll();
    }
    
    /**
     * Busca empresas parceiras que contenham o nome especificado
     * @param nome Parte do nome da empresa
     * @return Lista de empresas encontradas
     */
    public List<EmpresaParceira> buscarEmpresasParceirasPorNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return empresaParceiraDAO.findByNomeContaining(nome.trim());
    }
    
    /**
     * Lista empresas parceiras com seus agentes
     * @return Lista de empresas com agentes
     */
    public List<EmpresaParceira> listarEmpresasParceirasComAgentes() {
        return empresaParceiraDAO.findAllWithAgentes();
    }
    
    /**
     * Busca uma empresa parceira com seus agentes
     * @param id ID da empresa
     * @return Empresa com agentes
     * @throws IllegalArgumentException Se a empresa não existir
     */
    public EmpresaParceira buscarEmpresaParceiraComAgentes(Long id) {
        Optional<EmpresaParceira> empresa = empresaParceiraDAO.findByIdWithAgentes(id);
        if (empresa.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + id);
        }
        return empresa.get();
    }
    
    /**
     * Lista empresas parceiras com seus transportes
     * @return Lista de empresas com transportes
     */
    public List<EmpresaParceira> listarEmpresasParceirasComTransportes() {
        return empresaParceiraDAO.findAllWithTransportes();
    }
    
    /**
     * Busca uma empresa parceira com seus transportes
     * @param id ID da empresa
     * @return Empresa com transportes
     * @throws IllegalArgumentException Se a empresa não existir
     */
    public EmpresaParceira buscarEmpresaParceiraComTransportes(Long id) {
        Optional<EmpresaParceira> empresa = empresaParceiraDAO.findByIdWithTransportes(id);
        if (empresa.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + id);
        }
        return empresa.get();
    }
    
    /**
     * Conta o número total de empresas parceiras
     * @return Número de empresas parceiras
     */
    public long contarEmpresasParceiras() {
        return empresaParceiraDAO.count();
    }
    
    /**
     * Verifica se uma empresa parceira existe
     * @param id ID da empresa
     * @return true se existe
     */
    public boolean empresaParceiraExiste(Long id) {
        return empresaParceiraDAO.existsById(id);
    }
    
    /**
     * Valida os dados de uma empresa parceira
     * @param nome Nome da empresa
     * @param cnpj CNPJ da empresa
     * @param endereco Endereço da empresa
     * @param telefone Telefone da empresa
     * @param email Email da empresa
     * @param tipoServico Tipo de serviço
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    private void validarDadosEmpresaParceira(String nome, String cnpj, String endereco, 
                                            String telefone, String email, TipoServico tipoServico) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da empresa é obrigatório");
        }
        
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("CNPJ da empresa é obrigatório");
        }
        
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço da empresa é obrigatório");
        }
        
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone da empresa é obrigatório");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email da empresa é obrigatório");
        }
        
        if (tipoServico == null) {
            throw new IllegalArgumentException("Tipo de serviço é obrigatório");
        }
        
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome da empresa deve ter pelo menos 2 caracteres");
        }
        
        if (endereco.trim().length() < 5) {
            throw new IllegalArgumentException("Endereço deve ter pelo menos 5 caracteres");
        }
        
        // Validação básica do CNPJ (14 dígitos)
        String cnpjLimpo = cnpj.trim().replaceAll("[^0-9]", "");
        if (cnpjLimpo.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve ter 14 dígitos");
        }
        
        // Validação básica do telefone (pelo menos 10 dígitos)
        String telefoneLimpo = telefone.trim().replaceAll("[^0-9]", "");
        if (telefoneLimpo.length() < 10) {
            throw new IllegalArgumentException("Telefone deve ter pelo menos 10 dígitos");
        }
        
        // Validação básica do email
        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email deve ter um formato válido");
        }
    }
}