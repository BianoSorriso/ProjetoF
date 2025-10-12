package com.scmflusao.controller;

import com.scmflusao.dao.AgenteDAO;
import com.scmflusao.dao.EmpresaParceiraDAO;
import com.scmflusao.dao.impl.AgenteDAOImpl;
import com.scmflusao.dao.impl.EmpresaParceiraDAOImpl;
import com.scmflusao.model.Agente;
import com.scmflusao.model.EmpresaParceira;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para operações relacionadas a Agente
 */
public class AgenteController {
    
    private final AgenteDAO agenteDAO;
    private final EmpresaParceiraDAO empresaParceiraDAO;
    
    public AgenteController() {
        this.agenteDAO = new AgenteDAOImpl();
        this.empresaParceiraDAO = new EmpresaParceiraDAOImpl();
    }
    
    /**
     * Cria um novo agente
     * @param nome Nome do agente
     * @param cpf CPF do agente
     * @param telefone Telefone do agente
     * @param email Email do agente
     * @param cargo Cargo do agente
     * @param dataNascimento Data de nascimento do agente
     * @param disponivel Se está disponível
     * @param empresaParceiraId ID da empresa parceira
     * @return Agente criado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Agente criarAgente(String nome, String cpf, String telefone, String email, 
                             String cargo, LocalDate dataNascimento, boolean disponivel, Long empresaParceiraId) {
        validarDadosAgente(nome, cpf, telefone, email, cargo, empresaParceiraId);
        
        // Verifica se a empresa parceira existe
        Optional<EmpresaParceira> empresaParceira = empresaParceiraDAO.findById(empresaParceiraId);
        if (empresaParceira.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + empresaParceiraId);
        }
        
        // Verifica se já existe um agente com o mesmo CPF
        Optional<Agente> agenteComMesmoCpf = agenteDAO.findByCpf(cpf);
        if (agenteComMesmoCpf.isPresent()) {
            throw new IllegalArgumentException("Já existe um agente com o CPF: " + cpf);
        }
        
        // Verifica se já existe um agente com o mesmo email
        Optional<Agente> agenteComMesmoEmail = agenteDAO.findByEmail(email);
        if (agenteComMesmoEmail.isPresent()) {
            throw new IllegalArgumentException("Já existe um agente com o email: " + email);
        }
        
        Agente agente = new Agente(nome, cpf, telefone, email, cargo, disponivel, empresaParceira.get());
        agente.setDataNascimento(dataNascimento);
        return agenteDAO.save(agente);
    }
    
    /**
     * Atualiza um agente existente
     * @param id ID do agente
     * @param nome Novo nome
     * @param cpf Novo CPF
     * @param telefone Novo telefone
     * @param email Novo email
     * @param cargo Novo cargo
     * @param dataNascimento Nova data de nascimento
     * @param disponivel Se está disponível
     * @param empresaParceiraId ID da empresa parceira
     * @return Agente atualizado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Agente atualizarAgente(Long id, String nome, String cpf, String telefone, String email, 
                                 String cargo, LocalDate dataNascimento, boolean disponivel, Long empresaParceiraId) {
        validarDadosAgente(nome, cpf, telefone, email, cargo, empresaParceiraId);
        
        Optional<Agente> agenteExistente = agenteDAO.findById(id);
        if (agenteExistente.isEmpty()) {
            throw new IllegalArgumentException("Agente não encontrado com ID: " + id);
        }
        
        // Verifica se a empresa parceira existe
        Optional<EmpresaParceira> empresaParceira = empresaParceiraDAO.findById(empresaParceiraId);
        if (empresaParceira.isEmpty()) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + empresaParceiraId);
        }
        
        Agente agente = agenteExistente.get();
        
        // Verifica se o novo CPF já existe em outro agente
        Optional<Agente> agenteComMesmoCpf = agenteDAO.findByCpf(cpf);
        if (agenteComMesmoCpf.isPresent() && !agenteComMesmoCpf.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outro agente com o CPF: " + cpf);
        }
        
        // Verifica se o novo email já existe em outro agente
        Optional<Agente> agenteComMesmoEmail = agenteDAO.findByEmail(email);
        if (agenteComMesmoEmail.isPresent() && !agenteComMesmoEmail.get().getId().equals(id)) {
            throw new IllegalArgumentException("Já existe outro agente com o email: " + email);
        }
        
        agente.setNome(nome);
        agente.setCpf(cpf);
        agente.setTelefone(telefone);
        agente.setEmail(email);
        agente.setCargo(cargo);
        agente.setDataNascimento(dataNascimento);
        agente.setDisponivel(disponivel);
        agente.setEmpresaParceira(empresaParceira.get());
        
        return agenteDAO.update(agente);
    }
    
    /**
     * Remove um agente
     * @param id ID do agente
     * @return true se removido com sucesso
     * @throws IllegalArgumentException Se o agente não existir
     */
    public boolean removerAgente(Long id) {
        if (!agenteDAO.existsById(id)) {
            throw new IllegalArgumentException("Agente não encontrado com ID: " + id);
        }
        
        agenteDAO.deleteById(id);
        return true;
    }
    
