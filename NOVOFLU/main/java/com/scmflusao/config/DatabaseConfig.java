package com.scmflusao.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static String URL;
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static boolean initialized = false;

    static {
        try {
            // Definir caminho do arquivo do banco (H2 embarcado)
            // Forçado para um único caminho para evitar múltiplos bancos
            String absoluteDbPath = "C:\\Users\\User\\OneDrive\\Desktop\\TENTARSEMSQL02\\NOVOFLU\\data\\scmflusao";
            File dbFile = new File(absoluteDbPath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            URL = "jdbc:h2:file:" + dbFile.getAbsolutePath() +
                  ";MODE=MySQL;DATABASE_TO_UPPER=false;AUTO_SERVER=TRUE";
            System.out.println("[DatabaseConfig] Banco único forçado em: " + dbFile.getAbsolutePath());

            // Carregar o driver H2
            Class.forName("org.h2.Driver");

            // Inicializar o banco de dados se ainda não foi inicializado
            if (!initialized) {
                initializeDatabase();
                initialized = true;
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Erro ao carregar o driver do H2: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar o driver do H2", e);
        } catch (Exception e) {
            System.err.println("Erro ao inicializar o banco de dados: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao inicializar o banco de dados", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }

    private static void initializeDatabase() {
        Connection conn = null;
        Statement stmt = null;

        try {
            // Conecta ao banco H2 (será criado automaticamente se não existir)
            conn = getConnection();
            stmt = conn.createStatement();

            // Executa o script de criação de tabelas
            executeScript(conn, "main/resources/database/schema.sql");
            // Executa extensão de schema para associação produto_item
            executeScript(conn, "main/resources/database/schema_produto_item.sql");

            // Verifica se há dados nas tabelas
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM pais");
            rs.next();
            int count = rs.getInt(1);

            // Se não houver dados, tenta importar dump antigo do MySQL (phpMyAdmin)
            if (count == 0) {
                String dumpPath = resolveExistingPath("BD.PHPMYADMIN.sql");
                if (dumpPath != null) {
                    System.out.println("Importando dump MySQL: " + dumpPath);
                    try (Statement s = conn.createStatement()) {
                        // Desativa integridade referencial para permitir inserções fora de ordem
                        s.execute("SET REFERENTIAL_INTEGRITY FALSE");
                    }
                    // Importa apenas dados (ignora CREATE/ALTER/DROP de tabelas do dump)
                    executeScript(conn, dumpPath, true);
                    try (Statement s = conn.createStatement()) {
                        s.execute("SET REFERENTIAL_INTEGRITY TRUE");
                    }
                } else {
                    executeScript(conn, "main/resources/populate_brasil.sql");
                    System.out.println("Banco de dados inicializado com dados de exemplo.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(conn);
        }
    }

    private static String resolveExistingPath(String scriptPath) {
        String[] candidates = new String[] {
            scriptPath,
            "NOVOFLU/" + scriptPath,
            "../" + scriptPath,
            "../../" + scriptPath,
            "../../../" + scriptPath
        };
        for (String p : candidates) {
            File f = new File(p);
            if (f.exists()) return f.getPath();
        }
        return null;
    }

    // Mantém compatibilidade para scripts regulares (schema/populate)
    private static void executeScript(Connection conn, String scriptPath) {
        executeScript(conn, scriptPath, false);
    }

    // Parser executa o script .sql; quando isMySqlDump=true, ignora comandos de DDL do MySQL
    private static void executeScript(Connection conn, String scriptPath, boolean isMySqlDump) {
        try {
            String resolved = resolveExistingPath(scriptPath);
            File scriptFile = resolved != null ? new File(resolved) : new File(scriptPath);
            if (!scriptFile.exists()) {
                System.err.println("Arquivo de script não encontrado: " + scriptPath);
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
            StringBuilder sb = new StringBuilder();
            String line;
            boolean inBlockComment = false;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();

                // Controle de comentários de bloco
                if (inBlockComment) {
                    if (trimmed.contains("*/")) {
                        inBlockComment = false;
                    }
                    continue;
                }
                if (trimmed.startsWith("/*")) {
                    if (!trimmed.contains("*/")) {
                        inBlockComment = true;
                        continue;
                    } else {
                        // comentário de bloco numa linha só
                        continue;
                    }
                }

                // Ignora comentários de linha e linhas vazias
                if (trimmed.isEmpty() || trimmed.startsWith("--")) {
                    continue;
                }

                // Ignora comandos específicos do dump MySQL
                if (trimmed.startsWith("LOCK TABLES") ||
                    trimmed.startsWith("UNLOCK TABLES") ||
                    trimmed.startsWith("DELIMITER") ||
                    trimmed.startsWith("CREATE DATABASE") ||
                    trimmed.startsWith("DROP DATABASE") ||
                    trimmed.startsWith("USE ") ||
                    trimmed.startsWith("SET ") ||
                    trimmed.startsWith("START TRANSACTION") ||
                    trimmed.startsWith("COMMIT") ||
                    trimmed.startsWith("ROLLBACK") ||
                    trimmed.startsWith("/*!")) {
                    continue;
                }

                sb.append(line).append(' ');

                // Se a linha terminar com ponto e vírgula, executa o comando
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString();
                    // Normalizações simples para compatibilidade com H2
                    sql = sql.replace('`', ' '); // remove backticks
                    sql = sql.replaceAll("(?i)ENGINE=\\w+", "");
                    sql = sql.replaceAll("(?i)DEFAULT\\s+CHARSET=\\w+", "");
                    sql = sql.replaceAll("(?i)COLLATE=\\w+", "");
                    sql = sql.replaceAll("(?i)current_timestamp\\(\\)", "CURRENT_TIMESTAMP");
                    // Converte ENUM para VARCHAR(50) quando aparecer em CREATE TABLE
                    sql = sql.replaceAll("(?i)ENUM\\s*\\([^)]*\\)", "VARCHAR(50)");
                    // Normaliza INSERT IGNORE para INSERT
                    sql = sql.replaceAll("(?i)INSERT\\s+IGNORE\\s+INTO", "INSERT INTO");

                    // Se for dump MySQL, ignorar DDL que colide com schema já criado
                    boolean shouldExecute = true;
                    String sqlTrimUpper = sql.trim().toUpperCase();
                    if (isMySqlDump) {
                        if (sqlTrimUpper.startsWith("CREATE TABLE") ||
                            sqlTrimUpper.startsWith("ALTER TABLE") ||
                            sqlTrimUpper.startsWith("DROP TABLE") ||
                            sqlTrimUpper.startsWith("CREATE INDEX") ||
                            sqlTrimUpper.startsWith("ALTER DATABASE") ||
                            sqlTrimUpper.startsWith("DROP INDEX")) {
                            shouldExecute = false; // já temos o schema em H2
                        }
                    }
                    if (shouldExecute) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sql);
                        } catch (SQLException e) {
                            // Silencia erros esperados de "já existe" em modo dump
                            String msg = e.getMessage() != null ? e.getMessage() : "";
                            boolean alreadyExists = msg.toLowerCase().contains("already exists");
                            if (isMySqlDump && alreadyExists) {
                                // não polui o log; segue com o próximo comando
                            } else {
                                System.err.println("Erro ao executar SQL: " + sql);
                                System.err.println("Mensagem de erro: " + e.getMessage());
                            }
                        }
                    }
                    sb = new StringBuilder();
                }
            }

            reader.close();

        } catch (IOException e) {
            System.err.println("Erro de IO ao ler script: " + e.getMessage());
            e.printStackTrace();
        }
    }
}