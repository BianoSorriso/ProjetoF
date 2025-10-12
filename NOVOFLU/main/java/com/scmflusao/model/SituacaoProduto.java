package com.scmflusao.model;

/**
 * Enumeração que representa as possíveis situações de um produto no sistema SCM Flusão
 */
public enum SituacaoProduto {
    CRIADO("Criado"),
    VALIDADO("Validado"),
    APROVADO("Aprovado"),
    EM_PRODUCAO("Em Produção"),
    DISPONIVEL("Disponível"),
    ATIVO("Ativo"),
    DESCONTINUADO("Descontinuado");
    
    private final String descricao;
    
    SituacaoProduto(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}