-- Script para popular o banco de dados com países e cidades do Brasil

-- Inserir o país Brasil se não existir
INSERT INTO pais (nome, codigo) VALUES ('Brasil', 'BR') ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id);

-- Usar subconsulta para obter o ID do país Brasil

-- Inserir cidades do Brasil por estado
-- Acre (AC)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Rio Branco', 'AC', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Cruzeiro do Sul', 'AC', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Alagoas (AL)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Maceió', 'AL', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Arapiraca', 'AL', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Amapá (AP)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Macapá', 'AP', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Santana', 'AP', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Amazonas (AM)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Manaus', 'AM', (SELECT id FROM pais WHERE codigo = 'BR'), true, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Parintins', 'AM', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Bahia (BA)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Salvador', 'BA', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Feira de Santana', 'BA', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Ceará (CE)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Fortaleza', 'CE', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Juazeiro do Norte', 'CE', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Distrito Federal (DF)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Brasília', 'DF', (SELECT id FROM pais WHERE codigo = 'BR'), false, true) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Espírito Santo (ES)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Vitória', 'ES', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Vila Velha', 'ES', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Goiás (GO)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Goiânia', 'GO', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Maranhão (MA)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('São Luís', 'MA', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Imperatriz', 'MA', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Mato Grosso (MT)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Cuiabá', 'MT', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Várzea Grande', 'MT', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Mato Grosso do Sul (MS)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Campo Grande', 'MS', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Dourados', 'MS', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Minas Gerais (MG)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Belo Horizonte', 'MG', (SELECT id FROM pais WHERE codigo = 'BR'), true, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Uberlândia', 'MG', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Pará (PA)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Belém', 'PA', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Santarém', 'PA', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Paraíba (PB)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('João Pessoa', 'PB', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Campina Grande', 'PB', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Paraná (PR)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Curitiba', 'PR', (SELECT id FROM pais WHERE codigo = 'BR'), true, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Londrina', 'PR', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Pernambuco (PE)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Recife', 'PE', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Jaboatão dos Guararapes', 'PE', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Piauí (PI)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Parnaíba', 'PI', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Rio de Janeiro (RJ)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Rio de Janeiro', 'RJ', (SELECT id FROM pais WHERE codigo = 'BR'), true, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('São Gonçalo', 'RJ', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Duque de Caxias', 'RJ', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Rio Grande do Norte (RN)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Natal', 'RN', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Mossoró', 'RN', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Rio Grande do Sul (RS)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Porto Alegre', 'RS', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Caxias do Sul', 'RS', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Rondônia (RO)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Ji-Paraná', 'RO', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Roraima (RR)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Boa Vista', 'RR', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Santa Catarina (SC)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Florianópolis', 'SC', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Joinville', 'SC', (SELECT id FROM pais WHERE codigo = 'BR'), true, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- São Paulo (SP)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('São Paulo', 'SP', (SELECT id FROM pais WHERE codigo = 'BR'), true, true) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Guarulhos', 'SP', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Campinas', 'SP', (SELECT id FROM pais WHERE codigo = 'BR'), true, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('São Bernardo do Campo', 'SP', (SELECT id FROM pais WHERE codigo = 'BR'), true, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Sergipe (SE)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Nossa Senhora do Socorro', 'SE', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Tocantins (TO)
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Palmas', 'TO', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO cidade (nome, estado, pais_id, eh_fabrica, eh_origem) VALUES ('Araguaína', 'TO', (SELECT id FROM pais WHERE codigo = 'BR'), false, false) ON DUPLICATE KEY UPDATE nome=VALUES(nome);