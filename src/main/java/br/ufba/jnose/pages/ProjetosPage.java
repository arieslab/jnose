package br.ufba.jnose.pages;

import br.ufba.jnose.WicketApplication;
import br.ufba.jnose.business.ProjetoBusiness;
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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjetosPage extends BasePage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private ProjetoBusiness projetoBusiness;

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
                Projeto projeto = GitCore.gitClone(repoGit);

                br.ufba.jnose.entities.Projeto projeto2 = new br.ufba.jnose.entities.Projeto();
                projeto2.setName(projeto.getName());
                projeto2.setJunitVersion(JNoseCore.getJUnitVersion(projeto.getPath()).toString());
                projeto2.setStars(GitCore.getStarts(projeto.getPath()));
                projeto2.setPath(projeto.getPath());
                projeto2.setUrl(repoGit);
                projetoBusiness.save(projeto2);

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
                item.add(new Label("stars",GitCore.getStarts(projeto.getPath())));
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

        List<br.ufba.jnose.entities.Projeto> listaProjetos2 = projetoBusiness.listAll();

        ListView<br.ufba.jnose.entities.Projeto> lista2 = new ListView<br.ufba.jnose.entities.Projeto>("lista2",listaProjetos2) {
            @Override
            protected void populateItem(ListItem<br.ufba.jnose.entities.Projeto> item) {
                br.ufba.jnose.entities.Projeto projeto = item.getModelObject();
                item.add(new Label("projetoNome",projeto.getName()));
                item.add(new Label("path",projeto.getPath()));
                item.add(new Label("junit",JNoseCore.getJUnitVersion(projeto.getPath())));
                item.add(new Label("stars",GitCore.getStarts(projeto.getPath())));

                ArrayList<Commit> lista = GitCore.gitLogOneLine(projeto.getPath());
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                item.add(new Label("lastupdate",df.format(lista.get(0).date)));

                item.add(new Link<String>("linkPull") {
                    @Override
                    public void onClick() {
                        GitCore.pull(projeto.getPath());
                        ArrayList<Commit> lista = GitCore.gitLogOneLine(projeto.getPath());
                        Date dateUpdate = lista.get(0).date;
                        projeto.setDateUpdate(dateUpdate);
                        projetoBusiness.save(projeto);
                        setResponsePage(ProjetosPage.class);
                    }
                });
                item.add(new Link<String>("linkDelete") {
                    @Override
                    public void onClick() {
                        projetoBusiness.delete(projeto.getId());

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
        add(lista2);
    }

    private List<Projeto> loadProjetos(){
        File file = new File(WicketApplication.JNOSE_PROJECTS_FOLDER);
        return JNoseCore.listaProjetos(file.toURI(),new StringBuffer());
    }
}