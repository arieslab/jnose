package io.github.arieslab.pages;

import io.github.arieslab.business.ProjetoBusiness;
import io.github.arieslab.base.GitCore;
import io.github.arieslab.base.JNose;
import io.github.arieslab.dtolocal.Commit;
import io.github.arieslab.dtolocal.ProjetoDTO;
import io.github.arieslab.entities.Projeto;
import io.github.arieslab.pages.base.BasePage;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import io.github.arieslab.pages.modals.ModalDetalhes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjetosPage extends BasePage {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(ProjetosPage.class.getName());

    @SpringBean
    private ProjetoBusiness projetoBusiness;

    private String repoGit;

    public ProjetosPage(){
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
                repoGit = "https://github.com/tassiovirginio/jnose-dataset.git";
                setResponsePage(new ProjetosPage(repoGit));
            }
        });

        IndicatingAjaxButton btEnviar = new IndicatingAjaxButton("btClone") {
            @Override
            public void onSubmit(AjaxRequestTarget target) {
                ProjetoDTO projeto = GitCore.gitClone(repoGit);

                io.github.arieslab.entities.Projeto projeto2 = new io.github.arieslab.entities.Projeto();
                projeto2.setName(projeto.getName());
                projeto2.setJunitVersion(JNose.getJUnitVersion(projeto.getPath()).toString());
                projeto2.setStars(GitCore.getStarts(projeto.getPath()));
                projeto2.setPath(projeto.getPath());
                projeto2.setUrl(repoGit);
                ArrayList<Commit> lista = GitCore.gitLogOneLine(projeto.getPath());
                projeto2.setDateUpdate(lista.get(0).date);
                projetoBusiness.save(projeto2);

                setResponsePage(ProjetosPage.class);
            }
        };
        form.add(btEnviar);
        add(form);

        List<io.github.arieslab.entities.Projeto> listaProjetosVerificar = projetoBusiness.listAll();

        for(Projeto projeto : listaProjetosVerificar){
            File file = new File(projeto.getPath());
            if(file.exists() == false){
                projetoBusiness.delete(projeto);
            }
        }

        List<io.github.arieslab.entities.Projeto> listaProjetos = projetoBusiness.listAll();

        ListView<io.github.arieslab.entities.Projeto> lista2 = new ListView<io.github.arieslab.entities.Projeto>("lista", listaProjetos) {
            @Override
            protected void populateItem(ListItem<io.github.arieslab.entities.Projeto> item) {
                io.github.arieslab.entities.Projeto projeto = item.getModelObject();
                item.add(new Label("projetoNome", projeto.getName()));
                item.add(new Label("path", projeto.getPath()));
                item.add(new Label("url", projeto.getUrl()));
                item.add(new Label("junit", projeto.getJunitVersion()));
                item.add(new Label("stars", projeto.getStars()));

                ArrayList<Commit> lista = new ArrayList<>();
                if(Files.exists(Path.of(projeto.getPath()))){
                    lista = GitCore.gitLogOneLine(projeto.getPath());
                }

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                try {
                    if(lista.size() > 0) {
                        item.add(new Label("lastupdate", df.format(lista.get(0).date)));
                    }else{
                        item.add(new Label("lastupdate", "N/A"));
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to format date", e);
                }

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
                            LOGGER.log(Level.WARNING, "Failed to delete directory: " + file.getPath(), e);
                        }
                        setResponsePage(ProjetosPage.class);
                    }
                });

                final ModalDetalhes modal = new ModalDetalhes("modal", projeto);
                item.add(modal);

                item.add(new AjaxLink<Void>("btModal") {
                    @Override
                    public void onClick(AjaxRequestTarget ajaxRequestTarget) {
                        modal.show(true);
                        modal.setVisible(true);
                        ajaxRequestTarget.add(modal);
                    }
                });

            }
        };
        add(lista2);

    }

}
