package br.ufba.jnose.pages;

import br.ufba.jnose.pages.base.BasePage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.Cookie;

public class EvolutionPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private String pastaPath;

    public EvolutionPage() {
        Form form = new Form<>("form");
        TextField tfPastaPath = new TextField("tfPastaPath", new PropertyModel(this, "pastaPath"));
        form.add(tfPastaPath);
        Button btEnviar = new Button("btEnviar") {
            @Override
            public void onSubmit() {
                System.out.println(pastaPath);
            }
        };
        form.add(btEnviar);
        add(form);
    }

}