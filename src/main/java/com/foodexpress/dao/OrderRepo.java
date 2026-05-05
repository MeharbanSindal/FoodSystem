package com.foodexpress.dao;

import com.foodexpress.model.Order;
import com.foodexpress.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepo {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Order order) {
        getSession().persist(order);
    }

    public void update(Order order) {
        getSession().merge(order);
    }

    public Order findById(Long id) {
        String hql = "SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.user " +
                "LEFT JOIN FETCH o.items i " +
                "LEFT JOIN FETCH i.product " +
                "WHERE o.id = :id";
        Query<Order> query = getSession().createQuery(hql, Order.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    public List<Order> findByUser(User user) {
        String hql = "SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.items i " +
                "LEFT JOIN FETCH i.product " +
                "WHERE o.user = :user ORDER BY o.id DESC";
        Query<Order> query = getSession().createQuery(hql, Order.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public List<Order> findAll() {
        String hql = "SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.user " +
                "LEFT JOIN FETCH o.items i " +
                "LEFT JOIN FETCH i.product " +
                "ORDER BY o.id DESC";
        return getSession().createQuery(hql, Order.class).getResultList();
    }

    public Double getTotalRevenue() {
        String hql = "SELECT SUM(o.totalAmount) FROM Order o WHERE o.status != 'CANCELLED'";
        Query<Double> query = getSession().createQuery(hql, Double.class);
        Double total = query.uniqueResult();
        return (total != null) ? total : 0.0;
    }

    public List<Order> findRecentOrders(int limit) {
        String hql = "SELECT o FROM Order o " +
                "LEFT JOIN FETCH o.user " +
                "ORDER BY o.id DESC";
        Query<Order> query = getSession().createQuery(hql, Order.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public long countByStatus(String status) {
        String hql = "SELECT COUNT(o.id) FROM Order o WHERE o.status = :status";
        Query<Long> query = getSession().createQuery(hql, Long.class);
        query.setParameter("status", status);
        Long count = query.uniqueResult();
        return count != null ? count : 0L;
    }

    public List<Order> findByDeliveryBoy(String deliveryBoy) {
        String hql = "SELECT DISTINCT o FROM Order o " +
                "LEFT JOIN FETCH o.user " +
                "LEFT JOIN FETCH o.items i " +
                "LEFT JOIN FETCH i.product " +
                "WHERE o.deliveryBoy = :deliveryBoy ORDER BY o.id DESC";
        Query<Order> query = getSession().createQuery(hql, Order.class);
        query.setParameter("deliveryBoy", deliveryBoy);
        return query.getResultList();
    }
}