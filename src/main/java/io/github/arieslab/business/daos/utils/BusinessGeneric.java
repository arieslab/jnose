package io.github.arieslab.business.daos.utils;

import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Transactional
public class BusinessGeneric<T extends DAOGeneric, Y extends Serializable> {

    @Autowired
    protected T dao;

    /**
     * Returns the number of entities.
     *
     * @return the entity count
     */
    public int size() {
        return dao.size();
    }

    /**
     * Saves an entity.
     *
     * @param u the entity to save
     */
    public void save(Y u) {
        dao.save(u);
    }

    /**
     * Deletes an entity.
     *
     * @param u the entity to delete
     */
    public void delete(Y u) {
        dao.delete(u);
    }

    /**
     * Deletes an entity by its ID.
     *
     * @param id the entity ID
     */
    public void delete(Long id) {
        dao.delete(id);
    }

    /**
     * Finds entities with pagination, ordered by ID descending.
     *
     * @param first the offset
     * @param count the page size
     * @return list of entities
     */
    public List<Y> find(Long first, Long count) {
        return dao.findByHQL(Order.desc("id"), first.intValue(), count.intValue());
    }

    /**
     * Finds an entity by its ID.
     *
     * @param id the entity ID
     * @return the found entity, or null
     */
    public Y find(Long id) {
        return (Y) dao.findById(id);
    }

    /**
     * Lists all entities.
     *
     * @return list of all entities
     */
    public List<Y> listAll() {
        return dao.listAll();
    }

}
