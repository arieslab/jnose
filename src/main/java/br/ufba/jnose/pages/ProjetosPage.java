package br.ufba.jnose.pages;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.core.GitCore;
import br.ufba.jnose.core.JNoseCore;
import br.ufba.jnose.dto.Commit;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProjetosPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private String repoGit;

    public ProjetosPage() {
        this("");
    }

    public ProjetosPage(String repo) {
        super("ProjetosPage");
        this.repoGit = repo;

        Form form = new Form<>("form");

        TextField tfGitRepo = new TextField("tfGitRepo", new PropertyModel(this, "repoGit"));
        tfGitRepo.setRequired(true);
        form.add(tfGitRepo);

        form.add(new Link<String>("lkAddOracle") {
            @Override
            public void onClick() {
                repoGit = "https://github.com/danielevalverde/jnose-tests.git";
                setResponsePage(new ProjetosPage(repoGit));
            }
        });

        Button btEnviar = new Button("btClone") {
            @Override
            public void onSubmit() {
                GitCore.gitClone(repoGit);
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
                item.add(new Label("junit",JNoseCore.getJUnitVersion(projeto.getPath())));
                ArrayList<Commit> lista = GitCore.gitLogOneLine(projeto.getPath());
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                item.add(new Label("lastupdate",df.format(lista.get(0).date)));
                item.add(new Link<String>("linkPull") {
                    @Override
                    public void onClick() {
                        GitCore.pull(projeto.getPath());
                        setResponsePage(ProjetosPage.class);
                    }
                });
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
        File file = new File(WicketApplication.JNOSE_PROJECTS_FOLDER);
        return JNoseCore.listaProjetos(file.toURI(),new StringBuffer());
    }
}