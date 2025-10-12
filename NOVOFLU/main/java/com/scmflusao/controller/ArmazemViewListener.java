package com.scmflusao.controller;

import com.scmflusao.model.Armazem;
import com.scmflusao.view.ArmazemView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ArmazemViewListener {
    private final ArmazemView view;
    private final ArmazemController controller;
    private final CidadeController cidadeController;
    
    public ArmazemViewListener(ArmazemView view) {
        this.view = view;
        this.controller = new ArmazemController();
        this.cidadeController = new CidadeController();
        
        // Inicializa a view com dados
        carregarCidades();
        carregarArmazens();
        
        // Adiciona os listeners aos botões
        view.setSalvarListener(new SalvarListener());
        view.setConsultarListener(new ConsultarListener());
        view.setAlterarListener(new AlterarListener());
        view.setExcluirListener(new ExcluirListener());
        view.setLimparListener(new LimparListener());
    }
    
    private void carregarCidades() {
        try {
            view.preencherCidades(cidadeController.listarTodasCidades());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar cidades: " + e.getMessage());
        }
    }
    
    private void carregarArmazens() {
        try {
            List<Armazem> armazens = controller.listarTodosArmazens();
            view.preencherTabela(armazens);
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar armazéns: " + e.getMessage());
        }
    }
    
    private class SalvarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Armazem armazem = view.getArmazemFromForm();
                boolean sucesso;
                
                if (armazem.getId() == null) {
                    // Novo armazém
                    controller.criarArmazem(
                        armazem.getNome(),
                        armazem.getEndereco(),
                        armazem.getCapacidadeTotal(),
                        armazem.getCapacidadeDisponivel(),
                        armazem.getCidade().getId()
                    );
                    sucesso = true;
                } else {
                    // Atualização
                    controller.atualizarArmazem(
                        armazem.getId(),
                        armazem.getNome(),
                        armazem.getEndereco(),
                        armazem.getCapacidadeTotal(),
                        armazem.getCapacidadeDisponivel(),
                        armazem.getCidade().getId()
                    );
                    sucesso = true;
                }
                
                if (sucesso) {
                    view.exibirMensagem("Armazém salvo com sucesso!");
                    view.limparFormulario();
                    carregarArmazens(); // Recarrega a tabela
                } else {
                    view.exibirMensagem("Erro ao salvar armazém.");
                }
            } catch (NumberFormatException ex) {
                view.exibirMensagem("Erro de formato: Verifique os campos numéricos.");
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao salvar: " + ex.getMessage());
            }
        }
    }
    
    private class ExcluirListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedArmazemId();
                if (id == null) {
                    view.exibirMensagem("Selecione um armazém na tabela para excluir.");
                    return;
                }
                
                int confirmacao = JOptionPane.showConfirmDialog(
                    view,
                    "Tem certeza que deseja excluir este armazém?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    boolean sucesso = controller.removerArmazem(id);
                    if (sucesso) {
                        view.exibirMensagem("Armazém excluído com sucesso!");
                        carregarArmazens(); // Recarrega a tabela
                    } else {
                        view.exibirMensagem("Erro ao excluir armazém.");
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
                String nome = JOptionPane.showInputDialog(view, "Digite o nome do armazém para consultar:");
                if (nome != null && !nome.trim().isEmpty()) {
                    List<Armazem> armazens = controller.buscarArmazensPorNome(nome);
                    if (armazens.isEmpty()) {
                        view.exibirMensagem("Nenhum armazém encontrado com o nome '" + nome + "'.");
                    } else {
                        view.preencherTabela(armazens);
                        view.exibirMensagem("Consulta realizada com sucesso!");
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao consultar: " + ex.getMessage());
            }
        }
    }
    
    private class AlterarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedArmazemId();
                if (id == null) {
                    view.exibirMensagem("Selecione um armazém na tabela para alterar.");
                    return;
                }
                
                Armazem armazem = controller.buscarArmazemPorId(id);
                if (armazem != null) {
                    view.preencherFormulario(armazem);
                } else {
                    view.exibirMensagem("Armazém não encontrado.");
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao alterar: " + ex.getMessage());
            }
        }
    }
}