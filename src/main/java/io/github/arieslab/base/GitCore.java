package io.github.arieslab.base;

import io.github.arieslab.WicketApplication;
import io.github.arieslab.dtolocal.Commit;
import io.github.arieslab.dtolocal.ProjetoDTO;
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

    /**
     * Fetches the stargazer count from GitHub for the project at the given local repository path.
     *
     * @param repoLocal the local repository path
     * @return the number of stars, or 0 if unavailable
     */
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


    /**
     * Extracts the GitHub owner/repo name from a git remote URL.
     *
     * @param path_ the git remote URL
     * @return the repository name in "owner/repo" format
     */
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

    /**
     * Clones a git repository from the given URL into the JNose projects folder.
     *
     * @param repoURL the repository URL to clone
     * @return a ProjetoDTO representing the cloned project
     */
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

        io.github.arieslab.entities.Projeto projetoBean = new io.github.arieslab.entities.Projeto();
        projetoBean.setName(repoName);
        projetoBean.setPath(file.getPath());
        ProjetoDTO projeto = new ProjetoDTO(projetoBean);

        return projeto;
    }

    /**
     * Retrieves the last 500 commits from the git log.
     *
     * @param pathExecute the repository path
     * @return list of commits
     */
    public static ArrayList<Commit> gitLogOneLine(String pathExecute) {
        return gitLogOneLine(pathExecute, 500);
    }

    /**
     * Retrieves up to maxCount commits from the git log.
     *
     * @param pathExecute the repository path
     * @param maxCount maximum number of commits to retrieve
     * @return list of commits
     */
    public static ArrayList<Commit> gitLogOneLine(String pathExecute, int maxCount) {

        ArrayList<Commit> lista = new ArrayList<>();
        try {
            Git git = Git.open(new File(pathExecute));
            git.log().all().setMaxCount(maxCount).call().forEach(revCommit -> {
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

    /**
     * Retrieves all commits in the repository.
     *
     * @param pathProject the repository path
     * @return list of all commits
     */
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

    /**
     * Retrieves all git tags with their associated commit information.
     *
     * @param pathExecute the repository path
     * @return list of commits with tag information
     */
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

    /**
     * Performs a git checkout to the specified commit ID or branch name.
     *
     * @param commitId the commit ID, tag, or branch name to check out
     * @param projetoPath the repository path
     */
    public static void checkout(String commitId, String projetoPath) {
        try {
            Git git = Git.open(new File(projetoPath));
            git.checkout().setForced(true).setName(commitId).call();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to checkout {0} in {1}", new Object[]{commitId, projetoPath});
            LOGGER.log(Level.FINE, "Checkout error", e);
        }
    }

    /**
     * Retrieves the remote origin URL of the git repository.
     *
     * @param projetoPath the repository path
     * @return the remote origin URL, or empty string on failure
     */
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

    /**
     * Retrieves the current branch name of the git repository.
     *
     * @param projetoPath the repository path
     * @return the current branch name
     */
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

    /**
     * Performs a git pull on the repository.
     *
     * @param projetoPath the repository path
     */
    public static void pull(String projetoPath) {
        try {
            Git git = Git.open(new File(projetoPath));
            git.pull().call();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to pull in: " + projetoPath, e);
        }
    }

    /**
     * Executes a git blame on the specified file and returns a map of line numbers to author names.
     *
     * @param projetoPath the repository path
     * @param filePathAbsolut the absolute path to the file
     * @return a map where keys are line numbers (1-indexed) and values are author names
     */
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

    /**
     * Retrieves the BlameResult for a specific file in the repository.
     *
     * @param projetoPath the repository path
     * @param filePath the absolute path to the file
     * @return the BlameResult object, or null on failure
     */
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
