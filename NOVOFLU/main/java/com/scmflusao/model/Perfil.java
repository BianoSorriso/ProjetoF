package com.scmflusao.model;

public enum Perfil {
    ADMIN("Administrador"),
    OPERADOR("Operador"),
    PARCEIRO("Parceiro de Transporte");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}