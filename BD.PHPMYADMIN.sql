-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 11/10/2025 às 18:32
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `scmflusao`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `agente`
--

CREATE TABLE `agente` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `cpf` varchar(11) NOT NULL,
  `data_nascimento` date DEFAULT NULL,
  `telefone` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `cargo` varchar(50) NOT NULL,
  `disponivel` tinyint(1) DEFAULT 1,
  `empresa_parceira_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `agente`
--

INSERT INTO `agente` (`id`, `nome`, `cpf`, `data_nascimento`, `telefone`, `email`, `cargo`, `disponivel`, `empresa_parceira_id`) VALUES
(6, 'Franchesco Virgulinni', '12345678910', '1987-06-26', '21999999999', 'sbinalla@gmail.com', 'La Maquina Mas Veloz', 1, 6);

-- --------------------------------------------------------

--
-- Estrutura para tabela `armazem`
--

CREATE TABLE `armazem` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `endereco` varchar(255) NOT NULL,
  `capacidade` decimal(10,2) NOT NULL,
  `ocupacao` decimal(10,2) DEFAULT 0.00,
  `ativo` tinyint(1) DEFAULT 1,
  `cidade_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `armazem`
--

INSERT INTO `armazem` (`id`, `nome`, `endereco`, `capacidade`, `ocupacao`, `ativo`, `cidade_id`) VALUES
(14, 'ARMAZEN 05', 'Rua01', 10000.00, 500.00, 1, 174);

-- --------------------------------------------------------

--
-- Estrutura para tabela `armazem_produto`
--

