package io.github.arieslab.base

import io.github.arieslab.WicketApplication
import io.github.arieslab.dtolocal.Commit
import io.github.arieslab.dtolocal.ProjetoDTO
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.blame.BlameResult
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.kohsuke.github.GitHub
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.Logger

object GitCore {

    private val LOGGER = Logger.getLogger(GitCore::class.java.name)

    fun getStarts(repoLocal: String): Int {
        val url = getURL(repoLocal)
        val repoName = getNameByGithub(url)
        var stars = 0
        try {
            val github = GitHub.connectAnonymously()
            val repo = github.getRepository(repoName)
            stars = repo.stargazersCount
        } catch (e: Exception) {
            LOGGER.warning("Failed to get stars for: $repoLocal")
        }
        return stars
    }

    private fun getNameByGithub(path_: String): String {
        var path = path_.replace(".git", "")
        if (path.endsWith("/")) {
            path = path.substring(0, path.length - 1)
        }
        val projeto_ = path.substring(path.lastIndexOf("/") + 1)
        var owner_ = path.substring(0, path.lastIndexOf("/"))
        owner_ = owner_.substring(owner_.lastIndexOf("/") + 1)
        return "$owner_/$projeto_"
    }

    fun gitClone(repoURL: String): ProjetoDTO {
        val repoName: String
        if (repoURL.contains(".git")) {
            repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1, repoURL.lastIndexOf("."))
        } else {
            repoName = repoURL.substring(repoURL.lastIndexOf("/") + 1)
        }

        val file: File
        try {
            file = File(WicketApplication.JNOSE_PROJECTS_FOLDER + repoName)
            if (file.exists()) {
                FileUtils.deleteDirectory(file)
            }
            Git.cloneRepository()
                .setURI(repoURL)
                .setDirectory(file)
                .call()
        } catch (e: Exception) {
            LOGGER.severe("Failed to clone: $repoURL")
            throw RuntimeException("Failed to clone: $repoURL", e)
        }

