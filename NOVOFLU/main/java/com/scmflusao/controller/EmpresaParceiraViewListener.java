package com.scmflusao.controller;

import com.scmflusao.model.EmpresaParceira;
import com.scmflusao.model.TipoServico;
import com.scmflusao.view.EmpresaParceiraView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class EmpresaParceiraViewListener {
    private final EmpresaParceiraView view;
    private final EmpresaParceiraController controller;
    
    public EmpresaParceiraViewListener(EmpresaParceiraView view) {
        this.view = view;
        this.controller = new EmpresaParceiraController();
        
        // Inicializa a view com dados
        carregarEmpresas();
        
        // Adiciona os listeners aos botões
        view.setSalvarListener(new SalvarListener());
        view.setConsultarListener(new ConsultarListener());
        view.setAlterarListener(new AlterarListener());
        view.setExcluirListener(new ExcluirListener());
        view.setLimparListener(new LimparListener());
    }
    
    private void carregarEmpresas() {
        try {
            List<EmpresaParceira> empresas = controller.listarTodasEmpresasParceiras();
            view.preencherTabela(empresas);
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar empresas: " + e.getMessage());
        }
    }
    
    private class SalvarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                EmpresaParceira empresa = view.getEmpresaFromForm();
                boolean sucesso;
                
                if (empresa.getId() == null) {
                    // Nova empresa
                    controller.criarEmpresaParceira(
                        empresa.getNome(),
                        empresa.getCnpj(),
                        empresa.getEndereco(),
                        empresa.getTelefone(),
                        empresa.getEmail(),
                        empresa.getTipoServico(),
                        empresa.isAtivo()
                    );
                    sucesso = true;
                } else {
                    // Atualização
                    controller.atualizarEmpresaParceira(
                        empresa.getId(),
                        empresa.getNome(),
                        empresa.getCnpj(),
                        empresa.getEndereco(),
                        empresa.getTelefone(),
                        empresa.getEmail(),
                        empresa.getTipoServico(),
                        empresa.isAtivo()
                    );
                    sucesso = true;
                }
                
                if (sucesso) {
                    view.exibirMensagem("Empresa parceira salva com sucesso!");
                    view.limparFormulario();
                    carregarEmpresas(); // Recarrega a tabela
                } else {
                    view.exibirMensagem("Erro ao salvar empresa parceira.");
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
                Long id = view.getSelectedEmpresaId();
                if (id == null) {
                    view.exibirMensagem("Selecione uma empresa na tabela para excluir.");
                    return;
                }
                
                int confirmacao = JOptionPane.showConfirmDialog(
                    view,
                    "Tem certeza que deseja excluir esta empresa?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    boolean sucesso = controller.removerEmpresaParceira(id);
                    if (sucesso) {
                        view.exibirMensagem("Empresa excluída com sucesso!");
                        carregarEmpresas(); // Recarrega a tabela
                    } else {
                        view.exibirMensagem("Erro ao excluir empresa.");
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
                String nome = view.getFiltroNome();
                String cnpj = view.getFiltroCnpj();
                TipoServico tipo = view.getFiltroTipoServico();
                String ativo = view.getFiltroAtivo();

                List<EmpresaParceira> base = controller.listarTodasEmpresasParceiras();
                List<EmpresaParceira> filtradas = new ArrayList<>();
                for (EmpresaParceira ep : base) {
                    boolean ok = true;
                    if (nome != null && !nome.trim().isEmpty()) {
                        ok &= ep.getNome() != null && ep.getNome().toLowerCase().contains(nome.trim().toLowerCase());
                    }
                    if (cnpj != null && !cnpj.trim().isEmpty()) {
                        String f = cnpj.replaceAll("\\D", "");
                        String cnpjEmpresa = ep.getCnpj() != null ? ep.getCnpj().replaceAll("\\D", "") : "";
                        ok &= cnpjEmpresa.equals(f);
                    }
                    if (tipo != null) {
                        ok &= ep.getTipoServico() == tipo;
                    }
                    if ("Sim".equals(ativo)) {
                        ok &= ep.isAtivo();
                    } else if ("Não".equals(ativo)) {
                        ok &= !ep.isAtivo();
                    }
                    if (ok) filtradas.add(ep);
                }

                view.preencherTabela(filtradas);
                view.exibirMensagem(filtradas.size() + " empresa(s) encontrada(s).");
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao consultar empresas: " + ex.getMessage());
            }
        }
    }
    
    private class AlterarListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Long id = view.getSelectedEmpresaId();
                if (id != null) {
                    EmpresaParceira empresa = controller.buscarEmpresaParceiraPorId(id);
                    view.preencherFormulario(empresa);
                } else {
                    view.exibirMensagem("Selecione uma empresa na tabela para alterar.");
                }
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao carregar empresa para alteração: " + ex.getMessage());
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