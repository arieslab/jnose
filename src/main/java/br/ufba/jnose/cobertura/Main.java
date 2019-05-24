package br.ufba.jnose.cobertura;

import br.ufba.jnose.util.ResultsWriter;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        start(null);
    }

    public static void start(String csvPath) throws Exception {
        File selectedFile = null;

        if (csvPath == null) {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "CSV", "csv");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getName());
                selectedFile = chooser.getSelectedFile();
            }
        } else {
            selectedFile = new File(csvPath);
        }

        BufferedReader in = new BufferedReader(new FileReader(selectedFile));
        String str;

        String[] lineItem;

        ResultsWriter resultsWriter = ResultsWriter.createResultsWriter("ClassInfor.csv");
        resultsWriter.writeLine(Arrays.asList("name","id","instructions","branches","lines","methods","complexity"));

        while ((str = in.readLine()) != null) {
            lineItem = str.split(",");
            ClassInfo classInfo = new ClassInfo(System.out);
            String file = lineItem[1].replace("/src/test/java/", "/target/test-classes/").replace(".java", ".class");
            String[] linha = classInfo.execute(file);
            resultsWriter.writeLine(Arrays.asList(linha));

//            CoreTutorial coreTutorial = new CoreTutorial(System.out);
//            coreTutorial.execute(lista[0]);
        }
        System.out.println("Completed!");

    }
}