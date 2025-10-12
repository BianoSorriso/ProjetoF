package com.scmflusao.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe que representa um produto no sistema SCM Flusão
 */
public class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private SituacaoProduto situacao;
    private Cidade cidadeFabricacao;
    private Armazem armazemAtual;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private List<Item> itensComponentes;

    public Produto() {
        this.situacao = SituacaoProduto.CRIADO;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.itensComponentes = new ArrayList<>();
    }
    
    public Produto(String nome, String descricao, BigDecimal preco, Cidade cidadeFabricacao) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.cidadeFabricacao = cidadeFabricacao;
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

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public SituacaoProduto getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProduto situacao) {
        this.situacao = situacao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Cidade getCidadeFabricacao() {
        return cidadeFabricacao;
    }

    public void setCidadeFabricacao(Cidade cidadeFabricacao) {
        this.cidadeFabricacao = cidadeFabricacao;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Armazem getArmazemAtual() {
        return armazemAtual;
    }

    public void setArmazemAtual(Armazem armazemAtual) {
        this.armazemAtual = armazemAtual;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public List<Item> getItensComponentes() {
        return new ArrayList<>(itensComponentes);
    }

    public void setItensComponentes(List<Item> itensComponentes) {
        this.itensComponentes = itensComponentes != null ? new ArrayList<>(itensComponentes) : new ArrayList<>();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void adicionarItem(Item item) {
        if (item != null) {
            this.itensComponentes.add(item); // permite duplicatas
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public void removerItem(Item item) {
        if (item != null && this.itensComponentes.contains(item)) {
            this.itensComponentes.remove(item);
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public void atualizarSituacao(SituacaoProduto novaSituacao) {
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

    // Métodos adicionais requeridos pelo sistema
    public BigDecimal getPesoUnitario() {
        // Calcula o peso unitário baseado nos itens componentes
        if (itensComponentes != null && !itensComponentes.isEmpty()) {
            BigDecimal pesoTotal = BigDecimal.ZERO;
            for (Item item : itensComponentes) {
                if (item.getPeso() != null) {
                    // peso total considera a quantidade do item
                    BigDecimal qtd = BigDecimal.valueOf(item.getQuantidade() != null ? item.getQuantidade() : 1);
                    pesoTotal = pesoTotal.add(item.getPeso().multiply(qtd));
                }
            }
            return pesoTotal;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPrecoUnitario() {
        return preco;
    }

    /**
     * Calcula o custo total baseado na soma dos valores unitários dos itens componentes
     * @return Custo total dos itens componentes
     */
    public BigDecimal getCustoTotal() {
        if (itensComponentes != null && !itensComponentes.isEmpty()) {
            BigDecimal custoTotal = BigDecimal.ZERO;
            for (Item item : itensComponentes) {
                if (item.getValorUnitario() != null) {
                    BigDecimal qtd = BigDecimal.valueOf(item.getQuantidade() != null ? item.getQuantidade() : 1);
                    custoTotal = custoTotal.add(item.getValorUnitario().multiply(qtd));
                }
            }
            return custoTotal;
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcula a margem de lucro (preço de venda - custo total)
     * @return Margem de lucro
     */
    public BigDecimal getMargemLucro() {
        if (preco != null) {
            return preco.subtract(getCustoTotal());
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calcula o percentual de margem de lucro
     * @return Percentual de margem (margem / custo * 100)
     */
    public BigDecimal getPercentualMargem() {
        BigDecimal custoTotal = getCustoTotal();
        if (custoTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margem = getMargemLucro();
            return margem.divide(custoTotal, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"));
        }
        return BigDecimal.ZERO;
    }

    public Cidade getCidadeOrigem() {
        return cidadeFabricacao;
    }

    @Override
    public String toString() {
        return nome + " (" + situacao + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Produto produto = (Produto) obj;
        return id != null && id.equals(produto.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}