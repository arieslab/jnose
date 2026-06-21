package br.ufba.jnose.base;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.dtolocal.Commit;
import br.ufba.jnose.dtolocal.ProjetoDTO;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitCore {

    private static final Logger LOGGER = Logger.getLogger(GitCore.class.getName());

    public static Integer getStarts(String repoLocal){

        String url = getURL(repoLocal);
        String repoName = getNameByGithub(url);

        Integer stars = 0;
        try {
            GitHub github = GitHub.connectAnonymously();
            GHRepository repo = github.getRepository(repoName);
            stars = repo.getStargazersCount();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to get stars for: " + repoLocal, e);
        }
        return stars;
    }


    private static String getNameByGithub(String path_) {
        String owner_ = "";

        path_ = path_.replace(".git","");

        if(path_.endsWith("/")){
            path_ = path_.substring(0,path_.length()-1);
        }

        String projeto_ = path_.substring(path_.lastIndexOf("/")+1);
        owner_ = path_.substring(0,path_.lastIndexOf("/"));
        owner_ = owner_.substring(owner_.lastIndexOf("/")+1);

        return owner_ + "/" + projeto_;
    }

    public static ProjetoDTO gitClone(String repoURL) {
        String repoName = "";
        if (repoURL.contains(".git")) {
            repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1, repoURL.lastIndexOf("."));
        } else {
            int x = repoURL.lastIndexOf("/");
            int size = repoURL.length();
            if (x == size - 1) {
                repoURL = repoURL.substring(0, repoURL.length() - 2);
                repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1);
            } else {
                repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1);
            }

        }
        File file = null;
        try {
            file = new File(WicketApplication.JNOSE_PROJECTS_FOLDER + repoName);
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
            Git git = Git.cloneRepository()
                    .setURI(repoURL)
                    .setDirectory(file)
                    .call();
        } catch (GitAPIException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to clone: " + repoURL, e);
        }

        br.ufba.jnose.entities.Projeto projetoBean = new br.ufba.jnose.entities.Projeto();
        projetoBean.setName(repoName);
        projetoBean.setPath(file.getPath());
        ProjetoDTO projeto = new ProjetoDTO(projetoBean);

        return projeto;
    }

    public static ArrayList<Commit> gitLogOneLine(String pathExecute) {

        ArrayList<Commit> lista = new ArrayList<>();
        try {
            Git git = Git.open(new File(pathExecute));
            git.log().all().call().forEach(revCommit -> {
                        PersonIdent authorIdent = revCommit.getAuthorIdent();
                        Date authorDate = authorIdent.getWhen();

                        lista.add(new Commit(
                                revCommit.getId().getName(),
                                authorIdent.getName(),
                                authorDate,
                                revCommit.getFullMessage()));
                    }
            );
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get git log for: " + pathExecute, e);
        }

        return lista;
    }

    public static ArrayList<Commit> getLastCommit(String pathProject) {

        ArrayList<Commit> lista = new ArrayList<>();
        try {
            Git git = Git.open(new File(pathProject));
            git.log().all().call().forEach(revCommit -> {
                        PersonIdent authorIdent = revCommit.getAuthorIdent();
                        Date authorDate = authorIdent.getWhen();

                        lista.add(new Commit(
                                revCommit.getId().getName(),
                                authorIdent.getName(),
                                authorDate,
                                revCommit.getFullMessage()));
                    }
            );
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get last commit for: " + pathProject, e);
        }

        return lista;
    }

    public static ArrayList<Commit> gitTags(String pathExecute) {

        ArrayList<Commit> lista = new ArrayList<>();

        try {
            Git git = Git.open(new File(pathExecute));

            List<Ref> call = git.tagList().call();
            for (Ref ref : call) {
                LOGGER.log(Level.FINE, "Tag: {0} {1} {2}", new Object[]{ref, ref.getName(), ref.getObjectId().getName()});

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
                    LOGGER.log(Level.FINE, "{0} - Commit: {1}, name: {2}, id: {3}",
                            new Object[]{authorDate, revCommit, revCommit.getName(), revCommit.getId().getName()});

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
            LOGGER.log(Level.WARNING, "Failed to get tags for: " + pathExecute, e);
        }

        return lista;
    }

    public static void checkout(String commitId, String projetoPath) {
        try {
            Git git = Git.open(new File(projetoPath));
            git.checkout().setForced(true).setName(commitId).call();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to checkout {0} in {1}", new Object[]{commitId, projetoPath});
            LOGGER.log(Level.FINE, "Checkout error", e);
        }
    }

    public static String getURL(String projetoPath) {
        String url = "";
        try {
            Git git = Git.open(new File(projetoPath));
            url = git.getRepository().getConfig().getString("remote", "origin", "url");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get URL for: " + projetoPath, e);
        }
        return url;
    }

    public static String branch(String projetoPath) {
        String branchcurrent = "";
        try {
            Git git = Git.open(new File(projetoPath));
            branchcurrent = git.getRepository().getBranch();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to get branch for: " + projetoPath, e);
        }
        return branchcurrent;
    }

    public static void pull(String projetoPath) {
        try {
            Git git = Git.open(new File(projetoPath));
            git.pull().call();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to pull in: " + projetoPath, e);
        }
    }

    public static Map<Integer,String> blame(String projetoPath, String filePathAbsolut) {

        Map<Integer,String> retorno = new HashMap<>();

        String filePathRepo = filePathAbsolut.replace(projetoPath+"/","");

        try {
            final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");

            Git git = Git.open(new File(projetoPath));
            BlameCommand blameCommand = git.blame();
            blameCommand.setStartCommit(git.getRepository().resolve("HEAD"));
            blameCommand.setFilePath(filePathRepo);
            BlameResult result = blameCommand.call();

            final RawText rawText = result.getResultContents();

            for (int i = 0; i < rawText.size(); i++) {
                final PersonIdent sourceAuthor = result.getSourceAuthor(i);
                final RevCommit sourceCommit = result.getSourceCommit(i);
                LOGGER.log(Level.FINE, "{0} - {1} - {2}: {3}",
                        new Object[]{sourceAuthor.getName(),
                                sourceCommit != null ? DATE_FORMAT.format(((long) sourceCommit.getCommitTime()) * 1000) : "",
                                sourceCommit != null ? sourceCommit.getName() : "",
                                rawText.getString(i)});

                retorno.put(i+1, sourceAuthor.getName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to blame file: " + filePathAbsolut, e);
        }

        return retorno;
    }

    public static BlameResult getBlameResultForFile(String projetoPath, String filePath) {
        BlameResult blame = null;
        try {
            Git git = Git.open(new File(projetoPath));
            Iterable<RevCommit> lista = git.log().all().call();
            Repository jgitRepository = git.getRepository();
            BlameCommand blamer = new BlameCommand(jgitRepository);
            ObjectId commitID = jgitRepository.resolve("HEAD");
            blamer.setStartCommit(commitID);
            filePath = filePath.replace(projetoPath+"/","");
            blamer.setFilePath(filePath);
            blame = blamer.call();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to blame file: " + filePath, e);
        }
        return blame;
    }

}
