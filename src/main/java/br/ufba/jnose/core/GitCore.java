package br.ufba.jnose.core;

import br.ufba.jnose.dto.Commit;
import br.ufba.jnose.dto.Projeto;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GitCore {

    public static Projeto gitClonee(String repoURL) {
        Projeto projeto = new Projeto("","");
        ArrayList<Commit> lista = new ArrayList<>();
        int r = 0;
        try {
            File file = new File("./projects");
            if(!file.exists()){
                file.mkdirs();
            }
            Process p = Runtime.getRuntime().exec("git clone " + repoURL, null, file);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineOut;
            while ((lineOut = input.readLine()) != null) {
                System.out.println(lineOut);
            }
            input.close();
            r = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projeto;
    }

    public static ArrayList<Commit> gitLogOneLine(String pathExecute) {
        ArrayList<Commit> lista = new ArrayList<>();
        int r = 0;
        try {
            Process p = Runtime.getRuntime().exec("git log --pretty=format:%h,%an,%ad,%s --date=iso8601", null, new File(pathExecute));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineOut;
            while ((lineOut = input.readLine()) != null) {
                String[] arrayCommit = lineOut.split(",");
                String id = arrayCommit[0];
                String name = arrayCommit[1];
                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(arrayCommit[2]);
                String msg = arrayCommit[3];
                lista.add(new Commit(id, name, date, msg));
            }
            input.close();
            r = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static ArrayList<Commit> gitTags(String pathExecute) {
        ArrayList<Commit> lista = new ArrayList<>();
        int r = 0;
        try {
            Process p = Runtime.getRuntime().exec("git tag", null, new File(pathExecute));
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lineOut;

            while ((lineOut = input.readLine()) != null) {
                String tagName = lineOut.trim();
                Process detalhes = Runtime.getRuntime().exec("git show " + tagName, null, new File(pathExecute));
                BufferedReader input2 = new BufferedReader(new InputStreamReader(detalhes.getInputStream()));
                String commit = "";
                String lineOut2;

                String id = "";
                String name = "";
                Date date = null;
                String msg = "";

                while ((lineOut2 = input2.readLine()) != null) {
                    if (lineOut2.trim().contains("Tagger:")) {
                        name = lineOut2.trim().replace("Tagger:", "").trim();
                    }
                    if (lineOut2.trim().contains("Date:")) {
                        String dateString = lineOut2.trim().replace("Date:", "").trim();
                        SimpleDateFormat formatter5 = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.US);
                        date = formatter5.parse(dateString);
                    }
                    if (lineOut2.trim().contains("commit ")) {
                        id = lineOut2.trim().replace("commit ", "").trim();
                    }
                }
                lista.add(new Commit(id, name, date, msg, tagName));
            }
            input.close();
            r = p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

}
