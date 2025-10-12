package com.scmflusao.model;

/**
 * Enumeração que representa as possíveis situações de um item no sistema SCM Flusão
 */
public enum SituacaoItem {
    REGISTRADO("Registrado"),
    VERIFICADO("Verificado"),
    EM_ESTOQUE("Em Estoque"),
    EM_MANUTENCAO("Em Manutenção"),
    ALOCADO("Alocado"),
    RESERVADO("Reservado"),
    UTILIZADO("Utilizado");
    
    private final String descricao;
    
    SituacaoItem(String descricao) {
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