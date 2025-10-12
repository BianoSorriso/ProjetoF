package com.scmflusao.controller;

import com.scmflusao.dao.*;
import com.scmflusao.dao.impl.*;
import com.scmflusao.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransporteController {

    private final TransporteDAO transporteDAO;
    private final EmpresaParceiraDAO empresaDAO;
    private final AgenteDAO agenteDAO;
    private final CidadeDAO cidadeDAO;
    private final ArmazemDAO armazemDAO;
    private final ProdutoDAO produtoDAO;
    private final ItemDAO itemDAO;

    public TransporteController() {
        this.transporteDAO = new TransporteDAOImpl();
        this.empresaDAO = new EmpresaParceiraDAOImpl();
        this.agenteDAO = new AgenteDAOImpl();
        this.cidadeDAO = new CidadeDAOImpl();
        this.armazemDAO = new ArmazemDAOImpl();
        this.produtoDAO = new ProdutoDAOImpl();
        this.itemDAO = new ItemDAOImpl();
    }

    public Transporte criarTransporte(Long empresaId,
                                      Long agenteId,
                                      Long origemCidadeId,
                                      Long destinoCidadeId,
                                      Long origemArmazemId,
                                      Long destinoArmazemId,
                                      Long produtoId,
                                      Long itemId,
                                      SituacaoTransporte situacao,
                                      LocalDateTime dataInicio,
                                      LocalDateTime dataFim) {
        validarEmpresa(empresaId);
        validarCidade(origemCidadeId);
        validarCidade(destinoCidadeId);
        if (agenteId != null) validarAgente(agenteId);
        if (origemArmazemId != null) validarArmazem(origemArmazemId);
        if (destinoArmazemId != null) validarArmazem(destinoArmazemId);
        // Produto é obrigatório
        if (produtoId == null) {
            throw new IllegalArgumentException("Produto é obrigatório para o transporte.");
        }
        validarProduto(produtoId);
        if (itemId != null) validarItem(itemId);

        Transporte t = new Transporte();
        EmpresaParceira ep = empresaDAO.findById(empresaId).get();
        t.setEmpresaParceira(ep);
        if (agenteId != null) t.setAgente(agenteDAO.findById(agenteId).orElse(null));
        t.setOrigemCidade(cidadeDAO.findById(origemCidadeId).orElseThrow());
        t.setDestinoCidade(cidadeDAO.findById(destinoCidadeId).orElseThrow());
        if (origemArmazemId != null) t.setOrigemArmazem(armazemDAO.findById(origemArmazemId).orElse(null));
        if (destinoArmazemId != null) t.setDestinoArmazem(armazemDAO.findById(destinoArmazemId).orElse(null));
        // Produto obrigatório
        t.setProduto(produtoDAO.findById(produtoId).orElseThrow());
        if (itemId != null) t.setItem(itemDAO.findById(itemId).orElse(null));
        t.setSituacao(situacao != null ? situacao : SituacaoTransporte.PLANEJADO);
        t.setDataInicio(dataInicio);
        t.setDataFim(dataFim);

        return transporteDAO.save(t);
    }

    public Transporte atualizarTransporte(Transporte t) {
        if (t.getId() == null || !transporteDAO.existsById(t.getId()))
            throw new IllegalArgumentException("Transporte não encontrado para atualização.");
        return transporteDAO.update(t);
    }

    public void removerTransporte(Long id) {
        if (!transporteDAO.existsById(id))
            throw new IllegalArgumentException("Transporte não encontrado com ID: " + id);
        transporteDAO.deleteById(id);
    }

    public Transporte buscarPorId(Long id) {
        Optional<Transporte> t = transporteDAO.findById(id);
        if (t.isEmpty()) throw new IllegalArgumentException("Transporte não encontrado com ID: " + id);
        return t.get();
    }

    public List<Transporte> listarTodos() { return transporteDAO.findAll(); }
    public List<Transporte> buscarPorSituacao(SituacaoTransporte s) { return transporteDAO.findBySituacao(s); }
    public List<Transporte> buscarPorEmpresa(Long empresaId) { return transporteDAO.findByEmpresaParceiraId(empresaId); }
    public List<Transporte> buscarPorAgente(Long agenteId) { return transporteDAO.findByAgenteId(agenteId); }
    public List<Transporte> buscarPorProduto(Long produtoId) { return transporteDAO.findByProdutoId(produtoId); }
    public List<Transporte> buscarPorItem(Long itemId) { return transporteDAO.findByItemId(itemId); }

    public Transporte alocarAgente(Long transporteId, Long agenteId) {
        Transporte t = buscarPorId(transporteId);
        validarAgente(agenteId);
        Agente agente = agenteDAO.findById(agenteId).orElseThrow();
        t.setAgente(agente);
        return transporteDAO.update(t);
    }

    public Transporte atualizarSituacao(Long transporteId, SituacaoTransporte situacao, LocalDateTime dataFim) {
        Transporte t = buscarPorId(transporteId);
        t.setSituacao(situacao);
        if (situacao == SituacaoTransporte.ENTREGUE) {
            t.setDataFim(dataFim != null ? dataFim : LocalDateTime.now());
        }
        return transporteDAO.update(t);
    }

    private void validarEmpresa(Long id) { if (!empresaDAO.existsById(id)) throw new IllegalArgumentException("Empresa parceira inválida"); }
    private void validarAgente(Long id) { if (!agenteDAO.existsById(id)) throw new IllegalArgumentException("Agente inválido"); }
    private void validarCidade(Long id) { if (!cidadeDAO.existsById(id)) throw new IllegalArgumentException("Cidade inválida"); }
    private void validarArmazem(Long id) { if (!armazemDAO.existsById(id)) throw new IllegalArgumentException("Armazém inválido"); }
    private void validarProduto(Long id) { if (!produtoDAO.existsById(id)) throw new IllegalArgumentException("Produto inválido"); }
    private void validarItem(Long id) { if (!itemDAO.existsById(id)) throw new IllegalArgumentException("Item inválido"); }
}
