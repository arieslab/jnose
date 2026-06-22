package io.github.arieslab.business.daos.utils;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
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

    public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    public org.hibernate.Session session() {
        return sessionFactory.getCurrentSession();
    }

    public Class<T> clazz() {
        return this.persistentClass;
    }

    public void delete(T entity) {
        session().remove(entity);
    }

    public void delete(Long id) {
        T entity = findById(id);
        if (entity != null) {
            session().remove(entity);
        }
    }

    public T findById(Long id) {
        return session().find(clazz(), id);
    }

    public List<T> listAll() {
        return findByHQL("FROM " + clazz().getSimpleName());
    }

    public int size() {
        CriteriaBuilder cb = session().getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        query.select(cb.count(query.from(clazz())));
        return session().createQuery(query).getSingleResult().intValue();
    }

    public T save(T entity) {
        session().persist(entity);
        return entity;
    }

    public List<T> findByCriteriaReturnList(Predicate... predicates) {
        return findByCriteria((Order) null, predicates);
    }

    public T findByCriteriaReturnUniqueResult(Predicate... predicates) {
        return findByCriteriaReturnUniqueResult((Order) null, predicates);
    }

    public List<T> findByCriteria(Order order, Predicate... predicates) {
        CriteriaBuilder cb = session().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz());
        Root<T> root = query.from(clazz());
        query.select(root).distinct(true);
        if (predicates.length > 0) {
            query.where(predicates);
        }
        if (order != null) {
            query.orderBy(order);
        }
        return session().createQuery(query).getResultList();
    }

    public List<T> findByCriteria(List<Order> orders, List<Predicate> predicates, int maxResult) {
        CriteriaBuilder cb = session().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz());
        Root<T> root = query.from(clazz());
        query.select(root).distinct(true);
        if (predicates != null && !predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        if (orders != null && !orders.isEmpty()) {
            query.orderBy(orders);
        }
        TypedQuery<T> q = session().createQuery(query);
        if (maxResult > 0) {
            q.setMaxResults(maxResult);
        }
        return q.getResultList();
    }

    public List<T> findByCriteriaWithAliases(List<Order> orders, List<Predicate> predicates, Map<String, String> alias) {
        CriteriaBuilder cb = session().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz());
        Root<T> root = query.from(clazz());
        query.select(root).distinct(true);
        if (alias != null) {
            for (Map.Entry<String, String> entry : alias.entrySet()) {
                root.join(entry.getKey()).alias(entry.getValue());
            }
        }
        if (predicates != null && !predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        if (orders != null && !orders.isEmpty()) {
            query.orderBy(orders);
        }
        return session().createQuery(query).getResultList();
    }

    public T findByCriteriaReturnUniqueResult(Order order, Predicate... predicates) {
        CriteriaBuilder cb = session().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz());
        Root<T> root = query.from(clazz());
        query.select(root).distinct(true);
        if (predicates.length > 0) {
            query.where(predicates);
        }
        if (order != null) {
            query.orderBy(order);
        }
        TypedQuery<T> q = session().createQuery(query);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public T findByCriteriaReturnUniqueResult(Order order, int offSet, int size, Predicate... predicates) {
        CriteriaBuilder cb = session().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz());
        Root<T> root = query.from(clazz());
        query.select(root).distinct(true);
        if (predicates.length > 0) {
            query.where(predicates);
        }
        if (order != null) {
            query.orderBy(order);
        }
        TypedQuery<T> q = session().createQuery(query);
        q.setFirstResult(offSet);
        q.setMaxResults(size);
        try {
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> findByCriteria(Order order) {
        CriteriaBuilder cb = session().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz());
        Root<T> root = query.from(clazz());
        query.select(root).distinct(true);
        if (order != null) {
            query.orderBy(order);
        }
        return session().createQuery(query).getResultList();
    }

    public List<T> findByCriteria(Order order, int offSet, int size) {
        CriteriaBuilder cb = session().getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz());
        Root<T> root = query.from(clazz());
        query.select(root).distinct(true);
        if (order != null) {
            query.orderBy(order);
        }
        TypedQuery<T> q = session().createQuery(query);
        q.setFirstResult(offSet);
        q.setMaxResults(size);
        return q.getResultList();
    }

    public List<T> findByHQL(String orderBy, boolean ascending, int offSet, int size) {
        String dir = ascending ? "asc" : "desc";
        TypedQuery<T> query = session().createQuery(
                "FROM " + clazz().getSimpleName() + " order by " + orderBy + " " + dir, clazz());
        query.setFirstResult(offSet);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public List<T> findByHQL(String hql) {
        return session().createQuery(hql, clazz()).getResultList();
    }

    public T findByHQLUniqueResult(String hql) {
        TypedQuery<T> query = session().createQuery(hql, clazz());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void executeSQL(String sql) {
        session().createNativeQuery(sql).executeUpdate();
    }

    @SuppressWarnings("rawtypes")
    public List executeSQL_List(String sql) {
        return session().createNativeQuery(sql).getResultList();
    }
}
