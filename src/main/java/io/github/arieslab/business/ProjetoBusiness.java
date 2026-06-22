package io.github.arieslab.business;

import io.github.arieslab.business.daos.ProjectDao;
import io.github.arieslab.business.daos.utils.BusinessGeneric;
import io.github.arieslab.entities.Projeto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static java.util.stream.Collectors.toList;

@Component
@Transactional
public class ProjetoBusiness extends BusinessGeneric<ProjectDao, Projeto> {

    /**
     * Lists all projects that have a valid (non-"None") JUnit version.
     *
     * @return filtered list of projects
     */
    public List<Projeto> listAllWithFilter(){
        return dao.listAll().stream().filter(o -> !o.getJunitVersion().equals("None")).collect(toList());
    }

    /**
     * Finds a project by its name.
     *
     * @param nomeProjeto the project name
     * @return the matching project, or null
     */
    public Projeto getProjetoByName(String nomeProjeto){
        return dao.findByHQLUniqueResult("FROM Projeto p WHERE p.name = '" + nomeProjeto+ "'");
    }
}
