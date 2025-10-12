package com.scmflusao.controller;

import com.scmflusao.dao.ProdutoDAO;
import com.scmflusao.dao.CidadeDAO;
import com.scmflusao.dao.ArmazemDAO;
import com.scmflusao.dao.impl.ProdutoDAOImpl;
import com.scmflusao.dao.impl.CidadeDAOImpl;
import com.scmflusao.dao.impl.ArmazemDAOImpl;
import com.scmflusao.model.Produto;
import com.scmflusao.model.Cidade;
import com.scmflusao.model.Armazem;
import com.scmflusao.model.SituacaoProduto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

/**
 * Controller para operações relacionadas a Produto
 */
public class ProdutoController {
    
    private final ProdutoDAO produtoDAO;
    private final CidadeDAO cidadeDAO;
    private final ArmazemDAO armazemDAO;
    
    public ProdutoController() {
        this.produtoDAO = new ProdutoDAOImpl();
        this.cidadeDAO = new CidadeDAOImpl();
        this.armazemDAO = new ArmazemDAOImpl();
    }
    
    /**
     * Cria um novo produto
     * @param nome Nome do produto
     * @param descricao Descrição do produto
     * @param preco Preço do produto
     * @param cidadeFabricacaoId ID da cidade de fabricação
     * @return Produto criado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Produto criarProduto(String nome, String descricao, BigDecimal preco, Long cidadeFabricacaoId, SituacaoProduto situacao) {
        return criarProdutoInterno(nome, descricao, preco, cidadeFabricacaoId, situacao, false);
    }
    
    /**
     * Atualiza um produto existente
     * @param id ID do produto
     * @param nome Novo nome
     * @param descricao Nova descrição
     * @param preco Novo preço
     * @param cidadeFabricacaoId ID da cidade de fabricação
     * @return Produto atualizado
     * @throws IllegalArgumentException Se os dados forem inválidos
     */
    public Produto atualizarProduto(Long id, String nome, String descricao, BigDecimal preco, Long cidadeFabricacaoId, SituacaoProduto situacao) {
        return atualizarProdutoInterno(id, nome, descricao, preco, cidadeFabricacaoId, situacao, false);
    }
    
    /**
     * Remove um produto
     * @param id ID do produto
     * @return true se removido com sucesso
     * @throws IllegalArgumentException Se o produto não existir
     */
    public boolean removerProduto(Long id) {
        if (!produtoDAO.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }
        // Antes de remover, desassocia todos os itens para evitar erro de FK
        try {
            produtoDAO.atualizarItensDoProduto(id, Collections.emptyList());
        } catch (Exception ignored) {
            // Em caso de falha na desassociação, ainda tentamos remover;
            // se existir restrição de FK, o delete lançará erro apropriado
        }
        produtoDAO.deleteById(id);
        return true;
    }
    
    /**
     * Busca um produto por ID
     * @param id ID do produto
     * @return Produto encontrado
     * @throws IllegalArgumentException Se o produto não existir
     */
    public Produto buscarProdutoPorId(Long id) {
        Optional<Produto> produto = produtoDAO.findByIdWithItens(id);
        if (produto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }
        return produto.get();
    }

    /**
     * Cria um novo produto e associa itens componentes
     */
    public Produto criarProdutoComItens(String nome, String descricao, BigDecimal preco, Long cidadeFabricacaoId, SituacaoProduto situacao, List<com.scmflusao.model.Item> itens) {
        boolean permitirZeroSemItens = (itens == null || itens.isEmpty());
        Produto criado = criarProdutoInterno(nome, descricao, preco, cidadeFabricacaoId, situacao, permitirZeroSemItens);
        if (itens != null) {
            produtoDAO.atualizarItensDoProduto(criado.getId(), itens);
            criado.setItensComponentes(itens);
        }
        return criado;
    }

    /**
     * Atualiza um produto existente e sua associação de itens
     */
    public Produto atualizarProdutoComItens(Long id, String nome, String descricao, BigDecimal preco, Long cidadeFabricacaoId, SituacaoProduto situacao, List<com.scmflusao.model.Item> itens) {
        boolean permitirZeroSemItens = (itens == null || itens.isEmpty());
        Produto atualizado = atualizarProdutoInterno(id, nome, descricao, preco, cidadeFabricacaoId, situacao, permitirZeroSemItens);
        // Evita limpar associações quando a lista vier vazia durante o salvar
        if (itens != null && !itens.isEmpty()) {
            produtoDAO.atualizarItensDoProduto(atualizado.getId(), itens);
            atualizado.setItensComponentes(itens);
        }
        return atualizado;
    }

