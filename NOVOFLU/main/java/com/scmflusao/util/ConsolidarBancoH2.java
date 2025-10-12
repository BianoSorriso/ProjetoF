package com.scmflusao.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utilitário para garantir um único banco H2 na aplicação.
 * - Caminho alvo: NOVOFLU/data/scmflusao.mv.db
 * - Lista cópias duplicadas e, se o arquivo alvo não existir,
 *   copia a cópia mais recente para o caminho padrão.
 */
public class ConsolidarBancoH2 {
    private static final String TARGET_BASE =
            "C:\\Users\\User\\OneDrive\\Desktop\\TENTARSEMSQL02\\NOVOFLU\\data\\scmflusao";
    private static final String TARGET_DB_FILE = TARGET_BASE + ".mv.db";

    public static void main(String[] args) throws Exception {
        File target = new File(TARGET_DB_FILE);
        ensureParent(target);

        System.out.println("[ConsolidarBancoH2] Alvo único: " + target.getAbsolutePath());

        List<File> duplicates = findDuplicates("scmflusao.mv.db", 4);
        System.out.println("[ConsolidarBancoH2] Encontrados " + duplicates.size() + " arquivos com nome 'scmflusao.mv.db':");
        for (File f : duplicates) {
            System.out.println("  - " + f.getAbsolutePath() + " (" + f.length() + " bytes)");
        }

        if (target.exists()) {
            System.out.println("[ConsolidarBancoH2] OK: alvo já existe. Nenhuma cópia necessária.");
        } else {
            if (duplicates.isEmpty()) {
                System.out.println("[ConsolidarBancoH2] Nenhuma fonte para copiar encontrada.");
            } else {
                File newest = duplicates.stream()
                        .max(Comparator.comparingLong(File::lastModified))
                        .orElse(duplicates.get(0));
                System.out.println("[ConsolidarBancoH2] Copiando cópia mais recente para o alvo: " + newest.getAbsolutePath());
                copyFile(newest, target);
                System.out.println("[ConsolidarBancoH2] Concluído. Banco único consolidado.");
            }
        }

        System.out.println("[ConsolidarBancoH2] FINALIZADO.");
    }

    private static void ensureParent(File target) {
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
    }

    private static void copyFile(File src, File dst) throws IOException {
        try (FileInputStream in = new FileInputStream(src);
             FileOutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
        }
    }

    private static List<File> findDuplicates(String fileName, int maxDepth) throws IOException {
        List<File> matches = new ArrayList<>();
        Path root = Paths.get("").toAbsolutePath();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            int depth = 0;
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                depth = root.relativize(dir).getNameCount();
                return depth > maxDepth ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.getFileName().toString().equalsIgnoreCase(fileName)) {
                    matches.add(file.toFile());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return matches;
    }
}