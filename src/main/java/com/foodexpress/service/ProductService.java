package com.foodexpress.service;

import com.foodexpress.dao.ProductRepo;
import com.foodexpress.dto.CartItem;
import com.foodexpress.model.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public List<String> getAllCategories() {
        return productRepo.findAllCategories();
    }

    public List<Product> searchProducts(int page, String query, String category) {
        return productRepo.findPaginated(page, 12, query, category);
    }

    public Product findById(Long id) {
        return productRepo.findById(id);
    }

    public void saveOrUpdate(Product product) {
        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            product.setStockQuantity(0);
        }
        productRepo.saveOrUpdate(product);
    }

    public void deleteProduct(Long id) {
        productRepo.delete(id);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepo.findAll().stream()
                .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() > 0 && p.getStockQuantity() <= threshold)
                .collect(Collectors.toList());
    }

    public long getOutOfStockCount() {
        return productRepo.findAll().stream()
                .filter(p -> p.getStockQuantity() == null || p.getStockQuantity() <= 0)
                .count();
    }

    public String validateCartStock(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Product product = findById(cartItem.getProductId());
            if (product == null) {
                return "Product not found in inventory.";
            }
            int stock = product.getStockQuantity() == null ? 0 : product.getStockQuantity();
            int required = cartItem.getQuantity() == null ? 0 : cartItem.getQuantity();
            if (stock <= 0) {
                return product.getName() + " is out of stock.";
            }
            if (required > stock) {
                return "Requested quantity is not available for " + product.getName() + ".";
            }
        }
        return null;
    }

    public void deductStock(Long productId, int quantity) {
        Product product = findById(productId);
        if (product == null) {
            throw new IllegalStateException("Product not found while deducting stock.");
        }

        int currentStock = product.getStockQuantity() == null ? 0 : product.getStockQuantity();
        if (quantity <= 0 || currentStock < quantity) {
            throw new IllegalStateException("Insufficient stock for " + product.getName() + ".");
        }

        product.setStockQuantity(currentStock - quantity);
        productRepo.saveOrUpdate(product);
    }
}