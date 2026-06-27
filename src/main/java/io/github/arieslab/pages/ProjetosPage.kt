package io.github.arieslab.pages

import io.github.arieslab.business.ProjetoBusiness
import io.github.arieslab.base.GitCore
import io.github.arieslab.base.JNose
import io.github.arieslab.dtolocal.Commit
import io.github.arieslab.dtolocal.ProjetoDTO
import io.github.arieslab.entities.Projeto
import io.github.arieslab.pages.base.BasePage
import io.github.arieslab.pages.modals.ModalDetalhes
import org.apache.commons.io.FileUtils
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.TextField
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.model.LambdaModel
import org.apache.wicket.spring.injection.annot.SpringBean
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.logging.Logger

class ProjetosPage @JvmOverloads constructor(repo: String = "") : BasePage("ProjetosPage") {

    companion object {
        private val LOGGER = Logger.getLogger(ProjetosPage::class.java.name)
    }

    @SpringBean
    private lateinit var projetoBusiness: ProjetoBusiness

    private var repoGit: String = repo

    init {
        val form = Form<Unit>("form")

        val tfGitRepo = TextField("tfGitRepo", LambdaModel.of({ repoGit }, { v -> repoGit = v }))
        tfGitRepo.isRequired = true
        form.add(tfGitRepo)

        form.add(object : Link<String>("lkAddOracle") {
            override fun onClick() {
                repoGit = "https://github.com/tassiovirginio/jnose-dataset.git"
                setResponsePage(ProjetosPage(repoGit))
            }
        })

        form.add(object : IndicatingAjaxButton("btClone") {
            override fun onSubmit(target: AjaxRequestTarget) {
                val projeto = GitCore.gitClone(repoGit)

                val projeto2 = Projeto()
                projeto2.name = projeto.name
                projeto2.junitVersion = JNose.getJUnitVersion(projeto.path).toString()
                projeto2.stars = GitCore.getStarts(projeto.path)
                projeto2.path = projeto.path
                projeto2.url = repoGit
                val lista = GitCore.gitLogOneLine(projeto.path)
                projeto2.dateUpdate = lista.first().date
                projetoBusiness.save(projeto2)

                setResponsePage(ProjetosPage::class.java)
            }
        })
        add(form)

        var listaProjetosVerificar = projetoBusiness.listAll()
        for (projeto in listaProjetosVerificar) {
            if (!File(projeto.path).exists()) {
                projetoBusiness.delete(projeto)
            }
        }

        val listaProjetos = projetoBusiness.listAll()

        add(object : ListView<Projeto>("lista", listaProjetos) {
            override fun populateItem(item: ListItem<Projeto>) {
                val projeto = item.modelObject
                item.add(Label("projetoNome", projeto.name))
                item.add(Label("path", projeto.path))
                item.add(Label("url", projeto.url))
                item.add(Label("junit", projeto.junitVersion))
                item.add(Label("stars", projeto.stars))

                var lista = mutableListOf<Commit>()
                if (Files.exists(Path.of(projeto.path))) {
                    lista = GitCore.gitLogOneLine(projeto.path)
                }

                val df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                try {
                    if (lista.isNotEmpty()) {
                        item.add(Label("lastupdate", df.format(lista.first().date!!.toInstant().atZone(ZoneId.systemDefault()))))
                    } else {
                        item.add(Label("lastupdate", "N/A"))
                    }
                } catch (e: Exception) {
                    LOGGER.warning("Failed to format date: $e")
                }

                item.add(object : Link<String>("linkPull") {
                    override fun onClick() {
                        GitCore.pull(projeto.path)
                        val lista = GitCore.gitLogOneLine(projeto.path)
                        projeto.dateUpdate = lista.first().date
                        projetoBusiness.save(projeto)
                        setResponsePage(ProjetosPage::class.java)
                    }
                })
                item.add(object : Link<String>("linkDelete") {
                    override fun onClick() {
                        projetoBusiness.delete(projeto.id!!)

                        val file = File(projeto.path)
                        try {
                            FileUtils.deleteDirectory(file)
                        } catch (e: Exception) {
                            LOGGER.warning("Failed to delete directory: ${file.path}")
                        }
                        setResponsePage(ProjetosPage::class.java)
                    }
                })

                val modal = ModalDetalhes("modal", projeto)
                item.add(modal)

                item.add(object : AjaxLink<Void>("btModal") {
                    override fun onClick(ajaxRequestTarget: AjaxRequestTarget) {
                        modal.show(true)
                        modal.isVisible = true
                        ajaxRequestTarget.add(modal)
                    }
                })
            }
        })
    }
}
