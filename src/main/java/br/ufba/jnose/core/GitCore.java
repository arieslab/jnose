package br.ufba.jnose.core;

import br.ufba.jnose.dto.Commit;
import br.ufba.jnose.dto.Projeto;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GitCore {

    public static Projeto gitClone(String repoURL) {
        String repoName = "";
        if(repoURL.contains(".git")) {
            repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1, repoURL.lastIndexOf("."));
        }else{
            int x = repoURL.lastIndexOf("/");
            int size = repoURL.length();
            if(x == size-1){
                repoURL = repoURL.substring(0,repoURL.length()-2);
                repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1, repoURL.length());
            }else{
                repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1, repoURL.length());
            }

        }
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

        return lista;
    }

    public static void checkout(String commitId, String projetoPath) {
        try {
            Git git = Git.open(new File(projetoPath));
            git.checkout().setForced(true).setName(commitId).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pull(String projetoPath) {
        try {
            Git git = Git.open(new File(projetoPath));
            git.pull().call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
