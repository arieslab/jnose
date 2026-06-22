package io.github.arieslab.pages.modals;

import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

/**
 * A Bootstrap modal for displaying test smell details with HTML content.
 */
public class ModalView extends Modal<Void> {

    /**
     * Creates a modal dialog with a header and HTML content body.
     *
     * @param markupId the Wicket component markup ID
     * @param header the modal header text
     * @param content the HTML content to display in the modal body
     */
    public ModalView(String markupId, String header, String content) {
        super(markupId);
        header(Model.of(header));
        add(new Label("content",content).setEscapeModelStrings(false));
    }

}
