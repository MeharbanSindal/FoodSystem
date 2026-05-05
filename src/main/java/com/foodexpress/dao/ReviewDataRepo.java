package com.foodexpress.dao;

import com.foodexpress.model.Review;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewDataRepo {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Review save(Review review) {
        return (Review) getSession().merge(review);
    }

    public Optional<Review> findById(Long reviewId) {
        return Optional.ofNullable(getSession().get(Review.class, reviewId));
    }

    public void deleteById(Long reviewId) {
        Review review = getSession().get(Review.class, reviewId);
        if (review != null) {
            getSession().remove(review);
        }
    }

    public List<Review> findByProductIdOrderByCreatedAtDesc(Long productId) {
        String hql = "FROM Review r WHERE r.product.id = :productId ORDER BY r.createdAt DESC";
        Query<Review> query = getSession().createQuery(hql, Review.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    public List<Review> findByUserId(Long userId) {
        String hql = "FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC";
        Query<Review> query = getSession().createQuery(hql, Review.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public Optional<Review> findByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId) {
        String hql = "FROM Review r WHERE r.user.id = :userId AND r.product.id = :productId AND r.orderId = :orderId";
        Query<Review> query = getSession().createQuery(hql, Review.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        query.setParameter("orderId", orderId);
        List<Review> list = query.getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Double getAverageRatingForProduct(Long productId) {
        String hql = "SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId";
        Query<Double> query = getSession().createQuery(hql, Double.class);
        query.setParameter("productId", productId);
        Double avg = query.uniqueResult();
        return avg == null ? 0.0 : avg;
    }

    public Long getReviewCountForProduct(Long productId) {
        String hql = "SELECT COUNT(r.id) FROM Review r WHERE r.product.id = :productId";
        Query<Long> query = getSession().createQuery(hql, Long.class);
        query.setParameter("productId", productId);
        Long count = query.uniqueResult();
        return count == null ? 0L : count;
    }

    public List<Review> findMostHelpfulReviewsForProduct(Long productId) {
        String hql = "FROM Review r WHERE r.product.id = :productId ORDER BY r.helpfulCount DESC, r.createdAt DESC";
        Query<Review> query = getSession().createQuery(hql, Review.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }

    public Long countByRating(Long productId, Integer rating) {
        String hql = "SELECT COUNT(r.id) FROM Review r WHERE r.product.id = :productId AND r.rating = :rating";
        Query<Long> query = getSession().createQuery(hql, Long.class);
        query.setParameter("productId", productId);
        query.setParameter("rating", rating);
        Long count = query.uniqueResult();
        return count == null ? 0L : count;
    }
}
