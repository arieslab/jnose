package io.github.arieslab.business.daos.utils

import org.springframework.transaction.annotation.Transactional
import java.io.Serializable

@Transactional
open class BusinessGeneric<T : DAOGeneric<*>, Y : Serializable>(protected open var dao: T) {

    open fun size() = dao.size()

    open fun save(u: Y) {
        @Suppress("unchecked_cast")
        (dao as DAOGeneric<Y>).save(u)
    }

    open fun delete(u: Y) {
        @Suppress("unchecked_cast")
        (dao as DAOGeneric<Y>).delete(u)
    }

    open fun delete(id: Long) {
        @Suppress("unchecked_cast")
        (dao as DAOGeneric<Y>).delete(id)
    }

    @Suppress("unchecked_cast")
    open fun find(first: Long, count: Long): List<Y> {
        return (dao as DAOGeneric<Y>).findByHQL("id", false, first.toInt(), count.toInt())
    }

    @Suppress("unchecked_cast")
    open fun find(id: Long): Y? {
        return (dao as DAOGeneric<Y>).findById(id)
    }

    @Suppress("unchecked_cast")
    open fun listAll(): List<Y> {
        return (dao as DAOGeneric<Y>).listAll()
    }
}
