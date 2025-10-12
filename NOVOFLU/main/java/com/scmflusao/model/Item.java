package com.scmflusao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Classe que representa um item no sistema SCM Flusão
 */
public class Item {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal peso;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private SituacaoItem situacao;
    private Cidade cidadeOrigem;
    private Armazem armazemAtual;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataAtualizacao;

    public Item() {
        this.situacao = SituacaoItem.REGISTRADO;
        this.quantidade = 1;
        this.dataEntrada = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public Item(String nome, String descricao, BigDecimal peso, Cidade cidadeOrigem) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.peso = peso;
        this.cidadeOrigem = cidadeOrigem;
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
        this.dataAtualizacao = LocalDateTime.now();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public SituacaoItem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoItem situacao) {
        this.situacao = situacao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Cidade getCidadeOrigem() {
        return cidadeOrigem;
    }

    public void setCidadeOrigem(Cidade cidadeOrigem) {
        this.cidadeOrigem = cidadeOrigem;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Armazem getArmazemAtual() {
        return armazemAtual;
    }

    public void setArmazemAtual(Armazem armazemAtual) {
        this.armazemAtual = armazemAtual;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public void atualizarSituacao(SituacaoItem novaSituacao) {
        this.situacao = novaSituacao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void transferirParaArmazem(Armazem novoArmazem) {
        if (this.armazemAtual != null) {
            // Remove do armazém atual (implementação depende da lógica do Armazem)
        }
        this.armazemAtual = novoArmazem;
        this.dataAtualizacao = LocalDateTime.now();
        // Adiciona ao novo armazém (implementação depende da lógica do Armazem)
    }

    public BigDecimal calcularValorTotal() {
        if (valorUnitario != null && quantidade != null) {
            return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
        return BigDecimal.ZERO;
    }

    // Métodos adicionais requeridos pelo sistema
    public BigDecimal getPesoTotal() {
        if (peso != null && quantidade != null) {
            return peso.multiply(BigDecimal.valueOf(quantidade));
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPesoUnitario() {
        return peso;
    }

    public BigDecimal getValorTotal() {
        return calcularValorTotal();
    }

    public Produto getProduto() {
        // Este método retorna null por enquanto, pois Item não tem referência direta a Produto
        // A implementação depende da arquitetura específica do sistema
        return null;
    }

    @Override
    public String toString() {
        return nome + " (" + situacao + ") - Qtd: " + quantidade;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return id != null && id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}