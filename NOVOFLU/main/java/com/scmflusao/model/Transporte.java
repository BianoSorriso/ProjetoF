package com.scmflusao.model;

import java.time.LocalDateTime;

public class Transporte {
    private Long id;
    private EmpresaParceira empresaParceira;
    private Agente agente;
    private Cidade origemCidade;
    private Cidade destinoCidade;
    private Armazem origemArmazem;
    private Armazem destinoArmazem;
    private Produto produto;
    private Item item;
    private SituacaoTransporte situacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EmpresaParceira getEmpresaParceira() { return empresaParceira; }
    public void setEmpresaParceira(EmpresaParceira empresaParceira) { this.empresaParceira = empresaParceira; }

    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }

    public Cidade getOrigemCidade() { return origemCidade; }
    public void setOrigemCidade(Cidade origemCidade) { this.origemCidade = origemCidade; }

    public Cidade getDestinoCidade() { return destinoCidade; }
    public void setDestinoCidade(Cidade destinoCidade) { this.destinoCidade = destinoCidade; }

    public Armazem getOrigemArmazem() { return origemArmazem; }
    public void setOrigemArmazem(Armazem origemArmazem) { this.origemArmazem = origemArmazem; }

    public Armazem getDestinoArmazem() { return destinoArmazem; }
    public void setDestinoArmazem(Armazem destinoArmazem) { this.destinoArmazem = destinoArmazem; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    

    public SituacaoTransporte getSituacao() { return situacao; }
    public void setSituacao(SituacaoTransporte situacao) { this.situacao = situacao; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
}