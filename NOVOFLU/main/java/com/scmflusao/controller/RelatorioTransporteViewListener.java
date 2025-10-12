package com.scmflusao.controller;

import com.scmflusao.model.Transporte;
import com.scmflusao.model.SituacaoTransporte;
import com.scmflusao.view.RelatorioTransporteView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RelatorioTransporteViewListener {
    private final RelatorioTransporteView view;
    private final TransporteController controller;

    public RelatorioTransporteViewListener(RelatorioTransporteView view) {
        this.view = view;
        this.controller = new TransporteController();
        this.view.setGerarListener(new GerarListener());
        this.view.setExportarListener(new ExportarListener());
        this.view.setExportarTxtListener(new ExportarTxtListener());
        carregarInicial();
    }

    private void carregarInicial() {
        try {
            view.preencherTabela(controller.listarTodos());
        } catch (Exception e) {
            view.exibirMensagem("Erro ao carregar relatório: " + e.getMessage());
        }
    }

    private class ExportarTxtListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            try {
                List<Transporte> lista = controller.listarTodos();
                SituacaoTransporte s = view.getSituacaoSelecionada();
                LocalDateTime inicio = view.getInicio();
                LocalDateTime fim = view.getFim();
                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                List<Transporte> filtrada = lista.stream().filter(t -> {
                    boolean okSituacao = s == null || t.getSituacao() == s;
                    boolean okInicio = inicio == null || (t.getDataInicio() != null && !t.getDataInicio().isBefore(inicio));
                    boolean okFim = fim == null || (t.getDataFim() != null && !t.getDataFim().isAfter(fim));
                    return okSituacao && okInicio && okFim;
                }).collect(Collectors.toList());

                long total = filtrada.size();
                long planejados = filtrada.stream().filter(t -> t.getSituacao() == SituacaoTransporte.PLANEJADO).count();
                long emTransporte = filtrada.stream().filter(t -> t.getSituacao() == SituacaoTransporte.EM_TRANSITO).count();
                long entregue = filtrada.stream().filter(t -> t.getSituacao() == SituacaoTransporte.ENTREGUE).count();
                long semInicio = filtrada.stream().filter(t -> t.getDataInicio() == null).count();
                long semFim = filtrada.stream().filter(t -> t.getDataFim() == null).count();

                // Cabeçalho e resumo
                StringBuilder sb = new StringBuilder();
                sb.append("=== RELATÓRIO GERAL DO SCM FLUSÃO ===\n\n");
                sb.append("▸ Gerado em: ").append(LocalDateTime.now().format(f)).append("\n");
                if (inicio != null || fim != null || s != null) {
                    sb.append("▸ Filtros: ");
                    sb.append(inicio != null ? ("De " + inicio.format(f)) : "");
                    sb.append((inicio != null && fim != null) ? " até " : "");
                    sb.append(fim != null ? fim.format(f) : "");
                    sb.append(s != null ? (" | Situação: " + s.name()) : "");
                    sb.append("\n\n");
                } else {
                    sb.append("▸ Filtros: nenhum\n\n");
                }

                sb.append("▌ ESTATÍSTICAS GERAIS:\n");
                sb.append("- Total de Transportes: ").append(total).append("\n");
                sb.append("- Planejados: ").append(planejados).append("\n");
                sb.append("- Em Transporte: ").append(emTransporte).append("\n");
                sb.append("- Entregues: ").append(entregue).append("\n");
                sb.append("- Sem Data Início: ").append(semInicio).append("\n");
                sb.append("- Sem Data Fim: ").append(semFim).append("\n\n");

                sb.append("▌ TRANSPORTES (TOP 20):\n");
                List<Transporte> top = filtrada.stream().limit(20).collect(Collectors.toList());
                for (Transporte t : top) {
                    sb.append(String.format("• ID %s | Empresa: %s | Agente: %s | Origem: %s | Destino: %s | Situação: %s | Início: %s | Fim: %s\n",
                            t.getId()!=null? t.getId().toString():"",
                            t.getEmpresaParceira()!=null? t.getEmpresaParceira().getNome():"",
                            t.getAgente()!=null? t.getAgente().getNome():"",
                            t.getOrigemCidade()!=null? t.getOrigemCidade().getNome():"",
                            t.getDestinoCidade()!=null? t.getDestinoCidade().getNome():"",
                            t.getSituacao()!=null? t.getSituacao().name():"",
                            t.getDataInicio()!=null? t.getDataInicio().format(f):"",
                            t.getDataFim()!=null? t.getDataFim().format(f):""));
                }
                if (top.isEmpty()) sb.append("(Sem registros para exibir)\n");

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Salvar Relatório TXT");
                if (chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                    Path p = chooser.getSelectedFile().toPath();
                    Files.write(p, sb.toString().getBytes(StandardCharsets.UTF_8));
                    JOptionPane.showMessageDialog(view, "Arquivo salvo: " + p.toString());
                }
            } catch (IOException ex) {
                view.exibirMensagem("Erro ao salvar TXT: " + ex.getMessage());
            } catch (Exception ex) {
                view.exibirMensagem("Erro na exportação: " + ex.getMessage());
            }
        }
    }

    private class GerarListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            try {
                SituacaoTransporte s = view.getSituacaoSelecionada();
                LocalDateTime inicio = view.getInicio();
                LocalDateTime fim = view.getFim();
                List<Transporte> lista = controller.listarTodos();

                List<Transporte> filtrada = lista.stream().filter(t -> {
                    boolean okSituacao = s == null || t.getSituacao() == s;
                    boolean okInicio = inicio == null || (t.getDataInicio() != null && !t.getDataInicio().isBefore(inicio));
                    boolean okFim = fim == null || (t.getDataFim() != null && !t.getDataFim().isAfter(fim));
                    return okSituacao && okInicio && okFim;
                }).collect(Collectors.toList());

                view.preencherTabela(filtrada);
            } catch (Exception ex) {
                view.exibirMensagem("Erro ao gerar relatório: " + ex.getMessage());
            }
        }
    }

    private class ExportarListener implements ActionListener {
        @Override public void actionPerformed(ActionEvent e) {
            try {
                List<Transporte> lista = controller.listarTodos();
                SituacaoTransporte s = view.getSituacaoSelecionada();
                LocalDateTime inicio = view.getInicio();
                LocalDateTime fim = view.getFim();
                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                List<Transporte> filtrada = lista.stream().filter(t -> {
                    boolean okSituacao = s == null || t.getSituacao() == s;
                    boolean okInicio = inicio == null || (t.getDataInicio() != null && !t.getDataInicio().isBefore(inicio));
                    boolean okFim = fim == null || (t.getDataFim() != null && !t.getDataFim().isAfter(fim));
                    return okSituacao && okInicio && okFim;
                }).collect(Collectors.toList());

                StringBuilder sb = new StringBuilder();
                sb.append("ID;Empresa;Agente;Origem;Destino;Situacao;DataInicio;DataFim\n");
                for (Transporte t : filtrada) {
                    sb.append(t.getId()).append(';')
                      .append(t.getEmpresaParceira()!=null? t.getEmpresaParceira().getNome():"").append(';')
                      .append(t.getAgente()!=null? t.getAgente().getNome():"").append(';')
                      .append(t.getOrigemCidade()!=null? t.getOrigemCidade().getNome():"").append(';')
                      .append(t.getDestinoCidade()!=null? t.getDestinoCidade().getNome():"").append(';')
                      .append(t.getSituacao()!=null? t.getSituacao().name():"").append(';')
                      .append(t.getDataInicio()!=null? t.getDataInicio().format(f):"").append(';')
                      .append(t.getDataFim()!=null? t.getDataFim().format(f):"").append('\n');
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Salvar CSV");
                if (chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                    Path p = chooser.getSelectedFile().toPath();
                    Files.write(p, sb.toString().getBytes());
                    JOptionPane.showMessageDialog(view, "Arquivo salvo: " + p.toString());
                }
            } catch (IOException ex) {
                view.exibirMensagem("Erro ao salvar CSV: " + ex.getMessage());
            } catch (Exception ex) {
                view.exibirMensagem("Erro na exportação: " + ex.getMessage());
            }
        }
    }
}