package com.foodexpress.dao;

import com.foodexpress.model.Payment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepo {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(Payment payment) {
        getSession().merge(payment);
    }

    public Optional<Payment> findByRazorpayOrderId(String razorpayOrderId) {
        String hql = "FROM Payment p WHERE p.razorpayOrderId = :razorpayOrderId";
        Query<Payment> query = getSession().createQuery(hql, Payment.class);
        query.setParameter("razorpayOrderId", razorpayOrderId);
        List<Payment> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<Payment> findByRazorpayPaymentId(String razorpayPaymentId) {
        String hql = "FROM Payment p WHERE p.razorpayPaymentId = :razorpayPaymentId";
        Query<Payment> query = getSession().createQuery(hql, Payment.class);
        query.setParameter("razorpayPaymentId", razorpayPaymentId);
        List<Payment> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Optional<Payment> findByOrderId(Long orderId) {
        String hql = "FROM Payment p WHERE p.orderId = :orderId";
        Query<Payment> query = getSession().createQuery(hql, Payment.class);
        query.setParameter("orderId", orderId);
        List<Payment> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
