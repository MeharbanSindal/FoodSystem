package com.foodexpress.dao;

import com.foodexpress.model.Address;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AddressRepo {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Address findById(Long id) {
        return getSession().get(Address.class, id);
    }

    public List<Address> findByUserId(Long userId) {
        String hql = "FROM Address a WHERE a.user.id = :userId ORDER BY a.defaultAddress DESC, a.id DESC";
        Query<Address> query = getSession().createQuery(hql, Address.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public void saveOrUpdate(Address address) {
        getSession().merge(address);
    }

    public void delete(Address address) {
        getSession().remove(address);
    }

    public void clearDefaultForUser(Long userId) {
        String hql = "UPDATE Address a SET a.defaultAddress = false WHERE a.user.id = :userId";
        Query<?> query = getSession().createQuery(hql);
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
}
