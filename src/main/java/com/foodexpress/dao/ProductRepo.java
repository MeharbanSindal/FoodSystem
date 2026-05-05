package com.foodexpress.dao;

import com.foodexpress.model.Product;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepo {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void saveOrUpdate(Product product) {
        getSession().merge(product);
    }

    public Product findById(Long id) {
        return getSession().get(Product.class, id);
    }

    public List<Product> findAll() {
        String hql = "FROM Product";
        return getSession().createQuery(hql, Product.class).getResultList(); // FIXED
    }

    public List<Product> findPaginated(int page, int size, String search, String category) {
        StringBuilder hql = new StringBuilder("FROM Product p WHERE 1=1");

        if (search != null && !search.trim().isEmpty()) {
            hql.append(" AND (p.name LIKE :search OR p.category LIKE :search)");
        }

        if (category != null && !category.trim().isEmpty()) {
            hql.append(" AND p.category = :category");
        }

        Query<Product> query = getSession().createQuery(hql.toString(), Product.class);

        if (search != null && !search.trim().isEmpty()) {
            query.setParameter("search", "%" + search.trim() + "%");
        }
        if (category != null && !category.trim().isEmpty()) {
            query.setParameter("category", category);
        }

        int firstResult = page <= 0 ? 0 : (page - 1) * size;
        query.setFirstResult(firstResult);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public List<String> findAllCategories() {
        String hql = "SELECT DISTINCT p.category FROM Product p WHERE p.category IS NOT NULL";
        return getSession().createQuery(hql, String.class).getResultList();
    }

    public void delete(Long id) {
        Product product = findById(id);

        if (product != null) {
            getSession().remove(product);
        }
    }
}