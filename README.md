## SCM FLUSÃO - Trabalho de AOO

Sistema de Controle e Monitoramento logístico (SCM) desenvolvido em Java/Swing com arquitetura MVC, persistência em banco H2 e organização por camadas. O objetivo é gerenciar agentes, empresas parceiras, produtos, itens e transportes com fluxos de criação, consulta, alteração, exclusão e relatórios.

## Screenshots

### Tela de Login
<div align="center">
  <img src="TELA LOGIN.jpg" alt="Tela de Login do Sistema" width="600">
  <p><em>Interface de autenticação com campos para email e senha</em></p>
</div>

### Tela Principal (Dashboard)
<div align="center">
  <img src="TELA PRINCIPAL.jpg" alt="Tela Principal - Dashboard" width="800">
  <p><em>Dashboard principal com menu lateral e indicadores de transportes</em></p>
</div>

### Gestão de Produtos
<div align="center">
  <img src="TELA GESTAO DE PRODUTOS.jpg" alt="Tela de Gestão de Produtos" width="800">
  <p><em>Interface para cadastro e gerenciamento de produtos e itens</em></p>
</div>

## Tecnologias Utilizadas
- `Java` (JDK) e `Swing` para UI desktop
- `H2` (`lib/h2-2.2.224.jar`) como banco de dados embutido
- Padrões: `MVC`, `DAO` (camada de acesso a dados), `Controller` + `View` + `Model`
- `Streams` da linguagem para agregações e filtros
- `Event Bus` simples para atualização em tempo real do Dashboard

## Como Foi Desenvolvido
- Estrutura em pacotes por responsabilidade: `view` (telas), `controller` (regras de negócio e orquestração), `model` (entidades), `dao` (persistência), `auth` (sessão/usuário), `events` (eventos e listeners).
- UI baseada em `Swing` com telas modulares: `AppShell` (janela principal com menu lateral) e janelas específicas para cada domínio (Agentes, Empresas, Produtos, Itens, Transportes, Relatórios).
- Regras de domínio nos `controllers` e persistência via `DAOs` conectados ao H2.
- Dashboard com contadores calculados dinamicamente a partir do estado dos transportes (Planejados, Em Trânsito, Entregues).
- Atualização em tempo real do Dashboard via `DashboardEventBus` quando transportes são criados/alterados/excluídos.

## Classes e Objetos (Principais)
- `com.scmflusao.view.AppShell`: janela principal com barra superior, menu lateral e área de conteúdo.
- `com.scmflusao.view.DashboardView`: painel com indicadores (totais de agentes, empresas e transportes por situação). Registra listener no `DashboardEventBus` para atualizar imediatamente.
- `com.scmflusao.view.TransporteView`: tela de cadastro/consulta de transportes (combos de produto/itens, cidades e armazéns, tabela de listagem).
- `com.scmflusao.controller.TransporteViewListener`: listener da tela de Transportes; carrega combos/tabela, valida e salva/atualiza/exclui. Dispara `DashboardEventBus.notifyTransporteChanged()` após mudanças.
- `com.scmflusao.controller.TransporteController`, `ProdutoController`, `ItemController`, etc.: regras de negócio e integração com DAOs.
- `com.scmflusao.model.Transporte`, `Produto`, `Item`, `Agente`, `EmpresaParceira`: entidades do domínio com estados como `SituacaoTransporte` e `SituacaoItem`.
- `com.scmflusao.view.Verificar`: ponto de entrada (`main`) que inicializa `LoginView` e, após login, abre o `AppShell`.
- `com.scmflusao.events.DashboardEventBus`: barramento de eventos minimalista para notificar alterações de transportes e atualizar o `DashboardView`.

## Estrutura de Pastas (resumo)
- `NOVOFLU/main/java/com/scmflusao/view/` — telas (`AppShell`, `DashboardView`, `LoginView`, `TransporteView`, etc.)
- `NOVOFLU/main/java/com/scmflusao/controller/` — listeners/controle (`TransporteViewListener`, `RelatorioTransporteViewListener`, etc.)
- `NOVOFLU/main/java/com/scmflusao/model/` — entidades (`Transporte`, `Produto`, `Item`, `SituacaoTransporte`, etc.)
- `NOVOFLU/main/java/com/scmflusao/dao/` — DAOs e implementações (acesso ao H2)
- `NOVOFLU/main/java/com/scmflusao/auth/` — sessão/usuário (`SessionManager`)
- `NOVOFLU/main/java/com/scmflusao/events/` — evento para Dashboard
- `NOVOFLU/lib/` — dependências (`h2-2.2.224.jar`)
- `NOVOFLU/out/` — saída de compilação (`javac -d out`)
- `NOVOFLU/data/` — arquivos do banco H2 (`scmflusao.mv.db`)

## Detalhes Técnicos
- Preenchimento de combos de `Itens` com base no `Produto` selecionado; quando a quantidade de um item no produto é maior que 1, a lista pode ser expandida (cada unidade representada) ou agregada conforme necessidade.
- Listener de produto e botão “Atualizar Itens” para recarregar explicitamente os itens do produto selecionado.
- Persistência com H2 em arquivo; DAOs encapsulam operações CRUD e consultas.
- Dashboard recalcula contadores ao construir a view e ao receber eventos de mudança em transportes via `DashboardEventBus`.
- Perfis de acesso (`ADMIN`, `OPERADOR`, `PARCEIRO`) controlam permissões nas telas.

## Como Executar

### Opção 1: Via Linha de Comando
1. Compilar fontes:
   - `javac -cp ".;NOVOFLU\lib\h2-2.2.224.jar" -d NOVOFLU\out (Get-ChildItem -Recurse NOVOFLU\main\java -Filter *.java | %% { $_.FullName })`
   - Em PowerShell (Windows), pode usar o script já demonstrado nas automações do projeto para coletar arquivos e compilar.
2. Executar aplicação:
   - `java -cp "NOVOFLU\out;NOVOFLU\lib\h2-2.2.224.jar" com.scmflusao.view.Verificar`

### Opção 2: Via Eclipse IDE
1. **Importar o projeto:**
   - Abra o Eclipse IDE
   - Vá em `File` → `Import` → `General` → `Existing Projects into Workspace`
   - Clique em `Browse` e selecione a pasta `NOVOFLU` (que contém os arquivos `.project` e `.classpath`)
   - Marque o projeto "NOVOFLU" e clique em `Finish`

2. **Configurar o classpath (se necessário):**
   - Clique com o botão direito no projeto → `Properties`
   - Vá em `Java Build Path` → `Libraries`
   - Se a biblioteca H2 não estiver presente, clique em `Add External JARs...`
   - Navegue até `NOVOFLU/lib/h2-2.2.224.jar` e adicione

3. **Executar a aplicação:**
   - Navegue até `src/main/java/com/scmflusao/view/Verificar.java`
   - Clique com o botão direito → `Run As` → `Java Application`
   - Ou use o atalho `Ctrl+F11` com o arquivo selecionado

### Login
- Consulte `LOGIN E SENHA.txt` na raiz do projeto para credenciais de teste, se necessário.

 

## Licença
Este projeto está licenciado sob a Licença MIT. Consulte o arquivo `LICENSE` para detalhes.

## Integrantes do Grupo
- Gustavo Coroa
- Giulia Baum
- Fabiano Bastos

---
Este README documenta o uso das principais camadas, entidades e fluxos da aplicação, os passos de execução e como publicar o trabalho no repositório solicitado.
