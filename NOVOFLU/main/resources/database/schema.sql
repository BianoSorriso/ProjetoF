-- Schema SQL para SCM Flusão

-- Tabela de Países
CREATE TABLE IF NOT EXISTS pais (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  codigo VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS cidade (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  estado VARCHAR(50) NOT NULL,
  cep VARCHAR(8) NULL,
  pais_id BIGINT NOT NULL,
  eh_fabrica BOOLEAN NOT NULL DEFAULT FALSE,
  eh_origem BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_cidade_pais FOREIGN KEY (pais_id) REFERENCES pais(id)
);

-- Tabela de Armazéns
CREATE TABLE IF NOT EXISTS armazem (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  endereco VARCHAR(255) NOT NULL,
  capacidade DECIMAL(15,3) NOT NULL,
  ocupacao DECIMAL(15,3) NOT NULL DEFAULT 0,
  ativo BOOLEAN NOT NULL DEFAULT TRUE,
  cidade_id BIGINT NOT NULL,
  CONSTRAINT fk_armazem_cidade FOREIGN KEY (cidade_id) REFERENCES cidade(id)
);

-- Tabela de Empresas Parceiras
CREATE TABLE IF NOT EXISTS empresa_parceira (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  cnpj VARCHAR(20) NOT NULL,
  endereco VARCHAR(255),
  telefone VARCHAR(50),
  email VARCHAR(255),
  tipo_servico VARCHAR(50) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabela de Agentes
CREATE TABLE IF NOT EXISTS agente (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  cpf VARCHAR(20) NOT NULL,
  cargo VARCHAR(100),
  data_nascimento DATE,
  telefone VARCHAR(50),
  email VARCHAR(255),
  disponivel BOOLEAN NOT NULL DEFAULT TRUE,
  empresa_parceira_id BIGINT NOT NULL,
  CONSTRAINT fk_agente_empresa FOREIGN KEY (empresa_parceira_id) REFERENCES empresa_parceira(id)
);

-- Tabela de Produtos
CREATE TABLE IF NOT EXISTS produto (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  descricao VARCHAR(1024),
  preco DECIMAL(15,2) NOT NULL,
  situacao VARCHAR(50) NOT NULL,
  cidade_fabricacao_id BIGINT NOT NULL,
  armazem_atual_id BIGINT NULL,
  data_criacao DATETIME NULL,
  data_atualizacao DATETIME NULL,
  CONSTRAINT fk_produto_cidade_fabricacao FOREIGN KEY (cidade_fabricacao_id) REFERENCES cidade(id),
  CONSTRAINT fk_produto_armazem_atual FOREIGN KEY (armazem_atual_id) REFERENCES armazem(id)
);

-- Tabela de Itens
CREATE TABLE IF NOT EXISTS item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  descricao VARCHAR(1024),
  peso DECIMAL(15,3) NOT NULL,
  quantidade INT NOT NULL,
  valor_unitario DECIMAL(15,2) NOT NULL,
  situacao VARCHAR(50) NOT NULL,
  cidade_origem_id BIGINT NOT NULL,
  armazem_atual_id BIGINT NULL,
  data_entrada DATETIME NULL,
  data_atualizacao DATETIME NULL,
  produto_id BIGINT NULL,
  CONSTRAINT fk_item_cidade_origem FOREIGN KEY (cidade_origem_id) REFERENCES cidade(id),
  CONSTRAINT fk_item_armazem_atual FOREIGN KEY (armazem_atual_id) REFERENCES armazem(id),
  CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);

-- Tabela de Transportes
CREATE TABLE IF NOT EXISTS transporte (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_parceira_id BIGINT NOT NULL,
  agente_id BIGINT NULL,
  origem_cidade_id BIGINT NOT NULL,
  destino_cidade_id BIGINT NOT NULL,
  origem_armazem_id BIGINT NULL,
  destino_armazem_id BIGINT NULL,
  produto_id BIGINT NULL,
  item_id BIGINT NULL,
  quantidade INT NOT NULL DEFAULT 1,
  situacao VARCHAR(50) NOT NULL,
  data_inicio DATETIME NULL,
  data_fim DATETIME NULL,
  CONSTRAINT fk_transporte_empresa FOREIGN KEY (empresa_parceira_id) REFERENCES empresa_parceira(id),
  CONSTRAINT fk_transporte_agente FOREIGN KEY (agente_id) REFERENCES agente(id),
  CONSTRAINT fk_transporte_origem_cidade FOREIGN KEY (origem_cidade_id) REFERENCES cidade(id),
  CONSTRAINT fk_transporte_destino_cidade FOREIGN KEY (destino_cidade_id) REFERENCES cidade(id),
  CONSTRAINT fk_transporte_origem_armazem FOREIGN KEY (origem_armazem_id) REFERENCES armazem(id),
  CONSTRAINT fk_transporte_destino_armazem FOREIGN KEY (destino_armazem_id) REFERENCES armazem(id),
  CONSTRAINT fk_transporte_produto FOREIGN KEY (produto_id) REFERENCES produto(id),
  CONSTRAINT fk_transporte_item FOREIGN KEY (item_id) REFERENCES item(id)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS idx_cidade_pais ON cidade (pais_id);
CREATE INDEX IF NOT EXISTS idx_armazem_cidade ON armazem (cidade_id);
CREATE INDEX IF NOT EXISTS idx_item_armazem ON item (armazem_atual_id);
CREATE INDEX IF NOT EXISTS idx_item_produto ON item (produto_id);
CREATE INDEX IF NOT EXISTS idx_produto_armazem ON produto (armazem_atual_id);
CREATE INDEX IF NOT EXISTS idx_transporte_situacao ON transporte (situacao);