    /**
     * Atualiza somente a associação de itens de um produto, sem validar campos do produto.
     * Útil para persistir remoções/adições de itens direto da UI.
     */
    public void atualizarItensDoProduto(Long produtoId, List<com.scmflusao.model.Item> itens) {
        if (produtoId == null || !produtoDAO.existsById(produtoId)) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + produtoId);
        }
        if (itens == null) itens = java.util.Collections.emptyList();
        produtoDAO.atualizarItensDoProduto(produtoId, itens);
    }

    /**
     * Busca produtos por nome
     * @param nome Nome do produto
     * @return Lista de produtos encontrados
     * @throws IllegalArgumentException Se o nome for inválido
     */
    public List<Produto> buscarProdutosPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório para busca");
        }
        return produtoDAO.findByNome(nome);
    }
    
    /**
     * Busca produtos por cidade de fabricação
     * @param cidadeId ID da cidade
     * @return Lista de produtos encontrados
     * @throws IllegalArgumentException Se o ID da cidade for inválido
     */
    public List<Produto> buscarProdutosPorCidadeFabricacao(Long cidadeId) {
        if (cidadeId == null) {
            throw new IllegalArgumentException("ID da cidade é obrigatório");
        }
        return produtoDAO.findByCidadeFabricacaoId(cidadeId);
    }
    
    /**
     * Busca produtos por situação
     * @param situacao Situação do produto
     * @return Lista de produtos encontrados
     */
    public List<Produto> buscarProdutosPorSituacao(SituacaoProduto situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("Situação é obrigatória");
        }
        return produtoDAO.findBySituacao(situacao);
    }
    
    /**
     * Busca produtos com preço menor ou igual ao especificado
     * @param preco Preço máximo
     * @return Lista de produtos encontrados
     */
    public List<Produto> buscarProdutosComPrecoMenorOuIgual(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço deve ser um valor não negativo");
        }
        return produtoDAO.findByPrecoLessThanEqual(preco);
    }
    
    /**
     * Busca produtos com preço maior ou igual ao especificado
     * @param preco Preço mínimo
     * @return Lista de produtos encontrados
     */
    public List<Produto> buscarProdutosComPrecoMaiorOuIgual(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço deve ser um valor não negativo");
        }
        return produtoDAO.findByPrecoGreaterThanEqual(preco);
    }
    
    /**
     * Busca produtos com preço entre os valores especificados
     * @param precoMin Preço mínimo
     * @param precoMax Preço máximo
     * @return Lista de produtos encontrados
     */
    public List<Produto> buscarProdutosComPrecoEntre(BigDecimal precoMin, BigDecimal precoMax) {
        if (precoMin == null || precoMax == null) {
            throw new IllegalArgumentException("Preços mínimo e máximo são obrigatórios");
        }
        if (precoMin.compareTo(precoMax) > 0) {
            throw new IllegalArgumentException("Preço mínimo não pode ser maior que o preço máximo");
        }
        return produtoDAO.findByPrecoBetween(precoMin, precoMax);
    }
    
    /**
     * Lista todos os produtos
     * @return Lista de todos os produtos
     */
    public List<Produto> listarTodosProdutos() {
        return produtoDAO.findAll();
    }
    
    /**
     * Busca produtos por nome contendo o texto especificado
     * @param nome Parte do nome do produto
     * @return Lista de produtos encontrados
     */
    public List<Produto> buscarProdutosPorNomeContendo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório para busca");
        }
        return produtoDAO.findByNomeContaining(nome);
    }
    
    /**
     * Lista produtos ordenados por preço (crescente)
     * @return Lista de produtos ordenados
     */
    public List<Produto> listarProdutosOrdenadosPorPreco() {
        return produtoDAO.findAllOrderByPrecoAsc();
    }
    
    /**
     * Lista produtos ordenados por preço (decrescente)
     * @return Lista de produtos ordenados
     */
    public List<Produto> listarProdutosOrdenadosPorPrecoDesc() {
        return produtoDAO.findAllOrderByPrecoDesc();
    }
    
    /**
     * Lista produtos ordenados por nome
     * @return Lista de produtos ordenados
     */
    public List<Produto> listarProdutosOrdenadosPorNome() {
        return produtoDAO.findAllOrderByNome();
    }
    
    /**
     * Transiciona a situação de um produto
     * @param id ID do produto
     * @param novaSituacao Nova situação
     * @return true se a transição foi bem-sucedida, false caso contrário
     */
    public boolean transicionarSituacao(Long id, SituacaoProduto novaSituacao) {
        try {
            atualizarSituacaoProduto(id, novaSituacao);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Atualiza a situação de um produto
     * @param id ID do produto
     * @param novaSituacao Nova situação
     * @return Produto atualizado
     * @throws IllegalArgumentException Se o produto não existir ou a situação for inválida
     */
    public Produto atualizarSituacaoProduto(Long id, SituacaoProduto novaSituacao) {
        if (novaSituacao == null) {
            throw new IllegalArgumentException("Nova situação é obrigatória");
        }
        
        Optional<Produto> produtoExistente = produtoDAO.findById(id);
        if (produtoExistente.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }
        
        Produto produto = produtoExistente.get();
        
        // Validação de transição de situação (pode ser expandida conforme regras de negócio)
        if (!podeTransicionarSituacao(produto.getSituacao(), novaSituacao)) {
            throw new IllegalArgumentException("Transição de situação inválida de " + 
                produto.getSituacao() + " para " + novaSituacao);
        }
        
        produto.setSituacao(novaSituacao);
        return produtoDAO.update(produto);
    }
    
    /**
     * Transfere um produto para um armazém
     * @param produtoId ID do produto
     * @param armazemId ID do armazém
     * @return Produto atualizado
     * @throws IllegalArgumentException Se o produto ou armazém não existirem
     */
    public Produto transferirProdutoParaArmazem(Long produtoId, Long armazemId) {
        Optional<Produto> produtoExistente = produtoDAO.findById(produtoId);
        if (produtoExistente.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + produtoId);
        }
        
        Optional<Armazem> armazemExistente = armazemDAO.findById(armazemId);
        if (armazemExistente.isEmpty()) {
            throw new IllegalArgumentException("Armazém não encontrado com ID: " + armazemId);
        }
        
        Produto produto = produtoExistente.get();
        produto.setArmazemAtual(armazemExistente.get());
        
        return produtoDAO.update(produto);
    }
    
    /**
     * Conta o número total de produtos
     * @return Número de produtos
     */
    public long contarProdutos() {
        return produtoDAO.count();
    }
    
    /**
     * Conta produtos por situação
     * @param situacao Situação do produto
     * @return Número de produtos na situação especificada
     */
    public long contarProdutosPorSituacao(SituacaoProduto situacao) {
        if (situacao == null) {
            throw new IllegalArgumentException("Situação é obrigatória");
        }
        return produtoDAO.countBySituacao(situacao);
    }
    
    /**
     * Verifica se um produto existe
     * @param id ID do produto
     * @return true se existe, false caso contrário
     */
    public boolean produtoExiste(Long id) {
        if (id == null) {
            return false;
        }
        return produtoDAO.existsById(id);
    }
    
    /**
     * Valida os dados básicos de um produto
     * @param nome Nome do produto
     * @param descricao Descrição do produto
     * @param preco Preço do produto
     * @param cidadeFabricacaoId ID da cidade de fabricação
     * @throws IllegalArgumentException Se algum dado for inválido
     */
    private void validarDadosProduto(String nome, String descricao, BigDecimal preco, Long cidadeFabricacaoId) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do produto é obrigatória");
        }
        
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser um valor positivo");
        }
        
        if (cidadeFabricacaoId == null) {
            throw new IllegalArgumentException("Cidade de fabricação é obrigatória");
        }
        
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome do produto deve ter pelo menos 2 caracteres");
        }
        
        if (descricao.trim().length() < 5) {
            throw new IllegalArgumentException("Descrição deve ter pelo menos 5 caracteres");
        }
        
        if (preco.scale() > 2) {
            throw new IllegalArgumentException("Preço não pode ter mais de 2 casas decimais");
        }
    }

    // Validação flexível que permite preço zero quando explicitamente habilitado
    private void validarDadosProdutoFlex(String nome, String descricao, BigDecimal preco, Long cidadeFabricacaoId, boolean permitirZeroQuandoSemItens) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição do produto é obrigatória");
        }
        if (preco == null) {
            throw new IllegalArgumentException("Preço é obrigatório");
        }
        if (!permitirZeroQuandoSemItens && preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser um valor positivo");
        }
        if (permitirZeroQuandoSemItens && preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        if (cidadeFabricacaoId == null) {
            throw new IllegalArgumentException("Cidade de fabricação é obrigatória");
        }
        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome do produto deve ter pelo menos 2 caracteres");
        }
        if (descricao.trim().length() < 5) {
            throw new IllegalArgumentException("Descrição deve ter pelo menos 5 caracteres");
        }
        if (preco.scale() > 2) {
            throw new IllegalArgumentException("Preço não pode ter mais de 2 casas decimais");
        }
    }

    // Helper interno para criar com regra de preço zero quando sem itens
    private Produto criarProdutoInterno(String nome, String descricao, BigDecimal preco, Long cidadeFabricacaoId, SituacaoProduto situacao, boolean permitirZeroQuandoSemItens) {
        validarDadosProdutoFlex(nome, descricao, preco, cidadeFabricacaoId, permitirZeroQuandoSemItens);

        Optional<Cidade> cidade = cidadeDAO.findById(cidadeFabricacaoId);
        if (cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + cidadeFabricacaoId);
        }

        List<Produto> produtosExistentes = produtoDAO.findByNome(nome);
        if (!produtosExistentes.isEmpty()) {
            throw new IllegalArgumentException("Já existe um produto com o nome: " + nome);
        }

        Produto produto = new Produto(nome, descricao, preco, cidade.get());
        produto.setSituacao(situacao != null ? situacao : SituacaoProduto.CRIADO);
        return produtoDAO.save(produto);
    }

    // Helper interno para atualizar com regra de preço zero quando sem itens
    private Produto atualizarProdutoInterno(Long id, String nome, String descricao, BigDecimal preco, Long cidadeFabricacaoId, SituacaoProduto situacao, boolean permitirZeroQuandoSemItens) {
        validarDadosProdutoFlex(nome, descricao, preco, cidadeFabricacaoId, permitirZeroQuandoSemItens);

        Optional<Produto> produtoExistente = produtoDAO.findById(id);
        if (produtoExistente.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado com ID: " + id);
        }

        Optional<Cidade> cidade = cidadeDAO.findById(cidadeFabricacaoId);
        if (cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não encontrada com ID: " + cidadeFabricacaoId);
        }

        // Verifica duplicidade de nome
        List<Produto> produtosComMesmoNome = produtoDAO.findByNome(nome);
        for (Produto produtoComMesmoNome : produtosComMesmoNome) {
            if (!produtoComMesmoNome.getId().equals(id)) {
                throw new IllegalArgumentException("Já existe outro produto com o nome: " + nome);
            }
        }

        Produto produto = produtoExistente.get();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setCidadeFabricacao(cidade.get());
        if (situacao != null) {
            produto.setSituacao(situacao);
        }
        return produtoDAO.update(produto);
    }
    
    /**
     * Verifica se é possível transicionar de uma situação para outra
     * @param situacaoAtual Situação atual
     * @param novaSituacao Nova situação
     * @return true se a transição é válida
     */
    private boolean podeTransicionarSituacao(SituacaoProduto situacaoAtual, SituacaoProduto novaSituacao) {
        // Regras básicas de transição - podem ser expandidas conforme necessário
        switch (situacaoAtual) {
            case CRIADO:
                return novaSituacao == SituacaoProduto.VALIDADO || novaSituacao == SituacaoProduto.DESCONTINUADO;
            case VALIDADO:
                return novaSituacao == SituacaoProduto.APROVADO || novaSituacao == SituacaoProduto.CRIADO || 
                       novaSituacao == SituacaoProduto.DESCONTINUADO;
            case APROVADO:
                return novaSituacao == SituacaoProduto.EM_PRODUCAO || novaSituacao == SituacaoProduto.VALIDADO ||
                       novaSituacao == SituacaoProduto.DESCONTINUADO;
            case EM_PRODUCAO:
                return novaSituacao == SituacaoProduto.DISPONIVEL || novaSituacao == SituacaoProduto.APROVADO ||
                       novaSituacao == SituacaoProduto.DESCONTINUADO;
            case DISPONIVEL:
                return novaSituacao == SituacaoProduto.EM_PRODUCAO || novaSituacao == SituacaoProduto.DESCONTINUADO;
            case DESCONTINUADO:
                return novaSituacao == SituacaoProduto.CRIADO; // Permite reativar um produto descontinuado
            default:
                return false;
        }
    }
}