    /**
     * Busca um agente por ID
     * @param id ID do agente
     * @return Agente encontrado
     * @throws IllegalArgumentException Se o agente não existir
     */
    public Agente buscarAgentePorId(Long id) {
        Optional<Agente> agente = agenteDAO.findById(id);
        if (agente.isEmpty()) {
            throw new IllegalArgumentException("Agente não encontrado com ID: " + id);
        }
        return agente.get();
    }
    
    /**
     * Busca agentes por nome
     * @param nome Nome do agente
     * @return Lista de agentes encontrados
     */
    public List<Agente> buscarAgentesPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return agenteDAO.findByNome(nome.trim());
    }
    
    /**
     * Busca agente por CPF
     * @param cpf CPF do agente
     * @return Agente encontrado
     * @throws IllegalArgumentException Se o agente não existir
     */
    public Agente buscarAgentePorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio");
        }
        
        Optional<Agente> agente = agenteDAO.findByCpf(cpf.trim());
        if (agente.isEmpty()) {
            throw new IllegalArgumentException("Agente não encontrado com CPF: " + cpf);
        }
        return agente.get();
    }
    
    /**
     * Busca agente por email
     * @param email Email do agente
     * @return Agente encontrado
     * @throws IllegalArgumentException Se o agente não existir
     */
    public Agente buscarAgentePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        
        Optional<Agente> agente = agenteDAO.findByEmail(email.trim());
        if (agente.isEmpty()) {
            throw new IllegalArgumentException("Agente não encontrado com email: " + email);
        }
        return agente.get();
    }
    
    /**
     * Busca agente por telefone
     * @param telefone Telefone do agente
     * @return Agente encontrado
     * @throws IllegalArgumentException Se o agente não existir
     */
    public Agente buscarAgentePorTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone não pode ser vazio");
        }
        
        Optional<Agente> agente = agenteDAO.findByTelefone(telefone.trim());
        if (agente.isEmpty()) {
            throw new IllegalArgumentException("Agente não encontrado com telefone: " + telefone);
        }
        return agente.get();
    }
    
    /**
     * Busca agentes por empresa parceira
     * @param empresaParceiraId ID da empresa parceira
     * @return Lista de agentes encontrados
     */
    public List<Agente> buscarAgentesPorEmpresaParceira(Long empresaParceiraId) {
        if (!empresaParceiraDAO.existsById(empresaParceiraId)) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + empresaParceiraId);
        }
        return agenteDAO.findByEmpresaParceiraId(empresaParceiraId);
    }
    
    /**
     * Busca agentes disponíveis por empresa parceira
     * @param empresaParceiraId ID da empresa parceira
     * @return Lista de agentes disponíveis
     */
    public List<Agente> buscarAgentesDisponiveisPorEmpresaParceira(Long empresaParceiraId) {
        if (!empresaParceiraDAO.existsById(empresaParceiraId)) {
            throw new IllegalArgumentException("Empresa parceira não encontrada com ID: " + empresaParceiraId);
        }
        return agenteDAO.findByEmpresaParceiraIdAndDisponivelTrue(empresaParceiraId);
    }
    
    /**
     * Busca agentes por cargo
     * @param cargo Cargo do agente
     * @return Lista de agentes encontrados
     */
    public List<Agente> buscarAgentesPorCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo não pode ser vazio");
        }
        return agenteDAO.findByCargo(cargo.trim());
    }
    
    /**
     * Lista todos os agentes
     * @return Lista de agentes
     */
    public List<Agente> listarTodosAgentes() {
        return agenteDAO.findAll();
    }
    
    /**
     * Lista agentes disponíveis
     * @return Lista de agentes disponíveis
     */
    public List<Agente> listarAgentesDisponiveis() {
        return agenteDAO.findByDisponivelTrue();
    }
    
    /**
     * Lista agentes indisponíveis
     * @return Lista de agentes indisponíveis
     */
    public List<Agente> listarAgentesIndisponiveis() {
        return agenteDAO.findByDisponivelFalse();
    }
    
    /**
     * Busca agentes que contenham o nome especificado
     * @param nome Parte do nome do agente
     * @return Lista de agentes encontrados
     */
    public List<Agente> buscarAgentesPorNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return agenteDAO.findByNomeContaining(nome.trim());
    }
    
    /**
     * Lista agentes com seus transportes
     * @return Lista de agentes com transportes
     */
    public List<Agente> listarAgentesComTransportes() {
        return agenteDAO.findAllWithTransportes();
    }
    
    /**
     * Busca um agente com seus transportes
     * @param id ID do agente
     * @return Agente com transportes
     * @throws IllegalArgumentException Se o agente não existir
     */
    public Agente buscarAgenteComTransportes(Long id) {
        Optional<Agente> agente = agenteDAO.findByIdWithTransportes(id);
        if (agente.isEmpty()) {
            throw new IllegalArgumentException("Agente não encontrado com ID: " + id);
        }
        return agente.get();
    }
    
    /**
     * Altera a disponibilidade de um agente
     * @param id ID do agente
     * @param disponivel Nova disponibilidade
     * @return Agente atualizado
     * @throws IllegalArgumentException Se o agente não existir
     */
    public Agente alterarDisponibilidadeAgente(Long id, boolean disponivel) {
        Optional<Agente> agenteExistente = agenteDAO.findById(id);
        if (agenteExistente.isEmpty()) {
            throw new IllegalArgumentException("Agente não encontrado com ID: " + id);
        }
        
        Agente agente = agenteExistente.get();
        agente.setDisponivel(disponivel);
        
        return agenteDAO.update(agente);
    }
    
    /**
     * Conta o número total de agentes
     * @return Número de agentes
     */
    public long contarAgentes() {
        return agenteDAO.count();
    }
    
    /**
     * Verifica se um agente existe
     * @param id ID do agente
     * @return true se existe
     */
    public boolean agenteExiste(Long id) {
        return agenteDAO.existsById(id);
    }
    
    /**
     * Valida os dados de um agente
     * @param nome Nome do agente
     * @param cpf CPF do agente
     * @param telefone Telefone do agente
     * @param email Email do agente
     * @param cargo Cargo do agente
     * @param empresaParceiraId ID da empresa parceira
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    private void validarDadosAgente(String nome, String cpf, String telefone, String email, 
                                   String cargo, Long empresaParceiraId) {
        // Nota: A validação de dataNascimento é feita separadamente quando necessário
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do agente é obrigatório");
        }
        
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do agente é obrigatório");
        }
        
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefone do agente é obrigatório");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email do agente é obrigatório");
        }
        
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo do agente é obrigatório");
        }
        
        if (empresaParceiraId == null) {
            throw new IllegalArgumentException("Empresa parceira é obrigatória");
        }
        
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome do agente deve ter pelo menos 2 caracteres");
        }
        
        if (cargo.trim().length() < 2) {
            throw new IllegalArgumentException("Cargo deve ter pelo menos 2 caracteres");
        }
        
        // Validação básica do CPF (11 dígitos)
        String cpfLimpo = cpf.trim().replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
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