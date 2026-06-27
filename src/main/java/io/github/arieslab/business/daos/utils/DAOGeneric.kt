package io.github.arieslab.business.daos.utils

import jakarta.persistence.NoResultException
import jakarta.persistence.TypedQuery
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Order
import jakarta.persistence.criteria.Predicate
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired

open class DAOGeneric<T> {

    @Autowired
    private lateinit var sessionFactory: SessionFactory

    @Suppress("UNCHECKED_CAST")
    private val persistentClass: Class<T> by lazy {
        (javaClass.genericSuperclass as? java.lang.reflect.ParameterizedType)
            ?.actualTypeArguments?.get(0) as? Class<T>
            ?: throw RuntimeException("Could not determine persistent class")
    }

    fun setSessionFactory(sf: SessionFactory) {
        sessionFactory = sf
    }

    fun session(): Session = sessionFactory.currentSession

    fun clazz() = persistentClass

    fun delete(entity: T) {
        session().remove(entity)
    }

    fun delete(id: Long) {
        val entity = findById(id)
        if (entity != null) {
            session().remove(entity)
        }
    }

    fun findById(id: Long): T? = session().find(clazz(), id)

    fun listAll(): List<T> = findByHQL("FROM ${clazz().simpleName}")

    fun size(): Int {
        val cb = session().criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        query.select(cb.count(query.from(clazz())))
        return session().createQuery(query).singleResult.toInt()
    }

    fun save(entity: T): T {
        session().persist(entity)
        return entity
    }

    fun findByCriteriaReturnList(vararg predicates: Predicate): List<T> {
        return findByCriteria(null as Order?, *predicates)
    }

    fun findByCriteriaReturnUniqueResult(vararg predicates: Predicate): T? {
        return findByCriteriaReturnUniqueResult(null as Order?, *predicates)
    }

    fun findByCriteria(order: Order?, vararg predicates: Predicate): List<T> {
        val cb = session().criteriaBuilder
        val query: CriteriaQuery<T> = cb.createQuery(clazz())
        val root = query.from(clazz())
        query.select(root).distinct(true)
        if (predicates.isNotEmpty()) {
            query.where(*predicates)
        }
        if (order != null) {
            query.orderBy(order)
        }
        return session().createQuery(query).resultList
    }

    fun findByCriteria(orders: List<Order>?, predicates: List<Predicate>?, maxResult: Int): List<T> {
        val cb = session().criteriaBuilder
        val query: CriteriaQuery<T> = cb.createQuery(clazz())
        val root = query.from(clazz())
        query.select(root).distinct(true)
        if (!predicates.isNullOrEmpty()) {
            query.where(*predicates.toTypedArray())
        }
        if (!orders.isNullOrEmpty()) {
            query.orderBy(orders)
        }
        val q = session().createQuery(query)
        if (maxResult > 0) {
            q.maxResults = maxResult
        }
        return q.resultList
    }

    fun findByCriteriaWithAliases(orders: List<Order>?, predicates: List<Predicate>?, alias: Map<String, String>?) {
        val cb = session().criteriaBuilder
        val query: CriteriaQuery<T> = cb.createQuery(clazz())
        val root = query.from(clazz())
        query.select(root).distinct(true)
        if (alias != null) {
            for ((key, value) in alias) {
                @Suppress("UNCHECKED_CAST")
                (root.join<Any, Any>(key) as Any).let { }
                root.join<Any, Any>(key).alias(value)
            }
        }
        if (!predicates.isNullOrEmpty()) {
            query.where(*predicates.toTypedArray())
        }
        if (!orders.isNullOrEmpty()) {
            query.orderBy(orders)
        }
    }

    fun findByCriteriaReturnUniqueResult(order: Order?, vararg predicates: Predicate): T? {
        val cb = session().criteriaBuilder
        val query: CriteriaQuery<T> = cb.createQuery(clazz())
        val root = query.from(clazz())
        query.select(root).distinct(true)
        if (predicates.isNotEmpty()) {
            query.where(*predicates)
        }
        if (order != null) {
            query.orderBy(order)
        }
        return try {
            session().createQuery(query).singleResult
        } catch (e: NoResultException) {
            null
        }
    }

    fun findByCriteriaReturnUniqueResult(order: Order?, offSet: Int, size: Int, vararg predicates: Predicate): T? {
        val cb = session().criteriaBuilder
        val query: CriteriaQuery<T> = cb.createQuery(clazz())
        val root = query.from(clazz())
        query.select(root).distinct(true)
        if (predicates.isNotEmpty()) {
            query.where(*predicates)
        }
        if (order != null) {
            query.orderBy(order)
        }
        val q = session().createQuery(query)
        q.firstResult = offSet
        q.maxResults = size
        return try {
            q.singleResult
        } catch (e: NoResultException) {
            null
        }
    }

    fun findByCriteria(order: Order?): List<T> {
        val cb = session().criteriaBuilder
        val query: CriteriaQuery<T> = cb.createQuery(clazz())
        val root = query.from(clazz())
        query.select(root).distinct(true)
        if (order != null) {
            query.orderBy(order)
        }
        return session().createQuery(query).resultList
    }

    fun findByCriteria(order: Order?, offSet: Int, size: Int): List<T> {
        val cb = session().criteriaBuilder
        val query: CriteriaQuery<T> = cb.createQuery(clazz())
        val root = query.from(clazz())
        query.select(root).distinct(true)
        if (order != null) {
            query.orderBy(order)
        }
        val q = session().createQuery(query)
        q.firstResult = offSet
        q.maxResults = size
        return q.resultList
    }

    fun findByHQL(orderBy: String, ascending: Boolean, offSet: Int, size: Int): List<T> {
        val dir = if (ascending) "asc" else "desc"
        val query = session().createQuery(
            "FROM ${clazz().simpleName} order by $orderBy $dir", clazz())
        query.firstResult = offSet
        query.maxResults = size
        return query.resultList
    }

    fun findByHQL(hql: String): List<T> {
        return session().createQuery(hql, clazz()).resultList
    }

    fun findByHQLUniqueResult(hql: String): T? {
        val query = session().createQuery(hql, clazz())
        return try {
            query.singleResult
        } catch (e: NoResultException) {
            null
        }
    }

    fun executeSQL(sql: String) {
        session().createNativeQuery(sql).executeUpdate()
    }

    @Suppress("unchecked_cast")
    fun executeSQL_List(sql: String): List<*> {
        return session().createNativeQuery(sql).resultList
    }
}
