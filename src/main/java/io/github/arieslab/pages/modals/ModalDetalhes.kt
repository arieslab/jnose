package io.github.arieslab.pages.modals

import io.github.arieslab.entities.Projeto
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ModalDetalhes(id: String, projeto: Projeto) : Modal<Void>(id) {
    init {
        header(Model.of(projeto.name))
        add(Label("name", projeto.name))
        add(Label("url", projeto.url))
        add(Label("path", projeto.path))
        add(Label("version", projeto.junitVersion))
        add(Label("stars", projeto.stars))
        val df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        add(Label("update", projeto.dateUpdate?.toInstant()?.atZone(ZoneId.systemDefault())?.format(df) ?: ""))
    }
}
