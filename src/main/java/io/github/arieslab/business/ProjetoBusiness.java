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
    public List<Projeto> listAllWithFilter(){
        return dao.listAll().stream().filter(o -> !o.getJunitVersion().equals("None")).collect(toList());
    }

    public Projeto getProjetoByName(String nomeProjeto){
        return dao.findByHQLUniqueResult("FROM Projeto p WHERE p.name = '" + nomeProjeto+ "'");
    }
}
