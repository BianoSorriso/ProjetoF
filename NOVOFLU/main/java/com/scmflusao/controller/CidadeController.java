package com.scmflusao.controller;

import com.scmflusao.dao.CidadeDAO;
import com.scmflusao.dao.PaisDAO;
import com.scmflusao.dao.impl.CidadeDAOImpl;
import com.scmflusao.dao.impl.PaisDAOImpl;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.Pais;

import java.util.List;
import java.util.Optional;

/**
 * Controller para operações relacionadas a Cidade
 */
public class CidadeController {
    
    private final CidadeDAO cidadeDAO;
    private final PaisDAO paisDAO;
    
    public CidadeController() {
        this.cidadeDAO = new CidadeDAOImpl();
        this.paisDAO = new PaisDAOImpl();
    }
    
    /**
     * Cria uma nova cidade
     * @param nome Nome da cidade
     * @param estado Estado da cidade
     * @param ehFabrica Se é uma fábrica
     * @param ehOrigem Se é origem
     * @param paisId ID do país
     * @return Cidade criada
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Cidade criarCidade(String nome, String estado, boolean ehFabrica, boolean ehOrigem, Long paisId) {
        validarDadosCidade(nome, estado, paisId);
        
        // Verifica se o país existe
        Optional<Pais> pais = paisDAO.findById(paisId);
        if (pais.isEmpty()) {
            throw new IllegalArgumentException("País não encontrado com ID: " + paisId);
        }
        
        // Verifica se já existe uma cidade com o mesmo nome no mesmo estado
        List<Cidade> cidadesExistentes = cidadeDAO.findByNome(nome);
        for (Cidade cidadeExistente : cidadesExistentes) {
            if (cidadeExistente.getEstado().equalsIgnoreCase(estado)) {
                throw new IllegalArgumentException("Já existe uma cidade com o nome " + nome + " no estado " + estado);
            }
        }
        
        Cidade cidade = new Cidade(nome, estado, ehFabrica, ehOrigem, pais.get());
        return cidadeDAO.save(cidade);
    }
    
    /**
     * Atualiza uma cidade existente
     * @param id ID da cidade
     * @param nome Novo nome
     * @param estado Novo estado
     * @param ehFabrica Se é uma fábrica
     * @param ehOrigem Se é origem
     * @param paisId ID do país
     * @return Cidade atualizada
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Cidade atualizarCidade(Long id, String nome, String estado, boolean ehFabrica, boolean ehOrigem, Long paisId) {
        validarDadosCidade(nome, estado, paisId);
        
        Optional<Cidade> cidadeExistente = cidadeDAO.findById(id);
        if (cidadeExistente.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + id);
        }
        
        // Verifica se o país existe
        Optional<Pais> pais = paisDAO.findById(paisId);
        if (pais.isEmpty()) {
            throw new IllegalArgumentException("País não encontrado com ID: " + paisId);
        }
        
        Cidade cidade = cidadeExistente.get();
        
        // Verifica se o novo nome já existe em outro estado (exceto a própria cidade)
        List<Cidade> cidadesComMesmoNome = cidadeDAO.findByNome(nome);
        for (Cidade cidadeComMesmoNome : cidadesComMesmoNome) {
            if (!cidadeComMesmoNome.getId().equals(id) && cidadeComMesmoNome.getEstado().equalsIgnoreCase(estado)) {
                throw new IllegalArgumentException("Já existe outra cidade com o nome " + nome + " no estado " + estado);
            }
        }
        
        cidade.setNome(nome);
        cidade.setEstado(estado);
        cidade.setEhFabrica(ehFabrica);
        cidade.setEhOrigem(ehOrigem);
        cidade.setPais(pais.get());
        
        return cidadeDAO.update(cidade);
    }
    
    /**
     * Remove uma cidade
     * @param id ID da cidade
     * @return true se removida com sucesso
     * @throws IllegalArgumentException Se a cidade não existir
     */
    public boolean removerCidade(Long id) {
        if (!cidadeDAO.existsById(id)) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + id);
        }
        
        cidadeDAO.deleteById(id);
        return true;
    }
    
    /**
     * Busca uma cidade por ID
     * @param id ID da cidade
     * @return Cidade encontrada
     * @throws IllegalArgumentException Se a cidade não existir
     */
    public Cidade buscarCidadePorId(Long id) {
        Optional<Cidade> cidade = cidadeDAO.findById(id);
        if (cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + id);
        }
        return cidade.get();
    }
    
    /**
     * Busca cidades por nome
     * @param nome Nome da cidade
     * @return Lista de cidades encontradas
     */
    public List<Cidade> buscarCidadesPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return cidadeDAO.findByNome(nome.trim());
    }
    
    /**
     * Busca cidades por nome com armazéns carregados
     * @param nome Nome da cidade
     * @return Lista de cidades encontradas com armazéns
     */
    public List<Cidade> buscarCidadesPorNomeComArmazens(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return cidadeDAO.findByNomeWithArmazens(nome.trim());
    }
    
    /**
     * Busca cidades por estado
     * @param estado Estado da cidade
     * @return Lista de cidades encontradas
     */
    public List<Cidade> buscarCidadesPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado não pode ser vazio");
        }
        return cidadeDAO.findByEstado(estado.trim());
    }
    
    /**
     * Busca cidades por estado com armazéns carregados
     * @param estado Estado da cidade
     * @return Lista de cidades encontradas com armazéns
     */
    public List<Cidade> buscarCidadesPorEstadoComArmazens(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado não pode ser vazio");
        }
        return cidadeDAO.findByEstadoWithArmazens(estado.trim());
    }
    
    /**
     * Busca cidades por país
     * @param paisId ID do país
     * @return Lista de cidades encontradas
     */
    public List<Cidade> buscarCidadesPorPais(Long paisId) {
        if (!paisDAO.existsById(paisId)) {
            throw new IllegalArgumentException("País não encontrado com ID: " + paisId);
        }
        return cidadeDAO.findByPaisId(paisId);
    }
    

    /**
     * Lista todas as cidades
     * @return Lista de cidades
     */
    public List<Cidade> listarTodasCidades() {
        return cidadeDAO.findAll();
    }
    
    /**
     * Lista cidades que são fábricas
     * @return Lista de cidades fábricas
     */
    public List<Cidade> listarCidadesFabricas() {
        return cidadeDAO.findByEhFabricaTrue();
    }
    
    /**
     * Lista cidades que são origem
     * @return Lista de cidades origem
     */
    public List<Cidade> listarCidadesOrigem() {
        return cidadeDAO.findByEhOrigemTrue();
    }
    
    /**
     * Busca cidades que contenham o nome especificado
     * @param nome Parte do nome da cidade
     * @return Lista de cidades encontradas
     */
    public List<Cidade> buscarCidadesPorNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return cidadeDAO.findByNomeContaining(nome.trim());
    }
    
    /**
     * Lista cidades com seus armazéns
     * @return Lista de cidades com armazéns
     */
    public List<Cidade> listarCidadesComArmazens() {
        return cidadeDAO.findAllWithArmazens();
    }
    
    /**
     * Busca uma cidade com seus armazéns
     * @param id ID da cidade
     * @return Cidade com armazéns
     * @throws IllegalArgumentException Se a cidade não existir
     */
    public Cidade buscarCidadeComArmazens(Long id) {
        Optional<Cidade> cidade = cidadeDAO.findByIdWithArmazens(id);
        if (cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + id);
        }
        return cidade.get();
    }
    
    /**
     * Conta o número total de cidades
     * @return Número de cidades
     */
    public long contarCidades() {
        return cidadeDAO.count();
    }
    
    /**
     * Verifica se uma cidade existe
     * @param id ID da cidade
     * @return true se existe
     */
    public boolean cidadeExiste(Long id) {
        return cidadeDAO.existsById(id);
    }
    
    /**
     * Valida os dados de uma cidade
     * @param nome Nome da cidade
     * @param estado Estado da cidade
     * @param paisId ID do país
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    private void validarDadosCidade(String nome, String estado, Long paisId) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da cidade é obrigatório");
        }
        
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado da cidade é obrigatório");
        }
        
        if (paisId == null) {
            throw new IllegalArgumentException("País é obrigatório");
        }
        
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome da cidade deve ter pelo menos 2 caracteres");
        }
        
        if (estado.trim().length() < 2) {
            throw new IllegalArgumentException("Estado deve ter pelo menos 2 caracteres");
        }
    }
    
    /**
     * Lista todos os países disponíveis
     * @return Lista de países
     */
    public List<Pais> listarTodosPaises() {
        return paisDAO.findAll();
    }
}