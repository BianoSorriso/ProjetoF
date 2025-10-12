package com.scmflusao.model;

public enum SituacaoTransporte {
    PLANEJADO("Planejado"),
    EM_TRANSITO("Em trânsito"),
    ENTREGUE("Entregue"),
    CANCELADO("Cancelado");

    private final String descricao;

    SituacaoTransporte(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}