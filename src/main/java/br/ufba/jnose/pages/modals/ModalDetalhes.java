package br.ufba.jnose.pages.modals;

import br.ufba.jnose.entities.Projeto;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class ModalDetalhes<Void> extends Modal<Void> {


    public ModalDetalhes(String id, Projeto projeto) {
        super(id);
        this.header(Model.of(projeto.getName()));
        add(new Label("name", projeto.getName()));
        add(new Label("url", projeto.getUrl()));
    }

}
