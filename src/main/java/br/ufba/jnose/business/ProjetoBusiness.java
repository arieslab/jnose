package br.ufba.jnose.business;

import br.ufba.jnose.business.daos.ProjectDao;
import br.ufba.jnose.business.daos.utils.BusinessGeneric;
import br.ufba.jnose.entities.Projeto;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import static org.hibernate.criterion.Restrictions.*;

@Component
@Transactional
public class ProjetoBusiness extends BusinessGeneric<ProjectDao, Projeto> {
}
