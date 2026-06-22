package io.github.arieslab.business.daos.utils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class DAOGeneric<T> {

    @Autowired
    private SessionFactory sessionFactory;
    private Class<T> persistentClass;

    public DAOGeneric() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Sets the Hibernate SessionFactory.
     *
     * @param sf the SessionFactory
     */
    public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    /**
     * Returns the current Hibernate session.
     *
     * @return the current Session
     */
    public org.hibernate.Session session() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Returns the persistent class type.
     *
     * @return the entity class
     */
    public Class<T> clazz() {
        return this.persistentClass;
    }

    /**
     * Deletes an entity from the database.
     *
     * @param entity the entity to delete
     */
    public void delete(T entity) {
        session().delete(entity);
    }

    /**
     * Deletes an entity by its ID using a HQL query.
     *
     * @param id the entity ID
     */
    public void delete(Long id) {
        String hql = "delete " + clazz().getSimpleName() + " where id = :id";
        Query q = session().createQuery(hql).setParameter("id", id);
        q.executeUpdate();
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id the entity ID
     * @return the found entity, or null
     */
    public T findById(Long id) {
        return (T) session().get(clazz(), id);
    }

    /**
     * Lists all entities of this type.
     *
     * @return list of all entities
     */
    public List<T> listAll() {
        return findByHQL("FROM " + clazz().getSimpleName());
    }

    /**
     * Returns the number of entities in the database.
     *
     * @return the entity count
     */
    public int size() {
        Criteria crit = session().createCriteria(clazz());
        crit.setProjection(Projections.rowCount());
        Object result = crit.uniqueResult();
        Long size = (Long) result;
        return size.intValue();
    }

    /**
     * Saves or updates an entity.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    public T save(T entity) {
        session().saveOrUpdate(entity);
        return entity;
    }

    /**
     * Finds entities matching the given Hibernate criteria.
     *
     * @param criterion the criteria to match
     * @return list of matching entities
     */
    public List<T> findByCriteriaReturnList(Criterion... criterion) {
        return findByCriteria(null, criterion);
    }

    /**
     * Finds a single entity matching the given criteria.
     *
     * @param criterion the criteria to match
     * @return the matching entity, or null
     */
    public T findByCriteriaReturnUniqueResult(Criterion... criterion) {
        return findByCriteriaReturnUniqueResult(null, criterion);
    }

    /**
     * Finds entities matching criteria with optional ordering.
     *
     * @param order optional ordering
     * @param criterion the criteria to match
     * @return list of matching entities
     */
    public List<T> findByCriteria(Order order, Criterion... criterion) {
        Criteria crit = session().createCriteria(clazz());
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (order != null) {
            crit.addOrder(order);
        }
        return crit.list();
    }

    /**
     * Finds entities matching criteria with ordering, limited to maxResult.
     *
     * @param orders list of ordering specifications
     * @param criterion list of criteria
     * @param maxResult maximum number of results
     * @return list of matching entities
     */
    public List<T> findByCriteria(List<Order> orders, List<Criterion> criterion, int maxResult) {
        Criteria crit = session().createCriteria(clazz());

        crit.setMaxResults(maxResult);

        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        for (Order o : orders) {
            crit.addOrder(o);
        }
        return crit.list();
    }

    /**
     * Finds entities matching criteria with ordering and aliases.
     *
     * @param orders list of ordering specifications
     * @param criterion list of criteria
     * @param alias map of alias names
     * @return list of matching entities
     */
    public List<T> findByCriteria2(List<Order> orders, List<Criterion> criterion, Map<String, String> alias) {
        Criteria crit = session().createCriteria(clazz());

        if (alias != null) {
            for (Map.Entry<String, String> entry : alias.entrySet()) {
                crit.createAlias(entry.getKey(), entry.getValue());
            }
        }

        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        for (Order o : orders) {
            crit.addOrder(o);
        }
        return crit.list();
    }

    /**
     * Finds a single entity matching criteria with optional ordering.
     *
     * @param order optional ordering
     * @param criterion the criteria to match
     * @return the matching entity, or null
     */
    public T findByCriteriaReturnUniqueResult(Order order, Criterion... criterion) {
        Criteria crit = session().createCriteria(clazz());
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (order != null) {
            crit.addOrder(order);
        }
        return (T) crit.uniqueResult();
    }

    /**
     * Finds a single entity with pagination and ordering.
     *
     * @param order optional ordering
     * @param offSet the offset
     * @param size the page size
     * @param criterion the criteria to match
     * @return the matching entity, or null
     */
    public T findByCriteriaReturnUniqueResult(Order order, int offSet, int size, Criterion... criterion) {
        Criteria crit = session().createCriteria(clazz());
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        crit.setFirstResult(offSet);
        crit.setMaxResults(size);
        for (Criterion c : criterion) {
            crit.add(c);
        }
        if (order != null) {
            crit.addOrder(order);
        }
        return (T) crit.uniqueResult();
    }

    /**
     * Lists all entities with optional ordering.
     *
     * @param order optional ordering
     * @return list of entities
     */
    public List<T> findByCriteria(Order order) {
        Criteria crit = session().createCriteria(clazz());
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (order != null) {
            crit.addOrder(order);
        }
        return crit.list();
    }

    /**
     * Lists entities with pagination and optional ordering.
     *
     * @param order optional ordering
     * @param offSet the offset
     * @param size the page size
     * @return list of entities
     */
    public List<T> findByCriteria(Order order, int offSet, int size) {
        Criteria crit = session().createCriteria(clazz());
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        crit.setFirstResult(offSet);
        crit.setMaxResults(size);
        if (order != null) {
            crit.addOrder(order);
        }
        return crit.list();
    }

    /**
     * Executes a paginated HQL query with ordering.
     *
     * @param order the ordering
     * @param offSet the offset
     * @param size the page size
     * @return list of results
     */
    public List<T> findByHQL(Order order, int offSet, int size) {
        Query query = session().createQuery("FROM " + clazz().getSimpleName() + " order by " + order.getPropertyName());
        query.setFirstResult(offSet);
        query.setMaxResults(size);
        return query.list();
    }

    /**
     * Executes an HQL query.
     *
     * @param hql the HQL query string
     * @return list of results
     */
    public List<T> findByHQL(String hql) {
        Query query = session().createQuery(hql);
        return query.list();
    }

    /**
     * Executes an HQL query returning a single result.
     *
     * @param hql the HQL query string
     * @return the single result, or null
     */
    public T findByHQLUniqueResult(String hql) {
        Query query = session().createQuery(hql);
        return (T) query.uniqueResult();
    }

    /**
     * Executes a native SQL update query.
     *
     * @param query the SQL query string
     */
    public void executeSQL(String query) {
        session().createSQLQuery(query).executeUpdate();
    }

    /**
     * Executes a native SQL query and returns results as a list of maps.
     *
     * @param sql the SQL query string
     * @return list of result rows as maps
     */
    public List executeSQL_List(String sql) {
        SQLQuery q = session().createSQLQuery(sql);
        q.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
        return q.list();
    }
}