CREATE TABLE `armazem_produto` (
  `id` bigint(20) NOT NULL,
  `armazem_id` bigint(20) NOT NULL,
  `produto_id` bigint(20) NOT NULL,
  `quantidade` int(11) NOT NULL DEFAULT 0,
  `data_entrada` timestamp NOT NULL DEFAULT current_timestamp(),
  `data_atualizacao` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `cidade`
--

CREATE TABLE `cidade` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `estado` varchar(50) NOT NULL,
  `cep` varchar(8) DEFAULT NULL,
  `pais_id` bigint(20) NOT NULL,
  `eh_fabrica` tinyint(1) DEFAULT 0,
  `eh_origem` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `cidade`
--

INSERT INTO `cidade` (`id`, `nome`, `estado`, `cep`, `pais_id`, `eh_fabrica`, `eh_origem`) VALUES
(174, 'São Gonçalo', 'RJ', '24417017', 1, 1, 0),
(182, 'Maricá', 'RJ', NULL, 1, 0, 1);

-- --------------------------------------------------------

--
-- Estrutura para tabela `empresa_parceira`
--

CREATE TABLE `empresa_parceira` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `cnpj` varchar(14) NOT NULL,
  `endereco` varchar(255) NOT NULL,
  `telefone` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `tipo_servico` enum('TRANSPORTE','ARMAZENAMENTO','DISTRIBUICAO','LOGISTICA_REVERSA','CONSULTORIA') NOT NULL,
  `ativo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `empresa_parceira`
--

INSERT INTO `empresa_parceira` (`id`, `nome`, `cnpj`, `endereco`, `telefone`, `email`, `tipo_servico`, `ativo`) VALUES
(6, 'Motoristas de Eurotruck Simulator', '12345678970145', 'Rua012', '21995511308', 'fabiano@gmail.com', 'DISTRIBUICAO', 1);

-- --------------------------------------------------------

--
-- Estrutura para tabela `item`
--

CREATE TABLE `item` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `descricao` text DEFAULT NULL,
  `peso` decimal(8,3) NOT NULL,
  `quantidade` int(11) NOT NULL DEFAULT 1,
  `valor_unitario` decimal(10,2) DEFAULT NULL,
  `situacao` enum('REGISTRADO','VERIFICADO','EM_ESTOQUE','EM_MANUTENCAO','ALOCADO','UTILIZADO') NOT NULL DEFAULT 'REGISTRADO',
  `cidade_origem_id` bigint(20) NOT NULL,
  `armazem_atual_id` bigint(20) DEFAULT NULL,
  `data_entrada` timestamp NOT NULL DEFAULT current_timestamp(),
  `data_atualizacao` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `produto_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `item`
--

INSERT INTO `item` (`id`, `nome`, `descricao`, `peso`, `quantidade`, `valor_unitario`, `situacao`, `cidade_origem_id`, `armazem_atual_id`, `data_entrada`, `data_atualizacao`, `produto_id`) VALUES
(2, 'Placa Mae Asus', 'Modelo', 5.000, 1, 600.00, 'REGISTRADO', 182, NULL, '2025-09-12 02:49:32', '2025-09-12 02:49:32', NULL),
(3, 'Placa de Video nvidia 5060', 'placa', 4.000, 1, 4000.00, 'REGISTRADO', 182, NULL, '2025-09-12 02:50:13', '2025-09-12 02:50:13', NULL);

-- --------------------------------------------------------

--
-- Estrutura para tabela `pais`
--

CREATE TABLE `pais` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `codigo` varchar(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `pais`
--

INSERT INTO `pais` (`id`, `nome`, `codigo`) VALUES
(1, 'Brasil', 'BRA'),
(2, 'Estados Unidos', 'USA'),
(3, 'Canadá', 'CAN'),
(4, 'Argentina', 'ARG'),
(5, 'China', 'CHN'),
(6, 'Japão', 'JPN'),
(7, 'Alemanha', 'DEU'),
(8, 'França', 'FRA'),
(9, 'Reino Unido', 'GBR'),
(10, 'Itália', 'ITA'),
(22, 'Brasil', 'BR');

-- --------------------------------------------------------

--
-- Estrutura para tabela `produto`
--

CREATE TABLE `produto` (
  `id` bigint(20) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `descricao` text DEFAULT NULL,
  `preco` decimal(10,2) NOT NULL,
  `situacao` enum('CRIADO','VALIDADO','APROVADO','EM_PRODUCAO','DISPONIVEL','DESCONTINUADO') NOT NULL DEFAULT 'CRIADO',
  `cidade_fabricacao_id` bigint(20) NOT NULL,
  `armazem_atual_id` bigint(20) DEFAULT NULL,
  `data_criacao` timestamp NOT NULL DEFAULT current_timestamp(),
  `data_atualizacao` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `produto`
--

INSERT INTO `produto` (`id`, `nome`, `descricao`, `preco`, `situacao`, `cidade_fabricacao_id`, `armazem_atual_id`, `data_criacao`, `data_atualizacao`) VALUES
(5, 'Computador', 'PC Gamer', 4600.00, 'CRIADO', 174, NULL, '2025-09-12 02:55:56', '2025-09-12 02:55:56');

-- --------------------------------------------------------

--
-- Estrutura para tabela `produto_item`
--

CREATE TABLE `produto_item` (
  `id` bigint(20) NOT NULL,
  `produto_id` bigint(20) NOT NULL,
  `item_id` bigint(20) NOT NULL,
  `quantidade_necessaria` int(11) NOT NULL DEFAULT 1,
  `data_associacao` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estrutura para tabela `transporte`
--

CREATE TABLE `transporte` (
  `id` bigint(20) NOT NULL,
  `empresa_parceira_id` bigint(20) NOT NULL,
  `agente_id` bigint(20) DEFAULT NULL,
  `origem_cidade_id` bigint(20) NOT NULL,
  `destino_cidade_id` bigint(20) NOT NULL,
  `origem_armazem_id` bigint(20) DEFAULT NULL,
  `destino_armazem_id` bigint(20) DEFAULT NULL,
  `produto_id` bigint(20) DEFAULT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  `quantidade` int(11) NOT NULL DEFAULT 1,
  `situacao` varchar(50) NOT NULL,
  `data_inicio` datetime DEFAULT NULL,
  `data_fim` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `transporte`
--

INSERT INTO `transporte` (`id`, `empresa_parceira_id`, `agente_id`, `origem_cidade_id`, `destino_cidade_id`, `origem_armazem_id`, `destino_armazem_id`, `produto_id`, `item_id`, `quantidade`, `situacao`, `data_inicio`, `data_fim`) VALUES
(3, 6, 6, 174, 174, 14, 14, 5, 2, 1, 'ENTREGUE', '2025-10-10 10:30:00', '2025-10-15 10:40:00');

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `agente`
--
ALTER TABLE `agente`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cpf` (`cpf`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `empresa_parceira_id` (`empresa_parceira_id`);

--
-- Índices de tabela `armazem`
--
ALTER TABLE `armazem`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_armazem_cidade` (`cidade_id`);

--
-- Índices de tabela `armazem_produto`
--
ALTER TABLE `armazem_produto`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_armazem_produto` (`armazem_id`,`produto_id`),
  ADD KEY `produto_id` (`produto_id`);

--
-- Índices de tabela `cidade`
--
ALTER TABLE `cidade`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_cidade_pais` (`pais_id`);

--
-- Índices de tabela `empresa_parceira`
--
ALTER TABLE `empresa_parceira`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cnpj` (`cnpj`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Índices de tabela `item`
--
ALTER TABLE `item`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cidade_origem_id` (`cidade_origem_id`),
  ADD KEY `idx_item_armazem` (`armazem_atual_id`),
  ADD KEY `idx_item_produto` (`produto_id`);

--
-- Índices de tabela `pais`
--
ALTER TABLE `pais`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo` (`codigo`);

--
-- Índices de tabela `produto`
--
ALTER TABLE `produto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cidade_fabricacao_id` (`cidade_fabricacao_id`),
  ADD KEY `idx_produto_armazem` (`armazem_atual_id`);

--
-- Índices de tabela `produto_item`
--
ALTER TABLE `produto_item`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_produto_item` (`produto_id`,`item_id`),
  ADD KEY `item_id` (`item_id`);

--
-- Índices de tabela `transporte`
--
ALTER TABLE `transporte`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_transporte_empresa` (`empresa_parceira_id`),
  ADD KEY `fk_transporte_agente` (`agente_id`),
  ADD KEY `fk_transporte_origem_cidade` (`origem_cidade_id`),
  ADD KEY `fk_transporte_destino_cidade` (`destino_cidade_id`),
  ADD KEY `fk_transporte_origem_armazem` (`origem_armazem_id`),
  ADD KEY `fk_transporte_destino_armazem` (`destino_armazem_id`),
  ADD KEY `fk_transporte_produto` (`produto_id`),
  ADD KEY `fk_transporte_item` (`item_id`),
  ADD KEY `idx_transporte_situacao` (`situacao`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `agente`
--
ALTER TABLE `agente`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de tabela `armazem`
--
ALTER TABLE `armazem`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de tabela `armazem_produto`
--
ALTER TABLE `armazem_produto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `cidade`
--
ALTER TABLE `cidade`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=183;

--
-- AUTO_INCREMENT de tabela `empresa_parceira`
--
ALTER TABLE `empresa_parceira`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de tabela `item`
--
ALTER TABLE `item`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de tabela `pais`
--
ALTER TABLE `pais`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT de tabela `produto`
--
ALTER TABLE `produto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de tabela `produto_item`
--
ALTER TABLE `produto_item`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de tabela `transporte`
--
ALTER TABLE `transporte`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `agente`
--
ALTER TABLE `agente`
  ADD CONSTRAINT `agente_ibfk_1` FOREIGN KEY (`empresa_parceira_id`) REFERENCES `empresa_parceira` (`id`);

--
-- Restrições para tabelas `armazem`
--
ALTER TABLE `armazem`
  ADD CONSTRAINT `armazem_ibfk_1` FOREIGN KEY (`cidade_id`) REFERENCES `cidade` (`id`);

--
-- Restrições para tabelas `armazem_produto`
--
ALTER TABLE `armazem_produto`
  ADD CONSTRAINT `armazem_produto_ibfk_1` FOREIGN KEY (`armazem_id`) REFERENCES `armazem` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `armazem_produto_ibfk_2` FOREIGN KEY (`produto_id`) REFERENCES `produto` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `cidade`
--
ALTER TABLE `cidade`
  ADD CONSTRAINT `cidade_ibfk_1` FOREIGN KEY (`pais_id`) REFERENCES `pais` (`id`);

--
-- Restrições para tabelas `item`
--
ALTER TABLE `item`
  ADD CONSTRAINT `fk_item_produto` FOREIGN KEY (`produto_id`) REFERENCES `produto` (`id`),
  ADD CONSTRAINT `item_ibfk_1` FOREIGN KEY (`cidade_origem_id`) REFERENCES `cidade` (`id`),
  ADD CONSTRAINT `item_ibfk_2` FOREIGN KEY (`armazem_atual_id`) REFERENCES `armazem` (`id`);

--
-- Restrições para tabelas `produto`
--
ALTER TABLE `produto`
  ADD CONSTRAINT `produto_ibfk_1` FOREIGN KEY (`cidade_fabricacao_id`) REFERENCES `cidade` (`id`),
  ADD CONSTRAINT `produto_ibfk_2` FOREIGN KEY (`armazem_atual_id`) REFERENCES `armazem` (`id`);

--
-- Restrições para tabelas `produto_item`
--
ALTER TABLE `produto_item`
  ADD CONSTRAINT `produto_item_ibfk_1` FOREIGN KEY (`produto_id`) REFERENCES `produto` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `produto_item_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `transporte`
--
ALTER TABLE `transporte`
  ADD CONSTRAINT `fk_transporte_agente` FOREIGN KEY (`agente_id`) REFERENCES `agente` (`id`),
  ADD CONSTRAINT `fk_transporte_destino_armazem` FOREIGN KEY (`destino_armazem_id`) REFERENCES `armazem` (`id`),
  ADD CONSTRAINT `fk_transporte_destino_cidade` FOREIGN KEY (`destino_cidade_id`) REFERENCES `cidade` (`id`),
  ADD CONSTRAINT `fk_transporte_empresa` FOREIGN KEY (`empresa_parceira_id`) REFERENCES `empresa_parceira` (`id`),
  ADD CONSTRAINT `fk_transporte_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`),
  ADD CONSTRAINT `fk_transporte_origem_armazem` FOREIGN KEY (`origem_armazem_id`) REFERENCES `armazem` (`id`),
  ADD CONSTRAINT `fk_transporte_origem_cidade` FOREIGN KEY (`origem_cidade_id`) REFERENCES `cidade` (`id`),
  ADD CONSTRAINT `fk_transporte_produto` FOREIGN KEY (`produto_id`) REFERENCES `produto` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
