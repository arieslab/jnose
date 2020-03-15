package br.ufba.jnose.core.testfiledetector;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileWalker {
    private List<Path> files;

    public List<Path> getJavaTestFiles(String directoryPath, boolean recursive) throws IOException {
        files = new ArrayList<>();
        Path startDir = Paths.get(directoryPath);

        if (recursive) {
            Files.walkFileTree(startDir, new FindJavaTestFilesVisitor());
        } else {
            Files.walk(startDir, 1)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        if(filePath.getFileName().toString().lastIndexOf(".")!=-1) {
                            String fileNameWithoutExtension = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().lastIndexOf("."));
                            //test files should have 'test(s)' as either a prefix or suffix
                            if (filePath.toString().toLowerCase().endsWith(".java") &&
                                    (
                                            fileNameWithoutExtension.toLowerCase().startsWith("test") ||
                                            fileNameWithoutExtension.toLowerCase().startsWith("tests") ||
                                            fileNameWithoutExtension.toLowerCase().endsWith("test") ||
                                            fileNameWithoutExtension.toLowerCase().endsWith("tests") ||
                                            fileNameWithoutExtension.toLowerCase().endsWith("testcase") ||
                                            fileNameWithoutExtension.toLowerCase().matches("^.*test\\d*$")
                                    )) {
                                files.add(filePath);
                            }
                        }
                    });
        }
        return files;
    }

    public List<Path> getJavaFiles(String directoryPath, boolean recursive) throws IOException {
        files = new ArrayList<>();
        Path startDir = Paths.get(directoryPath);

        if (recursive) {
            Files.walkFileTree(startDir, new FindJavaFilesVisitor());
        } else {
            Files.walk(startDir, 1)
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        if (filePath.toString().toLowerCase().endsWith(".java")) {
                            files.add(filePath);
                        }
                    });
        }
        return files;
    }

    private class FindJavaFilesVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.toString().toLowerCase().endsWith(".java")) {
                files.add(file);
            }
            return FileVisitResult.CONTINUE;
        }
    }

    public class FindJavaTestFilesVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
            if (file.getFileName().toString().lastIndexOf(".") !=-1) {
                String fileNameWithoutExtension = file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf("."));
                //test files should have 'test(s)' as either a prefix or suffix
                if (file.toString().toLowerCase().endsWith(".java") &&
                        (
                                fileNameWithoutExtension.toLowerCase().startsWith("test") ||
                                fileNameWithoutExtension.toLowerCase().startsWith("tests") ||
                                fileNameWithoutExtension.toLowerCase().endsWith("test") ||
                                fileNameWithoutExtension.toLowerCase().endsWith("tests") ||
                                fileNameWithoutExtension.toLowerCase().endsWith("testcase") ||
                                fileNameWithoutExtension.toLowerCase().matches("^.*test\\d*$")
                        )) {
                    files.add(file);
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }
}


