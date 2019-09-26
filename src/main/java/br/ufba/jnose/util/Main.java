//package br.ufba.jnose.util;
//
//import br.ufba.jnose.cobertura.ReportGenerator;
//
//import static com.google.common.collect.Lists.newArrayList;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.*;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.List;
//
//import static java.lang.System.out;
//
//public class Main {
//
//    private static int numeroPastas = 0;
//    public static int pastalAtual = 0;
//    public static TextAreaLogProgram textAreaLogProgram;
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                textAreaLogProgram = new TextAreaLogProgram();
//                textAreaLogProgram.setVisible(true);
//            }
//        });
//    }
//
////    private static String time;
//
//    public static void start(PrintStream printStream) {
//
//        String path = openWindows(null);
//        File[] directories = new File(path).listFiles(File::isDirectory);
//        List<String> listaPastas = new ArrayList<String>();
//
//        for (File dir : directories) {
//            String pathPom = dir.getAbsolutePath() + "/pom.xml";
//            if (new File(pathPom).exists()) {
//                out.println("Processando: " + dir.getAbsolutePath());
//                listaPastas.add(dir.getAbsolutePath());
//            } else {
//                out.println("Não é um projeto MAVEN: " + dir.getAbsolutePath());
//            }
//        }
//
//        numeroPastas = listaPastas.size();
//        textAreaLogProgram.progressBar.setMaximum(numeroPastas * 4);
//
//        String currentPath = Paths.get("").toAbsolutePath().toString();
//        out.println("Current relative path is: " + currentPath);
//
//        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
//        String reportPath = currentPath + "/reports/" + time + "/";//File.pathSeparator
//        new File(reportPath).mkdirs();
//
//        listaPastas.forEach(pathPasta -> runFile(pathPasta, false, printStream, reportPath));
//
//        mesclarGeral(reportPath);
//    }
//
//    private static void runFile(String pathProject, Boolean window, PrintStream printStream, String reportPath) {
//        try {
//            String projectName = pathProject.substring(pathProject.lastIndexOf("/") + 1);
//            //para a mesclagem geral
//            listaProjetos.add(projectName);
//
//            textAreaLogProgram.textArea.setForeground(Color.LIGHT_GRAY);
//            execCommand("mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Drat.skip=true", pathProject, printStream);
//            ReportGenerator reportGenerator = new ReportGenerator(new File(pathProject), new File(reportPath));
//            reportGenerator.create();
//            textAreaLogProgram.progressBar.setValue(++br.ufba.jnose.util.Main.pastalAtual);
//
//            String testfiledetector_csv = br.ufba.jnose.testfiledetector.Main.start(pathProject, projectName, reportPath);
//            textAreaLogProgram.progressBar.setValue(++br.ufba.jnose.util.Main.pastalAtual);
//
//            String testfilemapping_csv = br.ufba.jnose.testfilemapping.Main.start(testfiledetector_csv, projectName, pathProject, reportPath);
//            textAreaLogProgram.progressBar.setValue(++br.ufba.jnose.util.Main.pastalAtual);
//
//            br.ufba.jnose.testsmelldetector.Main.start(testfilemapping_csv, reportPath, projectName);
//            textAreaLogProgram.progressBar.setValue(++br.ufba.jnose.util.Main.pastalAtual);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static List<String> listaProjetos = new ArrayList<String>();
//
//    private static void mesclarGeral(String reportPath) {
//        try {
//            ResultsWriter resultsWriter = ResultsWriter.createResultsWriter(reportPath + "all" + "_testsmesll.csv");
//            resultsWriter.writeColumnName(br.ufba.jnose.testsmelldetector.Main.columnNames);
//
//            if (listaProjetos.size() != 0) {
//                for (String projeto : listaProjetos) {
//
//                    File jacocoFile = new File(reportPath + projeto + "_testsmesll.csv");
//                    FileReader jacocoFileReader = new FileReader(jacocoFile);
//                    BufferedReader jacocoIn = new BufferedReader(jacocoFileReader);
//
//                    boolean pularLinha = false;
//                    String str;
//                    while ((str = jacocoIn.readLine()) != null) {
//                        if (pularLinha) {
//                            resultsWriter.writeLine(newArrayList(str.split(",")));
//                        } else {
//                            pularLinha = true;
//                        }
//                    }
//                    jacocoIn.close();
//                    jacocoFileReader.close();
////                    jacocoFile.deleteOnExit();
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private static void execCommand(final String commandLine, String pathExecute, PrintStream printStream) {
//        int r = 0;
//        System.setOut(printStream);
//        try {
//            Process p = Runtime.getRuntime().exec(commandLine, null, new File(pathExecute));
//            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String lineOut;
//            while ((lineOut = input.readLine()) != null) {
//                System.out.println(lineOut);
//            }
//            input.close();
//            r = p.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private static String openWindows(String absolutePath) {
//        File selectedFile = null;
//
//        if (absolutePath == null) {
//            JFileChooser chooser = new JFileChooser();
//            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
//            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//            chooser.setAcceptAllFileFilterUsed(false);
//            int returnVal = chooser.showOpenDialog(null);
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
//                selectedFile = chooser.getSelectedFile();
//            }
//            chooser.setDragEnabled(false);
//            chooser.setMultiSelectionEnabled(false);
//            chooser.setFileHidingEnabled(false);
//
//            return selectedFile.getAbsolutePath();
//        } else {
//            return absolutePath;
//        }
//    }
//
//}
