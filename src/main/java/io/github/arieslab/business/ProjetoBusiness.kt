package io.github.arieslab.business

import io.github.arieslab.business.daos.ProjectDao
import io.github.arieslab.business.daos.utils.BusinessGeneric
import io.github.arieslab.entities.Projeto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
open class ProjetoBusiness @Autowired constructor(
    override var dao: ProjectDao
) : BusinessGeneric<ProjectDao, Projeto>(dao) {

    open fun listAllWithFilter(): List<Projeto> {
        return dao.listAll().filter { it.junitVersion != "None" }
    }

    open fun getProjetoByName(nomeProjeto: String): Projeto? {
        return dao.findByHQLUniqueResult("FROM Projeto p WHERE p.name = '$nomeProjeto'")
    }
}
