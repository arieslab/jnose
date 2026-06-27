package io.github.arieslab.business.daos

import io.github.arieslab.business.daos.utils.DAOGeneric
import io.github.arieslab.entities.Projeto
import org.springframework.stereotype.Component

@Component
open class ProjectDao : DAOGeneric<Projeto>()
