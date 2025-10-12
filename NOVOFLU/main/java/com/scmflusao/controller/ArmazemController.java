package com.scmflusao.controller;

import com.scmflusao.dao.ArmazemDAO;
import com.scmflusao.dao.CidadeDAO;
import com.scmflusao.dao.ProdutoDAO;
import com.scmflusao.dao.ItemDAO;
import com.scmflusao.dao.impl.ArmazemDAOImpl;
import com.scmflusao.dao.impl.CidadeDAOImpl;
import com.scmflusao.dao.impl.ProdutoDAOImpl;
import com.scmflusao.dao.impl.ItemDAOImpl;
import com.scmflusao.model.Armazem;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.Produto;
import com.scmflusao.model.Item;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller para operações relacionadas a Armazém
 */
public class ArmazemController {
    
    private final ArmazemDAO armazemDAO;
    private final CidadeDAO cidadeDAO;
    private final ProdutoDAO produtoDAO;
    private final ItemDAO itemDAO;
    
    public ArmazemController() {
        this.armazemDAO = new ArmazemDAOImpl();
        this.cidadeDAO = new CidadeDAOImpl();
        this.produtoDAO = new ProdutoDAOImpl();
        this.itemDAO = new ItemDAOImpl();
    }
    
    /**
     * Cria um novo armazém
     * @param nome Nome do armazém
     * @param endereco Endereço do armazém
     * @param capacidadeTotal Capacidade total
     * @param capacidadeDisponivel Capacidade disponível
     * @param cidadeId ID da cidade
     * @return Armazém criado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Armazem criarArmazem(String nome, String endereco, BigDecimal capacidadeTotal, 
                               BigDecimal capacidadeDisponivel, Long cidadeId) {
        validarDadosArmazem(nome, endereco, capacidadeTotal, capacidadeDisponivel, cidadeId);
        
        // Verifica se a cidade existe
        Optional<Cidade> cidade = cidadeDAO.findById(cidadeId);
        if (cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + cidadeId);
        }
        
        // Verifica se já existe um armazém com o mesmo nome na mesma cidade
        List<Armazem> armazensExistentes = armazemDAO.findByNome(nome);
        for (Armazem armazemExistente : armazensExistentes) {
            if (armazemExistente.getCidade().getId().equals(cidadeId)) {
                throw new IllegalArgumentException("Já existe um armazém com o nome " + nome + " nesta cidade");
            }
        }
        
        Armazem armazem = new Armazem(nome, endereco, capacidadeTotal, capacidadeDisponivel, cidade.get());
        return armazemDAO.save(armazem);
    }
    
    /**
     * Atualiza um armazém existente
     * @param id ID do armazém
     * @param nome Novo nome
     * @param endereco Novo endereço
     * @param capacidadeTotal Nova capacidade total
     * @param capacidadeDisponivel Nova capacidade disponível
     * @param cidadeId ID da cidade
     * @return Armazém atualizado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Armazem atualizarArmazem(Long id, String nome, String endereco, BigDecimal capacidadeTotal, 
                                   BigDecimal capacidadeDisponivel, Long cidadeId) {
        validarDadosArmazem(nome, endereco, capacidadeTotal, capacidadeDisponivel, cidadeId);
        
        Optional<Armazem> armazemExistente = armazemDAO.findById(id);
        if (armazemExistente.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + id);
        }
        
        // Verifica se a cidade existe
        Optional<Cidade> cidade = cidadeDAO.findById(cidadeId);
        if (cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + cidadeId);
        }
        
        Armazem armazem = armazemExistente.get();
        
        // Verifica se o novo nome já existe em outro armazém na mesma cidade
        List<Armazem> armazensComMesmoNome = armazemDAO.findByNome(nome);
        for (Armazem armazemComMesmoNome : armazensComMesmoNome) {
            if (!armazemComMesmoNome.getId().equals(id) && armazemComMesmoNome.getCidade().getId().equals(cidadeId)) {
                throw new IllegalArgumentException("Já existe outro armazém com o nome " + nome + " nesta cidade");
            }
        }
        
        armazem.setNome(nome);
        armazem.setEndereco(endereco);
        armazem.setCapacidadeTotal(capacidadeTotal);
        armazem.setCapacidadeDisponivel(capacidadeDisponivel);
        armazem.setCidade(cidade.get());
        
        return armazemDAO.update(armazem);
    }
    
    /**
     * Remove um armazém
     * @param id ID do armazém
     * @return true se removido com sucesso
     * @throws IllegalArgumentException Se o armazém não existir
     */
    public boolean removerArmazem(Long id) {
        if (!armazemDAO.existsById(id)) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + id);
        }
        
        armazemDAO.deleteById(id);
        return true;
    }
    
    /**
     * Busca um armazém por ID
     * @param id ID do armazém
     * @return Armazém encontrado
     * @throws IllegalArgumentException Se o armazém não existir
     */
    public Armazem buscarArmazemPorId(Long id) {
        Optional<Armazem> armazem = armazemDAO.findById(id);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + id);
        }
        return armazem.get();
    }
    
    /**
     * Busca armazéns por nome
     * @param nome Nome do armazém
     * @return Lista de armazéns encontrados
     */
    public List<Armazem> buscarArmazensPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return armazemDAO.findByNome(nome.trim());
    }
    
    /**
     * Busca armazéns por cidade
     * @param cidadeId ID da cidade
     * @return Lista de armazéns encontrados
     */
    public List<Armazem> buscarArmazensPorCidade(Long cidadeId) {
        if (!cidadeDAO.existsById(cidadeId)) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + cidadeId);
        }
        return armazemDAO.findByCidadeId(cidadeId);
    }
    
    /**
     * Busca armazéns com capacidade disponível maior que o valor especificado
     * @param capacidade Capacidade mínima
     * @return Lista de armazéns encontrados
     */
    public List<Armazem> buscarArmazensComCapacidadeDisponivel(BigDecimal capacidade) {
        if (capacidade == null || capacidade.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Capacidade deve ser um valor positivo");
        }
        return armazemDAO.findByCapacidadeDisponivelGreaterThan(capacidade);
    }
    
    /**
     * Busca armazéns com capacidade entre os valores especificados
     * @param capacidadeMin Capacidade mínima
     * @param capacidadeMax Capacidade máxima
     * @return Lista de armazéns encontrados
     */
    public List<Armazem> buscarArmazensComCapacidadeEntre(BigDecimal capacidadeMin, BigDecimal capacidadeMax) {
        if (capacidadeMin == null || capacidadeMax == null) {
            throw new IllegalArgumentException("Capacidades mínima e máxima são obrigatórias");
        }
        
        if (capacidadeMin.compareTo(BigDecimal.ZERO) < 0 || capacidadeMax.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Capacidades devem ser valores positivos");
        }
        
        if (capacidadeMin.compareTo(capacidadeMax) > 0) {
            throw new IllegalArgumentException("Capacidade mínima não pode ser maior que a máxima");
        }
        
        return armazemDAO.findByCapacidadeBetween(capacidadeMin, capacidadeMax);
    }
    
    /**
     * Lista todos os armazéns
     * @return Lista de armazéns
     */
    public List<Armazem> listarTodosArmazens() {
        return armazemDAO.findAll();
    }
    
    /**
     * Busca armazéns que contenham o nome especificado
     * @param nome Parte do nome do armazém
     * @return Lista de armazéns encontrados
     */
    public List<Armazem> buscarArmazensPorNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        return armazemDAO.findByNomeContaining(nome.trim());
    }
    
    /**
     * Lista armazéns ordenados por capacidade disponível (decrescente)
     * @return Lista de armazéns ordenados
     */
    public List<Armazem> listarArmazensOrdenadosPorCapacidade() {
        return armazemDAO.findAllOrderByCapacidadeDisponivelDesc();
    }
    
    /**
     * Lista armazéns com seus itens
     * @return Lista de armazéns com itens
     */
    public List<Armazem> listarArmazensComItens() {
        return armazemDAO.findAllWithItens();
    }
    
    /**
     * Busca um armazém com seus itens
     * @param id ID do armazém
     * @return Armazém com itens
     * @throws IllegalArgumentException Se o armazém não existir
     */
    public Armazem buscarArmazemComItens(Long id) {
        Optional<Armazem> armazem = armazemDAO.findByIdWithItens(id);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + id);
        }
        return armazem.get();
    }
    
    /**
     * Lista armazéns com seus produtos
     * @return Lista de armazéns com produtos
     */
    public List<Armazem> listarArmazensComProdutos() {
        return armazemDAO.findAllWithProdutos();
    }
    
    /**
     * Busca um armazém com seus produtos
     * @param id ID do armazém
     * @return Armazém com produtos
     * @throws IllegalArgumentException Se o armazém não existir
     */
    public Armazem buscarArmazemComProdutos(Long id) {
        Optional<Armazem> armazem = armazemDAO.findByIdWithProdutos(id);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + id);
        }
        return armazem.get();
    }
    
    /**
     * Atualiza a capacidade disponível de um armazém
     * @param id ID do armazém
     * @param novaCapacidadeDisponivel Nova capacidade disponível
     * @return Armazém atualizado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Armazem atualizarCapacidadeDisponivel(Long id, BigDecimal novaCapacidadeDisponivel) {
        if (novaCapacidadeDisponivel == null || novaCapacidadeDisponivel.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Capacidade disponível deve ser um valor positivo");
        }
        
        Optional<Armazem> armazemExistente = armazemDAO.findById(id);
        if (armazemExistente.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + id);
        }
        
        Armazem armazem = armazemExistente.get();
        
        if (novaCapacidadeDisponivel.compareTo(armazem.getCapacidadeTotal()) > 0) {
            throw new IllegalArgumentException("Capacidade disponível não pode ser maior que a capacidade total");
        }
        
        armazem.setCapacidadeDisponivel(novaCapacidadeDisponivel);
        return armazemDAO.update(armazem);
    }
    
    /**
     * Conta o número total de armazéns
     * @return Número de armazéns
     */
    public long contarArmazens() {
        return armazemDAO.count();
    }
    
    /**
     * Verifica se um armazém existe
     * @param id ID do armazém
     * @return true se existe
     */
    public boolean armazemExiste(Long id) {
        return armazemDAO.existsById(id);
    }
    
    /**
     * Adiciona um produto ao armazém
     * @param armazemId ID do armazém
     * @param produtoId ID do produto
     * @param quantidade Quantidade do produto
     * @return true se adicionado com sucesso
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public boolean adicionarProdutoAoArmazem(Long armazemId, Long produtoId, Integer quantidade) {
        if (armazemId == null || produtoId == null || quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Dados inválidos para adicionar produto ao armazém");
        }
        
        Optional<Armazem> armazem = armazemDAO.findById(armazemId);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + armazemId);
        }
        
        Optional<Produto> produto = produtoDAO.findById(produtoId);
        if (produto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + produtoId);
        }
        
        // Verifica se há capacidade suficiente
        BigDecimal pesoTotal = produto.get().getPesoUnitario().multiply(new BigDecimal(quantidade));
        if (armazem.get().getCapacidadeDisponivel().compareTo(pesoTotal) < 0) {
            throw new IllegalArgumentException("Capacidade insuficiente no armazém");
        }
        
        // Atualiza a capacidade disponível
        BigDecimal novaCapacidade = armazem.get().getCapacidadeDisponivel().subtract(pesoTotal);
        atualizarCapacidadeDisponivel(armazemId, novaCapacidade);
        
        return true;
    }
    
    /**
     * Remove um produto do armazém
     * @param armazemId ID do armazém
     * @param produtoId ID do produto
     * @param quantidade Quantidade do produto a remover
     * @return true se removido com sucesso
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public boolean removerProdutoDoArmazem(Long armazemId, Long produtoId, Integer quantidade) {
        if (armazemId == null || produtoId == null || quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Dados inválidos para remover produto do armazém");
        }
        
        Optional<Armazem> armazem = armazemDAO.findById(armazemId);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + armazemId);
        }
        
        Optional<Produto> produto = produtoDAO.findById(produtoId);
        if (produto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + produtoId);
        }
        
        // Libera a capacidade
        BigDecimal pesoTotal = produto.get().getPesoUnitario().multiply(new BigDecimal(quantidade));
        BigDecimal novaCapacidade = armazem.get().getCapacidadeDisponivel().add(pesoTotal);
        
        // Verifica se não excede a capacidade total
        if (novaCapacidade.compareTo(armazem.get().getCapacidadeTotal()) > 0) {
            novaCapacidade = armazem.get().getCapacidadeTotal();
        }
        
        atualizarCapacidadeDisponivel(armazemId, novaCapacidade);
        
        return true;
    }
    
    /**
     * Adiciona um item ao armazém
     * @param armazemId ID do armazém
     * @param itemId ID do item
     * @return true se adicionado com sucesso
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public boolean adicionarItemAoArmazem(Long armazemId, Long itemId) {
        if (armazemId == null || itemId == null) {
            throw new IllegalArgumentException("Dados inválidos para adicionar item ao armazém");
        }
        
        Optional<Armazem> armazem = armazemDAO.findById(armazemId);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + armazemId);
        }
        
        Optional<Item> item = itemDAO.findById(itemId);
        if (item.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + itemId);
        }
        
        // Verifica se há capacidade suficiente
        if (armazem.get().getCapacidadeDisponivel().compareTo(item.get().getPesoTotal()) < 0) {
            throw new IllegalArgumentException("Capacidade insuficiente no armazém");
        }
        
        // Atualiza a capacidade disponível
        BigDecimal novaCapacidade = armazem.get().getCapacidadeDisponivel().subtract(item.get().getPesoTotal());
        atualizarCapacidadeDisponivel(armazemId, novaCapacidade);
        
        return true;
    }
    
    /**
     * Remove um item do armazém
     * @param armazemId ID do armazém
     * @param itemId ID do item
     * @return true se removido com sucesso
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public boolean removerItemDoArmazem(Long armazemId, Long itemId) {
        if (armazemId == null || itemId == null) {
            throw new IllegalArgumentException("Dados inválidos para remover item do armazém");
        }
        
        Optional<Armazem> armazem = armazemDAO.findById(armazemId);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + armazemId);
        }
        
        Optional<Item> item = itemDAO.findById(itemId);
        if (item.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + itemId);
        }
        
        // Libera a capacidade
        BigDecimal novaCapacidade = armazem.get().getCapacidadeDisponivel().add(item.get().getPesoTotal());
        
        // Verifica se não excede a capacidade total
        if (novaCapacidade.compareTo(armazem.get().getCapacidadeTotal()) > 0) {
            novaCapacidade = armazem.get().getCapacidadeTotal();
        }
        
        atualizarCapacidadeDisponivel(armazemId, novaCapacidade);
        
        return true;
    }
    
    /**
     * Calcula o peso total ocupado no armazém
     * @param armazemId ID do armazém
     * @return Peso total ocupado
     * @throws IllegalArgumentException Se o armazém não existir
     */
    public BigDecimal calcularPesoOcupado(Long armazemId) {
        Optional<Armazem> armazem = armazemDAO.findById(armazemId);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + armazemId);
        }
        
        return armazem.get().getCapacidadeTotal().subtract(armazem.get().getCapacidadeDisponivel());
    }
    
    /**
     * Calcula a porcentagem de ocupação do armazém
     * @param armazemId ID do armazém
     * @return Porcentagem de ocupação (0-100)
     * @throws IllegalArgumentException Se o armazém não existir
     */
    public BigDecimal calcularPorcentagemOcupacao(Long armazemId) {
        Optional<Armazem> armazem = armazemDAO.findById(armazemId);
        if (armazem.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + armazemId);
        }
        
        BigDecimal pesoOcupado = calcularPesoOcupado(armazemId);
        if (armazem.get().getCapacidadeTotal().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return pesoOcupado.divide(armazem.get().getCapacidadeTotal(), 4, BigDecimal.ROUND_HALF_UP)
                         .multiply(new BigDecimal("100"));
    }
    
    /**
     * Valida os dados de um armazém
     * @param nome Nome do armazém
     * @param endereco Endereço do armazém
     * @param capacidadeTotal Capacidade total
     * @param capacidadeDisponivel Capacidade disponível
     * @param cidadeId ID da cidade
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    private void validarDadosArmazem(String nome, String endereco, BigDecimal capacidadeTotal, 
                                    BigDecimal capacidadeDisponivel, Long cidadeId) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do armazém é obrigatório");
        }
        
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço do armazém é obrigatório");
        }
        
        if (capacidadeTotal == null || capacidadeTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Capacidade total deve ser um valor positivo");
        }
        
        if (capacidadeDisponivel == null || capacidadeDisponivel.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Capacidade disponível deve ser um valor não negativo");
        }
        
        if (capacidadeDisponivel.compareTo(capacidadeTotal) > 0) {
            throw new IllegalArgumentException("Capacidade disponível não pode ser maior que a capacidade total");
        }
        
        if (cidadeId == null) {
            throw new IllegalArgumentException("Cidade é obrigatória");
        }
        
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome do armazém deve ter pelo menos 2 caracteres");
        }
        
        if (endereco.trim().length() < 5) {
            throw new IllegalArgumentException("Endereço deve ter pelo menos 5 caracteres");
        }
    }
}