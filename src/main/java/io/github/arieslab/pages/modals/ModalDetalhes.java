package io.github.arieslab.pages.modals;

import io.github.arieslab.entities.Projeto;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * A Bootstrap modal for displaying detailed project information.
 */
public class ModalDetalhes<Void> extends Modal<Void> {

    /**
     * Creates a modal dialog showing project details such as name, URL, path, JUnit version, stars, and last update date.
     *
     * @param id the Wicket component markup ID
     * @param projeto the project entity whose details are displayed
     */
    public ModalDetalhes(String id, Projeto projeto) {
        super(id);
        this.header(Model.of(projeto.getName()));
        add(new Label("name", projeto.getName()));
        add(new Label("url", projeto.getUrl()));
        add(new Label("path", projeto.getPath()));
        add(new Label("version", projeto.getJunitVersion()));
        add(new Label("stars", projeto.getStars()));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        if(projeto.getDateUpdate() == null) {
            add(new Label("update", ""));
        }else{
            add(new Label("update", df.format(projeto.getDateUpdate().toInstant().atZone(ZoneId.systemDefault()))));
        }
    }

}