        val projetoBean = io.github.arieslab.entities.Projeto()
        projetoBean.name = repoName
        projetoBean.path = file.path
        return ProjetoDTO.fromProjeto(projetoBean)
    }

    fun gitLogOneLine(pathExecute: String): MutableList<Commit> {
        return gitLogOneLine(pathExecute, 500)
    }

    fun gitLogOneLine(pathExecute: String, maxCount: Int): MutableList<Commit> {
        val lista = mutableListOf<Commit>()
        try {
            val git = Git.open(File(pathExecute))
            git.log().all().setMaxCount(maxCount).call().forEach { revCommit: RevCommit ->
                val authorIdent = revCommit.authorIdent
                val authorDate = authorIdent.`when`
                lista.add(Commit(
                    revCommit.id.name,
                    authorIdent.name,
                    authorDate,
                    revCommit.fullMessage))
            }
        } catch (e: Exception) {
            LOGGER.warning("Failed to get git log for: $pathExecute")
        }
        return lista
    }

    fun getLastCommit(pathProject: String): MutableList<Commit> {
        val lista = mutableListOf<Commit>()
        try {
            val git = Git.open(File(pathProject))
            git.log().all().call().forEach { revCommit: RevCommit ->
                val authorIdent = revCommit.authorIdent
                val authorDate = authorIdent.`when`
                lista.add(Commit(
                    revCommit.id.name,
                    authorIdent.name,
                    authorDate,
                    revCommit.fullMessage))
            }
        } catch (e: Exception) {
            LOGGER.warning("Failed to get last commit for: $pathProject")
        }
        return lista
    }

    fun gitTags(pathExecute: String): MutableList<Commit> {
        val lista = mutableListOf<Commit>()
        try {
            val git = Git.open(File(pathExecute))
            val call: List<Ref> = git.tagList().call()
            for (ref in call) {
                LOGGER.fine("Tag: $ref ${ref.name} ${ref.objectId.name}")
                val log = git.log()
                val peeledRef = git.repository.peel(ref)
                if (peeledRef.peeledObjectId != null) {
                    log.add(peeledRef.peeledObjectId)
                } else {
                    log.add(ref.objectId)
                }
                val logs = log.call()
                for (revCommit in logs) {
                    val authorIdent = revCommit.authorIdent
                    val authorDate = authorIdent.`when`
                    LOGGER.fine("$authorDate - Commit: $revCommit, name: ${revCommit.name}, id: ${revCommit.id.name}")
                    lista.add(Commit(
                        revCommit.id.name,
                        authorIdent.name,
                        authorDate,
                        revCommit.fullMessage,
                        ref.name))
                    break
                }
            }
        } catch (e: Exception) {
            LOGGER.warning("Failed to get tags for: $pathExecute")
        }
        return lista
    }

    fun checkout(commitId: String, projetoPath: String) {
        try {
            val git = Git.open(File(projetoPath))
            git.checkout().setForced(true).setName(commitId).call()
        } catch (e: Exception) {
            LOGGER.warning("Failed to checkout $commitId in $projetoPath")
            LOGGER.fine("Checkout error: $e")
        }
    }

    fun getURL(projetoPath: String): String {
        var url = ""
        try {
            val git = Git.open(File(projetoPath))
            url = git.repository.config.getString("remote", "origin", "url")
        } catch (e: Exception) {
            LOGGER.warning("Failed to get URL for: $projetoPath")
        }
        return url
    }

    fun branch(projetoPath: String): String {
        var branchcurrent = ""
        try {
            val git = Git.open(File(projetoPath))
            branchcurrent = git.repository.branch
        } catch (e: Exception) {
            LOGGER.warning("Failed to get branch for: $projetoPath")
        }
        return branchcurrent
    }

    fun pull(projetoPath: String) {
        try {
            val git = Git.open(File(projetoPath))
            git.pull().call()
        } catch (e: Exception) {
            LOGGER.warning("Failed to pull in: $projetoPath")
        }
    }

    fun blame(projetoPath: String, filePathAbsolut: String): Map<Int, String> {
        val retorno = mutableMapOf<Int, String>()
        val filePathRepo = filePathAbsolut.replace("$projetoPath/", "")

        try {
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val git = Git.open(File(projetoPath))
            val blameCommand = git.blame()
            blameCommand.setStartCommit(git.repository.resolve("HEAD"))
            blameCommand.setFilePath(filePathRepo)
            val result = blameCommand.call()
            val rawText = result.resultContents

            for (i in 0 until rawText.size()) {
                val sourceAuthor = result.getSourceAuthor(i)
                val sourceCommit = result.getSourceCommit(i)
                LOGGER.fine("${sourceAuthor.name} - ${sourceCommit?.let { dateFormat.format(Instant.ofEpochSecond(it.commitTime.toLong()).atZone(ZoneId.systemDefault())) }} - ${sourceCommit?.name}: ${rawText.getString(i)}")
                retorno[i + 1] = sourceAuthor.name
            }
        } catch (e: Exception) {
            LOGGER.warning("Failed to blame file: $filePathAbsolut")
        }

        return retorno
    }

    fun getBlameResultForFile(projetoPath: String, filePath: String): BlameResult? {
        var blame: BlameResult? = null
        try {
            val git = Git.open(File(projetoPath))
            val jgitRepository = git.repository
            val blamer = org.eclipse.jgit.api.BlameCommand(jgitRepository)
            val commitID = jgitRepository.resolve("HEAD")
            blamer.setStartCommit(commitID)
            val filePathRepo = filePath.replace("$projetoPath/", "")
            blamer.setFilePath(filePathRepo)
            blame = blamer.call()
        } catch (e: Exception) {
            LOGGER.warning("Failed to blame file: $filePath")
        }
        return blame
    }
}
