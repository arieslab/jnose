package br.ufba.jnose.pages;

import br.ufba.jnose.core.GitCore;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.dto.Projeto;
import br.ufba.jnose.pages.base.BasePage;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjetosPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private String repoGit;

    public ProjetosPage() {
        repoGit = "";

        Form form = new Form<>("form");

        TextField tfGitRepo = new TextField("tfGitRepo", new PropertyModel(this, "repoGit"));
        tfGitRepo.setRequired(true);
        form.add(tfGitRepo);

        Button btEnviar = new Button("btClone") {
            @Override
            public void onSubmit() {
                GitCore.gitClonee(repoGit);
                setResponsePage(ProjetosPage.class);
            }
        };
        form.add(btEnviar);
        add(form);

        List<Projeto> listaProjetos = loadProjetos();

        ListView<Projeto> lista = new ListView<Projeto>("lista",listaProjetos) {
            @Override
            protected void populateItem(ListItem<Projeto> item) {
                Projeto projeto = item.getModelObject();
                item.add(new Label("projetoNome",projeto.getName()));
                item.add(new Label("path",projeto.getPath()));
                item.add(new Link<String>("linkDelete") {
                    @Override
                    public void onClick() {
                        File file = new File(projeto.getPath());
                        try {
                            FileUtils.deleteDirectory(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        setResponsePage(ProjetosPage.class);
                    }
                });
            }
        };
        add(lista);
    }

    private List<Projeto> loadProjetos(){
        File file = new File("./projects");
        return JNoseCore.listaProjetos(file.toURI(),new StringBuffer());
    }
}