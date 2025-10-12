-- Verificação de produto_item para produto 25
SELECT produto_id, item_id, quantidade FROM produto_item WHERE produto_id = 25 ORDER BY item_id;

-- Conferir colunas existentes na tabela produto_item
SELECT table_name, column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE UPPER(table_name) = 'PRODUTO_ITEM' ORDER BY column_name;