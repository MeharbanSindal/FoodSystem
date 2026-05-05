package com.foodexpress.dao;

import com.foodexpress.model.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepo {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(User user) {
        getSession().persist(user);
    }

    public void update(User user) {
        getSession().merge(user);
    }

    public User findById(Long id) {
        return getSession().get(User.class, id);
    }

    public User findByEmail(String email) {

        String hql = "FROM User u WHERE u.email = :email";

        Query<User> query = getSession().createQuery(hql, User.class);
        query.setParameter("email", email);

        List<User> list = query.getResultList(); // FIXED

        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> findAll() {
        String hql = "FROM User";
        return getSession().createQuery(hql, User.class).getResultList(); // FIXED
    }

    public List<User> findByRole(String role) {
        String hql = "FROM User u WHERE u.role = :role ORDER BY u.fullName ASC";
        Query<User> query = getSession().createQuery(hql, User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }
}