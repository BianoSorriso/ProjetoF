package com.scmflusao.controller;

import com.scmflusao.model.Agente;
import com.scmflusao.model.EmpresaParceira;
import com.scmflusao.view.AgenteView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;

public class AgenteViewListener {
    private final AgenteView view;
    private final AgenteController controller;
    private final EmpresaParceiraController empresaController;
    
    public AgenteViewListener(AgenteView view) {
        this.view = view;
        this.controller = new AgenteController();
        this.empresaController = new EmpresaParceiraController();
        
        // Inicializa a view com dados
        carregarEmpresas();
        carregarAgentes();
        
        // Adiciona os listeners aos botões
        view.setSalvarListener(new SalvarListener());
        view.setConsultarListener(new ConsultarListener());
        view.setAlterarListener(new AlterarListener());
        view.setExcluirListener(new ExcluirListener());
        view.setLimparListener(new LimparListener());
    }
    
    private void carregarEmpresas() {
        try {
            view.preencherEmpresasParceiras(empresaController.listarTodasEmpresasParceiras());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar empresas parceiras: " + e.getMessage());
        }
    }
    
    private void carregarAgentes() {
        try {
            List<Agente> agentes = controller.listarTodosAgentes();
            view.preencherTabela(agentes);
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar agentes: " + e.getMessage());
        }
    }
    
    private class SalvarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Agente agente = view.getAgenteFromForm();
                // Verifica se é um novo agente ou atualização
                boolean sucesso;
                if (agente.getId() == null) {
                    // Novo agente
                    controller.criarAgente(
                        agente.getNome(),
                        agente.getCpf(),
                        agente.getTelefone(),
                        agente.getEmail(),
                        agente.getCargo(),
                        agente.getDataNascimento(),
                        agente.isDisponivel(),
                        agente.getEmpresaParceira().getId()
                    );
                    sucesso = true;
                } else {
                    // Atualização
                    controller.atualizarAgente(
                        agente.getId(),
                        agente.getNome(),
                        agente.getCpf(),
                        agente.getTelefone(),
                        agente.getEmail(),
                        agente.getCargo(),
                        agente.getDataNascimento(),
                        agente.isDisponivel(),
                        agente.getEmpresaParceira().getId()
                    );
                    sucesso = true;
                }
                
                if (sucesso) {
                    view.exibirMensagem("Agente salvo com sucesso!");
                    view.limparFormulario();
                    carregarAgentes(); // Recarrega a tabela
                } else {
                    view.exibirMensagem("Erro ao salvar agente.");
                }
            } catch (ParseException ex) {
                view.exibirMensagem("Erro de formato: Verifique o formato da data (dd/MM/yyyy).");
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao salvar: " + ex.getMessage());
            }
        }
    }
    
    private class ExcluirListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedAgenteId();
                if (id == null) {
                    view.exibirMensagem("Selecione um agente na tabela para excluir.");
                    return;
                }
                
                int confirmacao = JOptionPane.showConfirmDialog(
                    view,
                    "Tem certeza que deseja excluir este agente?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    boolean sucesso = controller.removerAgente(id);
                    if (sucesso) {
                        view.exibirMensagem("Agente excluído com sucesso!");
                        carregarAgentes(); // Recarrega a tabela
                    } else {
                        view.exibirMensagem("Erro ao excluir agente.");
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao excluir: " + ex.getMessage());
            }
        }
    }
    
    private class LimparListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.limparFormulario();
        }
    }
    
    private class ConsultarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String nome = view.getFiltroNome();
                String cargo = view.getFiltroCargo();
                EmpresaParceira empresa = view.getFiltroEmpresa();
                String disponivel = view.getFiltroDisponivel();

                List<Agente> base = controller.listarTodosAgentes();
                List<Agente> filtrados = new ArrayList<>();
                for (Agente a : base) {
                    boolean ok = true;
                    if (nome != null && !nome.trim().isEmpty()) {
                        ok &= a.getNome() != null && a.getNome().toLowerCase().contains(nome.trim().toLowerCase());
                    }
                    if (cargo != null && !cargo.trim().isEmpty()) {
                        ok &= a.getCargo() != null && a.getCargo().toLowerCase().contains(cargo.trim().toLowerCase());
                    }
                    if (empresa != null) {
                        ok &= a.getEmpresaParceira() != null && a.getEmpresaParceira().getId().equals(empresa.getId());
                    }
                    if ("Sim".equals(disponivel)) {
                        ok &= a.isDisponivel();
                    } else if ("Não".equals(disponivel)) {
                        ok &= !a.isDisponivel();
                    }
                    if (ok) filtrados.add(a);
                }

                view.preencherTabela(filtrados);
                view.exibirMensagem(filtrados.size() + " agente(s) encontrado(s).");
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao consultar: " + ex.getMessage());
            }
        }
    }
    
    private class AlterarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedAgenteId();
                if (id == null) {
                    view.exibirMensagem("Selecione um agente na tabela para alterar.");
                    return;
                }
                
                Agente agente = controller.buscarAgentePorId(id);
                if (agente != null) {
                    view.preencherFormulario(agente);
                } else {
                    view.exibirMensagem("Agente não encontrado.");
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao alterar: " + ex.getMessage());
            }
        }
    }
}