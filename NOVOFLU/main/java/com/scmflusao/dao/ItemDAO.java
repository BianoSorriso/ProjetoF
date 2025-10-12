package com.scmflusao.dao;

import com.scmflusao.model.Item;
import com.scmflusao.model.SituacaoItem;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Interface DAO para operações com Item
 */
public interface ItemDAO {
    /**
     * Salva um novo item
     * @param item Item a ser salvo
     * @return Item salvo com ID gerado
     */
    Item save(Item item);
    
    /**
     * Atualiza um item existente
     * @param item Item a ser atualizado
     * @return Item atualizado
     */
    Item update(Item item);
    
    /**
     * Busca um item por ID
     * @param id ID do item
     * @return Optional contendo o item se encontrado
     */
    Optional<Item> findById(Long id);
    
    /**
     * Lista todos os itens
     * @return Lista de itens
     */
    List<Item> findAll();
    
    /**
     * Remove um item por ID
     * @param id ID do item a ser removido
     */
    void deleteById(Long id);
    
    /**
     * Verifica se um item existe por ID
     * @param id ID do item
     * @return true se existe, false caso contrário
     */
    boolean existsById(Long id);
    
    /**
     * Busca itens por nome
     * @param nome Nome do item
     * @return Lista de itens encontrados
     */
    List<Item> findByNome(String nome);
    
    /**
     * Busca itens que contenham o nome especificado
     * @param nome Parte do nome do item
     * @return Lista de itens encontrados
     */
    List<Item> findByNomeContaining(String nome);
    
    /**
     * Busca itens por situação
     * @param situacao Situação do item
     * @return Lista de itens encontrados
     */
    List<Item> findBySituacao(SituacaoItem situacao);

    /**
     * Busca itens associados a um produto pelo seu ID
     * @param produtoId ID do produto
     * @return Lista de itens vinculados ao produto
     */
    List<Item> findByProdutoId(Long produtoId);
    
    /**
     * Busca itens por cidade de origem
     * @param cidadeId ID da cidade
     * @return Lista de itens
     */
    List<Item> findByCidadeOrigemId(Long cidadeId);
    
    /**
     * Busca itens por nome do produto
     * @param nomeProduto Nome do produto
     * @return Lista de itens
     */
    List<Item> findByProdutoNome(String nomeProduto);
    
    /**
     * Busca itens por armazém atual
     * @param armazemId ID do armazém
     * @return Lista de itens encontrados
     */
    List<Item> findByArmazemAtualId(Long armazemId);
    
    /**
     * Busca itens com peso menor ou igual ao especificado
     * @param peso Peso máximo
     * @return Lista de itens encontrados
     */
    List<Item> findByPesoLessThanEqual(BigDecimal peso);
    
    /**
     * Busca itens com peso maior ou igual ao especificado
     * @param peso Peso mínimo
     * @return Lista de itens encontrados
     */
    List<Item> findByPesoGreaterThanEqual(BigDecimal peso);
    
    /**
     * Busca itens com peso entre os valores especificados
     * @param pesoMin Peso mínimo
     * @param pesoMax Peso máximo
     * @return Lista de itens encontrados
     */
    List<Item> findByPesoBetween(BigDecimal pesoMin, BigDecimal pesoMax);
    
    /**
     * Busca itens com quantidade maior que zero
     * @return Lista de itens disponíveis
     */
    List<Item> findByQuantidadeGreaterThan(Integer quantidade);
    
    /**
     * Busca itens com quantidade menor ou igual ao especificado
     * @param quantidade Quantidade máxima
     * @return Lista de itens encontrados
     */
    List<Item> findByQuantidadeLessThanEqual(Integer quantidade);
    
    /**
     * Busca itens com quantidade maior ou igual ao especificado
     * @param quantidade Quantidade mínima
     * @return Lista de itens encontrados
     */
    List<Item> findByQuantidadeGreaterThanEqual(Integer quantidade);
    
    /**
     * Busca itens com valor unitário menor ou igual ao especificado
     * @param valor Valor máximo
     * @return Lista de itens encontrados
     */
    List<Item> findByValorUnitarioLessThanEqual(BigDecimal valor);
    
    /**
     * Busca itens com valor unitário maior ou igual ao especificado
     * @param valor Valor mínimo
     * @return Lista de itens encontrados
     */
    List<Item> findByValorUnitarioGreaterThanEqual(BigDecimal valor);
    
    /**
     * Lista itens ordenados por nome
     * @return Lista de itens ordenados
     */
    List<Item> findAllOrderByNome();
    
    /**
     * Lista itens ordenados por peso (crescente)
     * @return Lista de itens ordenados
     */
    List<Item> findAllOrderByPesoAsc();
    
    /**
     * Lista itens ordenados por peso (decrescente)
     * @return Lista de itens ordenados
     */
    List<Item> findAllOrderByPesoDesc();
    
    /**
     * Lista itens ordenados por quantidade (crescente)
     * @return Lista de itens ordenados
     */
    List<Item> findAllOrderByQuantidadeAsc();
    
    /**
     * Lista itens ordenados por quantidade (decrescente)
     * @return Lista de itens ordenados
     */
    List<Item> findAllOrderByQuantidadeDesc();
    
    /**
     * Lista itens ordenados por valor unitário (crescente)
     * @return Lista de itens ordenados
     */
    List<Item> findAllOrderByValorUnitarioAsc();
    
    /**
     * Lista itens ordenados por valor unitário (decrescente)
     * @return Lista de itens ordenados
     */
    List<Item> findAllOrderByValorUnitarioDesc();
    
    /**
     * Conta o número total de itens
     * @return Número de itens
     */
    long count();
    
    /**
     * Conta itens por situação
     * @param situacao Situação do item
     * @return Número de itens na situação especificada
     */
    long countBySituacao(SituacaoItem situacao);
    
    /**
     * Soma a quantidade total de itens
     * @return Quantidade total de itens
     */
    long sumQuantidade();
    
    /**
     * Calcula o valor total do estoque de itens
     * @return Valor total do estoque
     */
    BigDecimal sumValorTotal();
}