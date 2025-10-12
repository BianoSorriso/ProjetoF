package com.scmflusao.controller;

import com.scmflusao.dao.ItemDAO;
import com.scmflusao.dao.CidadeDAO;
import com.scmflusao.dao.ArmazemDAO;
import com.scmflusao.dao.impl.ItemDAOImpl;
import com.scmflusao.dao.impl.CidadeDAOImpl;
import com.scmflusao.dao.impl.ArmazemDAOImpl;
import com.scmflusao.model.Item;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.Armazem;
import com.scmflusao.model.SituacaoItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Controller para operações relacionadas a Item
 */
public class ItemController {
    
    private final ItemDAO itemDAO;
    private final CidadeDAO cidadeDAO;
    private final ArmazemDAO armazemDAO;
    
    public ItemController() {
        this.itemDAO = new ItemDAOImpl();
        this.cidadeDAO = new CidadeDAOImpl();
        this.armazemDAO = new ArmazemDAOImpl();
    }
    
    /**
     * Cria um novo item
     * @param nome Nome do item
     * @param descricao Descrição do item
     * @param peso Peso do item
     * @param quantidade Quantidade do item
     * @param valorUnitario Valor unitário do item
     * @param cidadeOrigemId ID da cidade de origem
     * @return Item criado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Item criarItem(String nome, String descricao, BigDecimal peso, Integer quantidade, 
                         BigDecimal valorUnitario, Long cidadeOrigemId, SituacaoItem situacao) {
        validarDadosItem(nome, descricao, peso, quantidade, valorUnitario, cidadeOrigemId);
        
        // Verifica se a cidade existe
        Optional<Cidade> cidade = cidadeDAO.findById(cidadeOrigemId);
        if (cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + cidadeOrigemId);
        }
        
        // Verifica se já existe um item com o mesmo nome
        List<Item> itensExistentes = itemDAO.findByNome(nome);
        if (!itensExistentes.isEmpty()) {
            throw new IllegalArgumentException("Já existe um item com o nome: " + nome);
        }
        
        Item item = new Item(nome, descricao, peso, cidade.get());
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        // Usa a situação escolhida; se não vier, mantém REGISTRADO como padrão
        item.setSituacao(situacao != null ? situacao : SituacaoItem.REGISTRADO);
        return itemDAO.save(item);
    }
    
    /**
     * Atualiza um item existente
     * @param id ID do item
     * @param nome Novo nome
     * @param descricao Nova descrição
     * @param peso Novo peso
     * @param quantidade Nova quantidade
     * @param valorUnitario Novo valor unitário
     * @param cidadeOrigemId ID da cidade de origem
     * @return Item atualizado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Item atualizarItem(Long id, String nome, String descricao, BigDecimal peso, 
                             Integer quantidade, BigDecimal valorUnitario, Long cidadeOrigemId, SituacaoItem situacao) {
        validarDadosItem(nome, descricao, peso, quantidade, valorUnitario, cidadeOrigemId);
        
        Optional<Item> itemExistente = itemDAO.findById(id);
        if (itemExistente.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + id);
        }
        
        // Verifica se a cidade existe
        Optional<Cidade> cidade = cidadeDAO.findById(cidadeOrigemId);
        if (cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + cidadeOrigemId);
        }
        
        Item item = itemExistente.get();
        
        // Verifica se o novo nome já existe em outro item
        List<Item> itensComMesmoNome = itemDAO.findByNome(nome);
        for (Item itemComMesmoNome : itensComMesmoNome) {
            if (!itemComMesmoNome.getId().equals(id)) {
                throw new IllegalArgumentException("Já existe outro item com o nome: " + nome);
            }
        }
        
        item.setNome(nome);
        item.setDescricao(descricao);
        item.setPeso(peso);
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        item.setCidadeOrigem(cidade.get());
        if (situacao != null) {
            item.setSituacao(situacao);
        }
        
        return itemDAO.update(item);
    }
    
    /**
     * Remove um item
     * @param id ID do item
     * @return true se removido com sucesso
     * @throws IllegalArgumentException Se o item não existir
     */
    public boolean removerItem(Long id) {
        if (!itemDAO.existsById(id)) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + id);
        }
        itemDAO.deleteById(id);
        return true;
    }
    
    /**
     * Busca um item por ID
     * @param id ID do item
     * @return Item encontrado
     * @throws IllegalArgumentException Se o item não existir
     */
    public Item buscarItemPorId(Long id) {
        Optional<Item> item = itemDAO.findById(id);
        if (item.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + id);
        }
        return item.get();
    }
    
    /**
     * Busca itens por nome
     * @param nome Nome do item
     * @return Lista de itens encontrados
     * @throws IllegalArgumentException Se o nome for inválido
     */
    public List<Item> buscarItensPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório para busca");
        }
        return itemDAO.findByNome(nome);
    }
    
    /**
     * Busca itens por cidade de origem
     * @param cidadeId ID da cidade
     * @return Lista de itens encontrados
     * @throws IllegalArgumentException Se o ID da cidade for inválido
     */
    public List<Item> buscarItensPorCidadeOrigem(Long cidadeId) {
        if (cidadeId == null) {
            throw new IllegalArgumentException("ID da cidade é obrigatório");
        }
        return itemDAO.findByCidadeOrigemId(cidadeId);
    }
    
    /**
     * Busca itens por situação
     * @param situacao Situação do item
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensPorSituacao(SituacaoItem situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("Situação é obrigatória");
        }
        return itemDAO.findBySituacao(situacao);
    }
    
    /**
     * Busca itens por nome do produto
     * @param nomeProduto Nome do produto
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensPorProduto(String nomeProduto) {
        if (nomeProduto == null || nomeProduto.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        return itemDAO.findByProdutoNome(nomeProduto);
    }
    
    /**
     * Busca itens com peso menor ou igual ao especificado
     * @param peso Peso máximo
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensComPesoMenorOuIgual(BigDecimal peso) {
        if (peso == null || peso.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Peso deve ser um valor não negativo");
        }
        return itemDAO.findByPesoLessThanEqual(peso);
    }
    
    /**
     * Busca itens com peso maior ou igual ao especificado
     * @param peso Peso mínimo
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensComPesoMaiorOuIgual(BigDecimal peso) {
        if (peso == null || peso.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Peso deve ser um valor não negativo");
        }
        return itemDAO.findByPesoGreaterThanEqual(peso);
    }
    
    /**
     * Busca itens com peso entre os valores especificados
     * @param pesoMin Peso mínimo
     * @param pesoMax Peso máximo
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensComPesoEntre(BigDecimal pesoMin, BigDecimal pesoMax) {
        if (pesoMin == null || pesoMax == null) {
            throw new IllegalArgumentException("Pesos mínimo e máximo são obrigatórios");
        }
        if (pesoMin.compareTo(pesoMax) > 0) {
            throw new IllegalArgumentException("Peso mínimo não pode ser maior que o peso máximo");
        }
        return itemDAO.findByPesoBetween(pesoMin, pesoMax);
    }
    
    /**
     * Busca itens com quantidade menor ou igual à especificada
     * @param quantidade Quantidade máxima
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensComQuantidadeMenorOuIgual(Integer quantidade) {
        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException("Quantidade deve ser um valor não negativo");
        }
        return itemDAO.findByQuantidadeLessThanEqual(quantidade);
    }
    
    /**
     * Busca itens com quantidade maior ou igual à especificada
     * @param quantidade Quantidade mínima
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensComQuantidadeMaiorOuIgual(Integer quantidade) {
        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException("Quantidade deve ser um valor não negativo");
        }
        return itemDAO.findByQuantidadeGreaterThanEqual(quantidade);
    }
    
    /**
     * Busca itens com quantidade mínima especificada
     * @param quantidade Quantidade mínima
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensComQuantidadeMinima(Integer quantidade) {
        if (quantidade == null || quantidade < 0) {
            throw new IllegalArgumentException("Quantidade deve ser um valor não negativo");
        }
        return itemDAO.findByQuantidadeGreaterThanEqual(quantidade);
    }
    
    /**
     * Busca itens com valor unitário menor ou igual ao especificado
     * @param valor Valor máximo
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensComValorMenorOuIgual(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor deve ser um valor não negativo");
        }
        return itemDAO.findByValorUnitarioLessThanEqual(valor);
    }
    
    /**
     * Busca itens com valor unitário maior ou igual ao especificado
     * @param valor Valor mínimo
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensComValorMaiorOuIgual(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor deve ser um valor não negativo");
        }
        return itemDAO.findByValorUnitarioGreaterThanEqual(valor);
    }
    
    /**
     * Lista todos os itens
     * @return Lista de todos os itens
     */
    public List<Item> listarTodosItens() {
        return itemDAO.findAll();
    }
    
    /**
     * Busca itens por nome contendo o texto especificado
     * @param nome Parte do nome do item
     * @return Lista de itens encontrados
     */
    public List<Item> buscarItensPorNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório para busca");
        }
        return itemDAO.findByNomeContaining(nome);
    }
    
    /**
     * Lista itens ordenados por peso (crescente)
     * @return Lista de itens ordenados
     */
    public List<Item> listarItensOrdenadosPorPeso() {
        return itemDAO.findAllOrderByPesoAsc();
    }
    
    /**
     * Lista itens ordenados por peso (decrescente)
     * @return Lista de itens ordenados
     */
    public List<Item> listarItensOrdenadosPorPesoDesc() {
        return itemDAO.findAllOrderByPesoDesc();
    }
    
    /**
     * Lista itens ordenados por quantidade (crescente)
     * @return Lista de itens ordenados
     */
    public List<Item> listarItensOrdenadosPorQuantidade() {
        return itemDAO.findAllOrderByQuantidadeAsc();
    }
    
    /**
     * Lista itens ordenados por quantidade (decrescente)
     * @return Lista de itens ordenados
     */
    public List<Item> listarItensOrdenadosPorQuantidadeDesc() {
        return itemDAO.findAllOrderByQuantidadeDesc();
    }
    
    /**
     * Lista itens ordenados por valor unitário (crescente)
     * @return Lista de itens ordenados
     */
    public List<Item> listarItensOrdenadosPorValor() {
        return itemDAO.findAllOrderByValorUnitarioAsc();
    }
    
    /**
     * Lista itens ordenados por valor unitário (decrescente)
     * @return Lista de itens ordenados
     */
    public List<Item> listarItensOrdenadosPorValorDesc() {
        return itemDAO.findAllOrderByValorUnitarioDesc();
    }
    
    /**
     * Lista itens ordenados por nome
     * @return Lista de itens ordenados
     */
    public List<Item> listarItensOrdenadosPorNome() {
        return itemDAO.findAllOrderByNome();
    }
    
    /**
     * Transiciona a situação de um item
     * @param id ID do item
     * @param novaSituacao Nova situação
     * @return true se a transição foi bem-sucedida, false caso contrário
     */
    public boolean transicionarSituacao(Long id, SituacaoItem novaSituacao) {
        try {
            atualizarSituacaoItem(id, novaSituacao);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Atualiza a situação de um item
     * @param id ID do item
     * @param novaSituacao Nova situação
     * @return Item atualizado
     * @throws IllegalArgumentException Se o item não existir ou a situação for inválida
     */
    public Item atualizarSituacaoItem(Long id, SituacaoItem novaSituacao) {
        if (novaSituacao == null) {
            throw new IllegalArgumentException("Nova situação é obrigatória");
        }
        
        Optional<Item> itemExistente = itemDAO.findById(id);
        if (itemExistente.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + id);
        }
        
        Item item = itemExistente.get();
        
        // Validação de transição de situação (pode ser expandida conforme regras de negócio)
        if (!podeTransicionarSituacao(item.getSituacao(), novaSituacao)) {
            throw new IllegalArgumentException("Transição de situação inválida de " + 
                item.getSituacao() + " para " + novaSituacao);
        }
        
        item.setSituacao(novaSituacao);
        return itemDAO.update(item);
    }
    
    /**
     * Transfere um item para um armazém
     * @param itemId ID do item
     * @param armazemId ID do armazém
     * @return Item atualizado
     * @throws IllegalArgumentException Se o item ou armazém não existirem
     */
    public Item transferirItemParaArmazem(Long itemId, Long armazemId) {
        Optional<Item> itemExistente = itemDAO.findById(itemId);
        if (itemExistente.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + itemId);
        }
        
        Optional<Armazem> armazemExistente = armazemDAO.findById(armazemId);
        if (armazemExistente.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + armazemId);
        }
        
        Item item = itemExistente.get();
        item.setArmazemAtual(armazemExistente.get());
        
        return itemDAO.update(item);
    }
    
    /**
     * Atualiza a quantidade de um item
     * @param id ID do item
     * @param novaQuantidade Nova quantidade
     * @return Item atualizado
     * @throws IllegalArgumentException Se o item não existir ou a quantidade for inválida
     */
    public Item atualizarQuantidadeItem(Long id, Integer novaQuantidade) {
        if (novaQuantidade == null || novaQuantidade < 0) {
            throw new IllegalArgumentException("Quantidade deve ser um valor não negativo");
        }
        
        Optional<Item> itemExistente = itemDAO.findById(id);
        if (itemExistente.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + id);
        }
        
        Item item = itemExistente.get();
        item.setQuantidade(novaQuantidade);
        
        return itemDAO.update(item);
    }
    
    /**
     * Conta o número total de itens
     * @return Número de itens
     */
    public long contarItens() {
        return itemDAO.count();
    }
    
    /**
     * Conta itens por situação
     * @param situacao Situação do item
     * @return Número de itens na situação especificada
     */
    public long contarItensPorSituacao(SituacaoItem situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("Situação é obrigatória");
        }
        return itemDAO.countBySituacao(situacao);
    }
    
    /**
     * Verifica se um item existe
     * @param id ID do item
     * @return true se existe, false caso contrário
     */
    public boolean itemExiste(Long id) {
        if (id == null) {
            return false;
        }
        return itemDAO.existsById(id);
    }
    
    /**
     * Calcula o valor total de um item (quantidade * valor unitário)
     * @param id ID do item
     * @return Valor total do item
     * @throws IllegalArgumentException Se o item não existir
     */
    public BigDecimal calcularValorTotalItem(Long id) {
        Optional<Item> itemExistente = itemDAO.findById(id);
        if (itemExistente.isEmpty()) {
            throw new IllegalArgumentException("Item não encontrado com ID: " + id);
        }
        
        Item item = itemExistente.get();
        return item.calcularValorTotal();
    }
    
    /**
     * Valida os dados básicos de um item
     * @param nome Nome do item
     * @param descricao Descrição do item
     * @param peso Peso do item
     * @param quantidade Quantidade do item
     * @param valorUnitario Valor unitário do item
     * @param cidadeOrigemId ID da cidade de origem
     * @throws IllegalArgumentException Se algum dado for inválido
     */
    private void validarDadosItem(String nome, String descricao, BigDecimal peso, 
                                 Integer quantidade, BigDecimal valorUnitario, Long cidadeOrigemId) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do item é obrigatório");
        }
        
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do item é obrigatória");
        }
        
        if (peso == null || peso.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Peso deve ser um valor positivo");
        }
        
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser um valor positivo");
        }
        
        if (valorUnitario == null || valorUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor unitário deve ser um valor positivo");
        }
        
        if (cidadeOrigemId == null) {
            throw new IllegalArgumentException("Cidade de origem é obrigatória");
        }
        
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome do item deve ter pelo menos 2 caracteres");
        }
        
        if (descricao.trim().length() < 5) {
            throw new IllegalArgumentException("Descrição deve ter pelo menos 5 caracteres");
        }
        
        if (peso.scale() > 3) {
            throw new IllegalArgumentException("Peso não pode ter mais de 3 casas decimais");
        }
        
        if (valorUnitario.scale() > 2) {
            throw new IllegalArgumentException("Valor unitário não pode ter mais de 2 casas decimais");
        }
    }
    
    /**
     * Verifica se é possível transicionar de uma situação para outra
     * @param situacaoAtual Situação atual
     * @param novaSituacao Nova situação
     * @return true se a transição é válida
     */
    private boolean podeTransicionarSituacao(SituacaoItem situacaoAtual, SituacaoItem novaSituacao) {
        // Regras básicas de transição - podem ser expandidas conforme necessário
        switch (situacaoAtual) {
            case REGISTRADO:
                return novaSituacao == SituacaoItem.VERIFICADO;
            case VERIFICADO:
                return novaSituacao == SituacaoItem.EM_ESTOQUE || novaSituacao == SituacaoItem.REGISTRADO;
            case EM_ESTOQUE:
                return novaSituacao == SituacaoItem.ALOCADO || novaSituacao == SituacaoItem.EM_MANUTENCAO ||
                       novaSituacao == SituacaoItem.VERIFICADO;
            case EM_MANUTENCAO:
                return novaSituacao == SituacaoItem.EM_ESTOQUE || novaSituacao == SituacaoItem.VERIFICADO;
            case ALOCADO:
                return novaSituacao == SituacaoItem.UTILIZADO || novaSituacao == SituacaoItem.EM_ESTOQUE ||
                       novaSituacao == SituacaoItem.EM_MANUTENCAO;
            case UTILIZADO:
                return novaSituacao == SituacaoItem.EM_MANUTENCAO || novaSituacao == SituacaoItem.EM_ESTOQUE;
            default:
                return false;
        }
    }
}