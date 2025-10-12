package com.scmflusao.dao;

import com.scmflusao.model.Produto;
import com.scmflusao.model.SituacaoProduto;
import com.scmflusao.model.Item;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface DAO para operações com Produto
 */
public interface ProdutoDAO {
    /**
     * Salva um novo produto
     * @param produto Produto a ser salvo
     * @return Produto salvo com ID gerado
     */
    Produto save(Produto produto);
    
    /**
     * Atualiza um produto existente
     * @param produto Produto a ser atualizado
     * @return Produto atualizado
     */
    Produto update(Produto produto);
    
    /**
     * Busca um produto por ID
     * @param id ID do produto
     * @return Optional contendo o produto se encontrado
     */
    Optional<Produto> findById(Long id);
    
    /**
     * Lista todos os produtos
     * @return Lista de produtos
     */
    List<Produto> findAll();
    
    /**
     * Remove um produto por ID
     * @param id ID do produto a ser removido
     */
    void deleteById(Long id);
    
    /**
     * Verifica se um produto existe por ID
     * @param id ID do produto
     * @return true se existe, false caso contrário
     */
    boolean existsById(Long id);
    
    /**
     * Busca produtos por nome
     * @param nome Nome do produto
     * @return Lista de produtos encontrados
     */
    List<Produto> findByNome(String nome);
    
    /**
     * Busca produtos que contenham o nome especificado
     * @param nome Parte do nome do produto
     * @return Lista de produtos encontrados
     */
    List<Produto> findByNomeContaining(String nome);
    
    /**
     * Busca produtos por situação
     * @param situacao Situação do produto
     * @return Lista de produtos encontrados
     */
    List<Produto> findBySituacao(SituacaoProduto situacao);
    
    /**
     * Busca produtos por cidade de fabricação
     * @param cidadeId ID da cidade de fabricação
     * @return Lista de produtos encontrados
     */
    List<Produto> findByCidadeFabricacaoId(Long cidadeId);
    
    /**
     * Busca produtos por armazém atual
     * @param armazemId ID do armazém
     * @return Lista de produtos encontrados
     */
    List<Produto> findByArmazemAtualId(Long armazemId);
    
    /**
     * Busca produtos com preço menor ou igual ao especificado
     * @param preco Preço máximo
     * @return Lista de produtos encontrados
     */
    List<Produto> findByPrecoLessThanEqual(BigDecimal preco);
    
    /**
     * Busca produtos com preço maior ou igual ao especificado
     * @param preco Preço mínimo
     * @return Lista de produtos encontrados
     */
    List<Produto> findByPrecoGreaterThanEqual(BigDecimal preco);
    
    /**
     * Busca produtos com preço entre os valores especificados
     * @param precoMin Preço mínimo
     * @param precoMax Preço máximo
     * @return Lista de produtos encontrados
     */
    List<Produto> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax);
    
    /**
     * Busca produtos com seus itens componentes
     * @return Lista de produtos com itens
     */
    List<Produto> findAllWithItens();
    
    /**
     * Busca um produto com seus itens componentes
     * @param id ID do produto
     * @return Optional contendo o produto com itens se encontrado
     */
    Optional<Produto> findByIdWithItens(Long id);

    /**
     * Atualiza a associação de itens de um produto
     * Remove vínculos antigos e vincula os itens informados ao produto
     * @param produtoId ID do produto
     * @param itens Lista de itens a serem associados
     */
    void atualizarItensDoProduto(Long produtoId, List<Item> itens);
    
    /**
     * Lista produtos ordenados por preço (crescente)
     * @return Lista de produtos ordenados
     */
    List<Produto> findAllOrderByPrecoAsc();
    
    /**
     * Lista produtos ordenados por preço (decrescente)
     * @return Lista de produtos ordenados
     */
    List<Produto> findAllOrderByPrecoDesc();
    
    /**
     * Lista produtos ordenados por nome
     * @return Lista de produtos ordenados
     */
    List<Produto> findAllOrderByNome();
    
    /**
     * Conta o número total de produtos
     * @return Número de produtos
     */
    long count();
    
    /**
     * Conta produtos por situação
     * @param situacao Situação do produto
     * @return Número de produtos na situação especificada
     */
    long countBySituacao(SituacaoProduto situacao);
}