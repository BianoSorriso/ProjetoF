package com.scmflusao.controller;

import com.scmflusao.model.*;
import com.scmflusao.view.TransporteView;
import com.scmflusao.auth.SessionManager;
import com.scmflusao.events.DashboardEventBus;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.util.List;

public class TransporteViewListener {
    private final TransporteView view;
    private final TransporteController transporteController;
    private final EmpresaParceiraController empresaController;
    private final AgenteController agenteController;
    private final CidadeController cidadeController;
    private final ArmazemController armazemController;
    private final ProdutoController produtoController;
    private final ItemController itemController;

    public TransporteViewListener(TransporteView view) {
        this.view = view;
        this.transporteController = new TransporteController();
        this.empresaController = new EmpresaParceiraController();
        this.agenteController = new AgenteController();
        this.cidadeController = new CidadeController();
        this.armazemController = new ArmazemController();
        this.produtoController = new ProdutoController();
        this.itemController = new ItemController();

        carregarCombos();
        carregarTransportes();
        // Garante preenchimento inicial consistente do combo de itens
        preencherItensParaProdutoSelecionado();

        view.setSalvarListener(new SalvarListener());
        view.setConsultarListener(new ConsultarListener());
        view.setAlterarListener(new AlterarListener());
        view.setExcluirListener(new ExcluirListener());
        view.setLimparListener(new LimparListener());
        // Filtra itens quando o produto mudar
        view.setProdutoChangeListener(new ProdutoChangeListener());
        // Botão explícito para atualizar os itens do produto atual
        view.setAtualizarItensListener(new AtualizarItensListener());
        // Itens são filtrados pelo produto; não há spinner de quantidade

        // Ajusta permissões por perfil
        Perfil p = SessionManager.getInstance().getUsuarioAtual().getPerfil();
        boolean isAdmin = p == Perfil.ADMIN;
        boolean isOperador = p == Perfil.OPERADOR;
        boolean isParceiro = p == Perfil.PARCEIRO;
        if (isAdmin || isOperador) {
            view.setSalvarEnabled(true);
            view.setConsultarEnabled(true);
            view.setAlterarEnabled(true);
            view.setExcluirEnabled(true);
        } else if (isParceiro) {
            // Parceiro pode agendar (salvar novo) e consultar; não pode alterar/excluir
            view.setSalvarEnabled(true);
            view.setConsultarEnabled(true);
            view.setAlterarEnabled(false);
            view.setExcluirEnabled(false);
        }
    }

    // Aplica regra de preenchimento dos itens conforme produto atualmente selecionado
    private void preencherItensParaProdutoSelecionado() {
        try {
            com.scmflusao.model.Transporte t = view.getTransporteFromForm();
            com.scmflusao.model.Produto produtoSel = t.getProduto();

            java.util.List<com.scmflusao.model.Item> itens;
            if (produtoSel != null && produtoSel.getId() != null) {
                com.scmflusao.model.Produto completo = produtoController.buscarProdutoPorId(produtoSel.getId());
                java.util.List<com.scmflusao.model.Item> expandida = new java.util.ArrayList<>();
                for (com.scmflusao.model.Item it : completo.getItensComponentes()) {
                    if (it == null || it.getId() == null) continue;
                    int qtd = (it.getQuantidade() != null && it.getQuantidade() > 0) ? it.getQuantidade() : 1;
                    for (int n = 0; n < qtd; n++) {
                        com.scmflusao.model.Item clone = new com.scmflusao.model.Item();
                        clone.setId(it.getId());
                        clone.setNome(it.getNome());
                        clone.setDescricao(it.getDescricao());
                        clone.setPeso(it.getPeso());
                        clone.setQuantidade(1);
                        clone.setValorUnitario(it.getValorUnitario());
                        clone.setSituacao(it.getSituacao());
                        clone.setCidadeOrigem(it.getCidadeOrigem());
                        clone.setArmazemAtual(it.getArmazemAtual());
                        clone.setDataEntrada(it.getDataEntrada());
                        clone.setDataAtualizacao(it.getDataAtualizacao());
                        expandida.add(clone);
                    }
                }
                itens = expandida;
            } else {
                itens = itemController.listarTodosItens();
            }
            view.preencherItens(itens);
        } catch (Exception ex) {
            try {
                view.preencherItens(itemController.listarTodosItens());
            } catch (Exception ignore) {}
            view.exibirMensagem("Erro ao filtrar itens pelo produto: " + ex.getMessage());
        }
    }

