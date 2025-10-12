package com.scmflusao.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Classe que representa um País no sistema SCM Flusão
 */
public class Pais {
    private Long id;
    private String nome;
    private String codigo;
    private List<Cidade> cidades;
    
    // Construtores
    public Pais() {
        this.cidades = new ArrayList<>();
    }
    
    public Pais(String nome, String codigo) {
        this();
        this.nome = nome;
        this.codigo = codigo;
    }
    
    // Getters e Setters
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
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public List<Cidade> getCidades() {
        return cidades;
    }
    
    public void setCidades(List<Cidade> cidades) {
        this.cidades = cidades;
    }
    
    // Métodos de negócio
    public void adicionarCidade(Cidade cidade) {
        if (cidade != null && !this.cidades.contains(cidade)) {
            this.cidades.add(cidade);
            cidade.setPais(this);
        }
    }
    
    public void removerCidade(Cidade cidade) {
        if (cidade != null && this.cidades.contains(cidade)) {
            this.cidades.remove(cidade);
            cidade.setPais(null);
        }
    }
    
    @Override
    public String toString() {
        return nome;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pais pais = (Pais) obj;
        return id != null ? id.equals(pais.id) : pais.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}