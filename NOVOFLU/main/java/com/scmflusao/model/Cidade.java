package com.scmflusao.model;

import java.util.List;
import java.util.ArrayList;

public class Cidade {
    private Long id;
    private String nome;
    private String estado;
    private Pais pais;
    private boolean ehFabrica;
    private boolean ehOrigem;
    private List<Armazem> armazens;

    public Cidade() {
        this.armazens = new ArrayList<>();
    }
    
    public Cidade(String nome, String estado, boolean ehFabrica, boolean ehOrigem, Pais pais) {
        this.nome = nome;
        this.estado = estado;
        this.ehFabrica = ehFabrica;
        this.ehOrigem = ehOrigem;
        this.pais = pais;
        this.armazens = new ArrayList<>();
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }
    


    public boolean isEhFabrica() {
        return ehFabrica;
    }

    public void setEhFabrica(boolean ehFabrica) {
        this.ehFabrica = ehFabrica;
    }

    public boolean isEhOrigem() {
        return ehOrigem;
    }

    public void setEhOrigem(boolean ehOrigem) {
        this.ehOrigem = ehOrigem;
    }

    public List<Armazem> getArmazens() {
        return armazens;
    }

    public void setArmazens(List<Armazem> armazens) {
        this.armazens = armazens;
    }

    public void addArmazem(Armazem armazem) {
        if (armazem != null && !this.armazens.contains(armazem)) {
            this.armazens.add(armazem);
            armazem.setCidade(this);
        }
    }

    public void removeArmazem(Armazem armazem) {
        if (armazem != null && this.armazens.contains(armazem)) {
            this.armazens.remove(armazem);
            armazem.setCidade(null);
        }
    }
    
    @Override
    public String toString() {
        return nome + " - " + estado;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cidade cidade = (Cidade) obj;
        return id != null ? id.equals(cidade.id) : cidade.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}