package com.scmflusao.controller;

import com.scmflusao.model.Cidade;
import com.scmflusao.view.CidadeView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CidadeViewListener {
    private final CidadeView view;
    private final CidadeController controller;
    
    public CidadeViewListener(CidadeView view) {
        this.view = view;
        this.controller = new CidadeController();
        
        // Inicializa a view com dados
        carregarPaises();
        carregarCidades();
        
        // Adiciona os listeners aos botões
        view.setSalvarListener(new SalvarListener());
        view.setConsultarListener(new ConsultarListener());
        view.setAlterarListener(new AlterarListener());
        view.setExcluirListener(new ExcluirListener());
        view.setLimparListener(new LimparListener());
    }
    
    private void carregarPaises() {
        try {
            view.preencherPaises(controller.listarTodosPaises());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar países: " + e.getMessage());
        }
    }
    
    private void carregarCidades() {
        try {
            List<Cidade> cidades = controller.listarCidadesComArmazens();
            view.preencherTabela(cidades);
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar cidades: " + e.getMessage());
        }
    }
    
    private class SalvarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Cidade cidade = view.getCidadeFromForm();
                boolean sucesso;
                
                if (cidade.getId() == null) {
                    // Nova cidade
                    controller.criarCidade(
                        cidade.getNome(),
                        cidade.getEstado(),
                        cidade.isEhFabrica(),
                        cidade.isEhOrigem(),
                        cidade.getPais().getId()
                    );
                    sucesso = true;
                } else {
                    // Atualização
                    controller.atualizarCidade(
                        cidade.getId(),
                        cidade.getNome(),
                        cidade.getEstado(),
                        cidade.isEhFabrica(),
                        cidade.isEhOrigem(),
                        cidade.getPais().getId()
                    );
                    sucesso = true;
                }
                
                if (sucesso) {
                    view.exibirMensagem("Cidade salva com sucesso!");
                    view.limparFormulario();
                    carregarCidades(); // Recarrega a tabela
                } else {
                    view.exibirMensagem("Erro ao salvar cidade.");
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao salvar: " + ex.getMessage());
            }
        }
    }
    
    private class ExcluirListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedCidadeId();
                if (id == null) {
                    view.exibirMensagem("Selecione uma cidade na tabela para excluir.");
                    return;
                }
                
                int confirmacao = JOptionPane.showConfirmDialog(
                    view,
                    "Tem certeza que deseja excluir esta cidade?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    boolean sucesso = controller.removerCidade(id);
                    if (sucesso) {
                        view.exibirMensagem("Cidade excluída com sucesso!");
                        carregarCidades(); // Recarrega a tabela
                    } else {
                        view.exibirMensagem("Erro ao excluir cidade.");
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao excluir: " + ex.getMessage());
            }
        }
    }
    
    private class ConsultarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String[] opcoes = {"Por Nome", "Por Estado", "Listar Todas"};
                int escolha = JOptionPane.showOptionDialog(
                    view,
                    "Escolha o tipo de consulta:",
                    "Consultar Cidades",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
                );
                
                List<Cidade> resultados = null;
                String mensagem = "";
                
                switch (escolha) {
                    case 0: // Por Nome
                        String nome = JOptionPane.showInputDialog(view, "Digite o nome da cidade:");
                        if (nome != null && !nome.trim().isEmpty()) {
                            resultados = controller.buscarCidadesPorNomeComArmazens(nome);
                            mensagem = "nome: " + nome;
                        }
                        break;
                        
                    case 1: // Por Estado
                        String estado = JOptionPane.showInputDialog(view, "Digite a sigla do estado (ex: SP, RJ):");
                        if (estado != null && !estado.trim().isEmpty()) {
                            resultados = controller.buscarCidadesPorEstadoComArmazens(estado.toUpperCase());
                            mensagem = "estado: " + estado.toUpperCase();
                        }
                        break;
                        
                    case 2: // Listar Todas
                        resultados = controller.listarCidadesComArmazens();
                        mensagem = "todas as cidades";
                        break;
                        
                    default:
                        return; // Usuário cancelou
                }
                
                if (resultados != null) {
                    view.preencherTabela(resultados);
                    if (!resultados.isEmpty()) {
                        view.exibirMensagem(resultados.size() + " cidade(s) encontrada(s) para " + mensagem + ".");
                    } else {
                        view.exibirMensagem("Nenhuma cidade encontrada para " + mensagem + ".");
                    }
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao consultar cidades: " + ex.getMessage());
            }
        }
    }
    
    private class AlterarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedCidadeId();
                if (id != null) {
                    Cidade cidade = controller.buscarCidadePorId(id);
                    view.preencherFormulario(cidade);
                } else {
                    view.exibirMensagem("Selecione uma cidade na tabela para alterar.");
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao carregar cidade para alteração: " + ex.getMessage());
            }
        }
    }
    
    private class LimparListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.limparFormulario();
        }
    }
}