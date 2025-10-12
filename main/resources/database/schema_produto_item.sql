-- Associação Produto-Item com quantidade por produto
CREATE TABLE IF NOT EXISTS produto_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  produto_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL,
  quantidade INT NOT NULL,
  CONSTRAINT fk_produto_item_produto FOREIGN KEY (produto_id) REFERENCES produto(id),
  CONSTRAINT fk_produto_item_item FOREIGN KEY (item_id) REFERENCES item(id),
  CONSTRAINT uq_produto_item UNIQUE (produto_id, item_id)
);

-- Índices auxiliares
CREATE INDEX IF NOT EXISTS idx_produto_item_produto ON produto_item (produto_id);
CREATE INDEX IF NOT EXISTS idx_produto_item_item ON produto_item (item_id);