    // Listener para filtrar itens conforme o produto selecionado
    private class ProdutoChangeListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            preencherItensParaProdutoSelecionado();
        }
    }

    // Listener do botão Atualizar Itens para recarregar explicitamente
    private class AtualizarItensListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            preencherItensParaProdutoSelecionado();
        }
    }

    

    private void carregarCombos() {
        try {
            view.preencherEmpresas(empresaController.listarTodasEmpresasParceiras());
            view.preencherAgentes(agenteController.listarTodosAgentes());
            List<Cidade> cidades = cidadeController.listarTodasCidades();
            view.preencherCidadesOrigem(cidades);
            view.preencherCidadesDestino(cidades);
            view.preencherArmazensOrigem(armazemController.listarTodosArmazens());
            view.preencherArmazensDestino(armazemController.listarTodosArmazens());
            view.preencherProdutos(produtoController.listarTodosProdutos());
            view.preencherItens(itemController.listarTodosItens());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void carregarTransportes() {
        try {
            view.preencherTabela(transporteController.listarTodos());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar transportes: " + e.getMessage());
        }
    }

    private class SalvarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Transporte t = view.getTransporteFromForm();
                // Restrição: Parceiro só pode cadastrar novos transportes (sem alteração)
                Perfil p = SessionManager.getInstance().getUsuarioAtual().getPerfil();
                if (p == Perfil.PARCEIRO && t.getId() != null) {
                    view.exibirMensagem("Parceiro só pode agendar novo transporte (sem alteração).");
                    return;
                }
                // Preenchimento automático de datas conforme situação
                if (t.getSituacao() == SituacaoTransporte.EM_TRANSITO && t.getDataInicio() == null) {
                    t.setDataInicio(LocalDateTime.now());
                }
                if (t.getSituacao() == SituacaoTransporte.ENTREGUE) {
                    if (t.getDataInicio() == null) t.setDataInicio(LocalDateTime.now());
                    if (t.getDataFim() == null) t.setDataFim(LocalDateTime.now());
                }
                if (t.getId() == null) {
                    // Validação: produto obrigatório
                    if (t.getProduto() == null || t.getProduto().getId() == null) {
                        view.exibirMensagem("Selecione um produto para criar o transporte.");
                        return;
                    }
                    transporteController.criarTransporte(
                        t.getEmpresaParceira() != null ? t.getEmpresaParceira().getId() : null,
                        t.getAgente() != null ? t.getAgente().getId() : null,
                        t.getOrigemCidade() != null ? t.getOrigemCidade().getId() : null,
                        t.getDestinoCidade() != null ? t.getDestinoCidade().getId() : null,
                        t.getOrigemArmazem() != null ? t.getOrigemArmazem().getId() : null,
                        t.getDestinoArmazem() != null ? t.getDestinoArmazem().getId() : null,
                        t.getProduto() != null ? t.getProduto().getId() : null,
                        t.getItem() != null ? t.getItem().getId() : null,
                        t.getSituacao(),
                        t.getDataInicio(),
                        t.getDataFim()
                    );
                } else {
                    transporteController.atualizarTransporte(t);
                }
                view.exibirMensagem("Transporte salvo com sucesso!");
                carregarTransportes();
                // Atualiza dashboard imediatamente
                DashboardEventBus.getInstance().notifyTransporteChanged();
                view.limparFormulario();
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao salvar transporte: " + ex.getMessage());
            }
        }
    }

    private class ExcluirListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedTransporteId();
                if (id == null) { view.exibirMensagem("Selecione um transporte na tabela."); return; }
                int confirm = JOptionPane.showConfirmDialog(view, "Confirma excluir transporte?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    transporteController.removerTransporte(id);
                    view.exibirMensagem("Transporte excluído!");
                    carregarTransportes();
                    // Atualiza dashboard imediatamente
                    DashboardEventBus.getInstance().notifyTransporteChanged();
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao excluir: " + ex.getMessage());
            }
        }
    }

    private class AlterarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedTransporteId();
                if (id == null) { view.exibirMensagem("Selecione um transporte na tabela."); return; }
                Transporte t = transporteController.buscarPorId(id);
                view.preencherFormulario(t);
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao carregar transporte: " + ex.getMessage());
            }
        }
    }

    private class LimparListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { view.limparFormulario(); }
    }

    private class ConsultarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] options = {"Todos", "Por Situação", "Por Empresa", "Por Agente"};
            String opt = (String) JOptionPane.showInputDialog(view, "Consultar: ", "Consulta", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (opt == null) return;
            try {
                switch (opt) {
                    case "Por Situação":
                        SituacaoTransporte s = (SituacaoTransporte) JOptionPane.showInputDialog(view, "Situação:", "Situação", JOptionPane.PLAIN_MESSAGE, null, SituacaoTransporte.values(), SituacaoTransporte.PLANEJADO);
                        view.preencherTabela(transporteController.buscarPorSituacao(s));
                        break;
                    case "Por Empresa":
                        String empIdStr = JOptionPane.showInputDialog(view, "ID da Empresa:");
                        Long empId = Long.parseLong(empIdStr);
                        view.preencherTabela(transporteController.buscarPorEmpresa(empId));
                        break;
                    case "Por Agente":
                        String agIdStr = JOptionPane.showInputDialog(view, "ID do Agente:");
                        Long agId = Long.parseLong(agIdStr);
                        view.preencherTabela(transporteController.buscarPorAgente(agId));
                        break;
                    default:
                        carregarTransportes();
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro na consulta: " + ex.getMessage());
            }
        }
    }
}