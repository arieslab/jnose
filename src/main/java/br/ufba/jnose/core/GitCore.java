package br.ufba.jnose.core;

import br.ufba.jnose.dto.Commit;
import br.ufba.jnose.dto.Projeto;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

public class GitCore {

    public static Projeto gitClone(String repoURL) {
        String repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1, repoURL.lastIndexOf("."));
        try {
            File file = new File("./projects/" + repoName);
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
            Git git = Git.cloneRepository()
                    .setURI(repoURL)
                    .setDirectory(file)
                    .call();
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
        Projeto projeto = new Projeto(repoName, "");
        return projeto;
    }

    public static ArrayList<Commit> gitLogOneLine(String pathExecute) {

        ArrayList<Commit> lista = new ArrayList<>();
        try {
            Git git = Git.open(new File(pathExecute));
            git.log().all().call().forEach(revCommit -> {
                        PersonIdent authorIdent = revCommit.getAuthorIdent();
                        Date authorDate = authorIdent.getWhen();
                        TimeZone authorTimeZone = authorIdent.getTimeZone();

                        lista.add(new Commit(
                                revCommit.getId().getName(),
                                authorIdent.getName(),
                                authorDate,
                                revCommit.getFullMessage()));
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


//        ArrayList<Commit> lista = new ArrayList<>();
//        int r = 0;
//        try {
//            Process p = Runtime.getRuntime().exec("git log --pretty=format:%h,%an,%ad,%s --date=iso8601", null, new File(pathExecute));
//            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String lineOut;
//            while ((lineOut = input.readLine()) != null) {
//                String[] arrayCommit = lineOut.split(",");
//                String id = arrayCommit[0];
//                String name = arrayCommit[1];
//                Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(arrayCommit[2]);
//                String msg = arrayCommit[3];
//                lista.add(new Commit(id, name, date, msg));
//            }
//            input.close();
//            r = p.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return lista;
    }

    public static ArrayList<Commit> gitTags(String pathExecute) {

        ArrayList<Commit> lista = new ArrayList<>();

        try {
            Git git = Git.open(new File(pathExecute));

            List<Ref> call = git.tagList().call();
            for (Ref ref : call) {
                System.out.println("Tag: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());

                LogCommand log = git.log();

                Ref peeledRef = git.getRepository().peel(ref);
                if (peeledRef.getPeeledObjectId() != null) {
                    log.add(peeledRef.getPeeledObjectId());
                } else {
                    log.add(ref.getObjectId());
                }

                Iterable<RevCommit> logs = log.call();
                for (RevCommit revCommit : logs) {
                    PersonIdent authorIdent = revCommit.getAuthorIdent();
                    Date authorDate = authorIdent.getWhen();
                    TimeZone authorTimeZone = authorIdent.getTimeZone();
                    System.out.println(authorDate + " - Commit: " + revCommit + ", name: " + revCommit.getName() + ", id: " + revCommit.getId().getName());

                    lista.add(new Commit(
                            revCommit.getId().getName(),
                            authorIdent.getName(),
                            authorDate,
                            revCommit.getFullMessage(),
                            ref.getName()));
                    break;
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

//        int r = 0;
//        try {
//            Process p = Runtime.getRuntime().exec("git tag", null, new File(pathExecute));
//            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String lineOut;
//
//            while ((lineOut = input.readLine()) != null) {
//                String tagName = lineOut.trim();
//                Process detalhes = Runtime.getRuntime().exec("git show " + tagName, null, new File(pathExecute));
//                BufferedReader input2 = new BufferedReader(new InputStreamReader(detalhes.getInputStream()));
//                String commit = "";
//                String lineOut2;
//
//                String id = "";
//                String name = "";
//                Date date = null;
//                String msg = "";
//
//                while ((lineOut2 = input2.readLine()) != null) {
//                    if (lineOut2.trim().contains("Tagger:")) {
//                        name = lineOut2.trim().replace("Tagger:", "").trim();
//                    }
//                    if (lineOut2.trim().contains("Date:")) {
//                        String dateString = lineOut2.trim().replace("Date:", "").trim();
//                        SimpleDateFormat formatter5 = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.US);
//                        date = formatter5.parse(dateString);
//                    }
//                    if (lineOut2.trim().contains("commit ")) {
//                        id = lineOut2.trim().replace("commit ", "").trim();
//                    }
//                }
//                lista.add(new Commit(id, name, date, msg, tagName));
//            }
//            input.close();
//            r = p.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return lista;
    }

    public static void checkout(String commitId, String projetoPath) {

        try {
            Git git = Git.open(new File(projetoPath));
            git.checkout().setForced(true).setName(commitId).call();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        JNoseCore.execCommand("git checkout " + commitId, projetoPath);
    }

}
