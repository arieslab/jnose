package io.github.arieslab.pages.modals

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model

class ModalView(markupId: String, header: String, content: String) : Modal<Void>(markupId) {
    init {
        header(Model.of(header))
        add(Label("content", content).apply { setEscapeModelStrings(false) })
    }
}
