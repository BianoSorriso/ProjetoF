package com.scmflusao.model;

import java.math.BigDecimal;

public class Armazem {
    private Long id;
    private String nome;
    private String endereco;
    private BigDecimal capacidade;
    private BigDecimal ocupacao;
    private boolean ativo;
    private Cidade cidade;

    public Armazem() {}
    
    public Armazem(String nome, String endereco, BigDecimal capacidadeTotal, 
                  BigDecimal capacidadeDisponivel, Cidade cidade) {
        this.nome = nome;
        this.endereco = endereco;
        this.capacidade = capacidadeTotal;
        this.ocupacao = capacidadeTotal.subtract(capacidadeDisponivel);
        this.ativo = true;
        this.cidade = cidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public BigDecimal getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(BigDecimal capacidade) {
        this.capacidade = capacidade;
    }
    
    public BigDecimal getCapacidadeTotal() {
        return capacidade;
    }

    public void setCapacidadeTotal(BigDecimal capacidadeTotal) {
        this.capacidade = capacidadeTotal;
    }

    public BigDecimal getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(BigDecimal ocupacao) {
        this.ocupacao = ocupacao;
    }
    
    public BigDecimal getCapacidadeDisponivel() {
        return capacidade.subtract(ocupacao);
    }

    public void setCapacidadeDisponivel(BigDecimal capacidadeDisponivel) {
        this.ocupacao = capacidade.subtract(capacidadeDisponivel);
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public BigDecimal getEspacoDisponivel() {
        return capacidade.subtract(ocupacao);
    }

    public boolean temEspacoDisponivel(BigDecimal quantidade) {
        return getEspacoDisponivel().compareTo(quantidade) >= 0;
    }
}