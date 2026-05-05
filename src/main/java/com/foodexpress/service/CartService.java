package com.foodexpress.service;

import com.foodexpress.dto.CartItem;
import com.foodexpress.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;

import java.util.*;

@Service
public class CartService {

    @Autowired
    private ProductService productService;

    /**
     * Get cart from session (cart items by product ID)
     */
    public Map<Long, CartItem> getCart(HttpSession session) {
        Object cartObj = session.getAttribute("cart");

        if (cartObj == null) {
            Map<Long, CartItem> emptyCart = new LinkedHashMap<>();
            session.setAttribute("cart", emptyCart);
            return emptyCart;
        }

        if (cartObj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            Map<Long, CartItem> cart = (Map<Long, CartItem>) cartObj;
            return cart;
        }

        if (cartObj instanceof List<?>) {
            Map<Long, CartItem> convertedCart = new LinkedHashMap<>();
            for (Object value : (List<?>) cartObj) {
                if (value instanceof CartItem item && item.getProductId() != null) {
                    convertedCart.put(item.getProductId(), item);
                }
            }
            session.setAttribute("cart", convertedCart);
            return convertedCart;
        }

        Map<Long, CartItem> fallbackCart = new LinkedHashMap<>();
        session.setAttribute("cart", fallbackCart);
        return fallbackCart;
    }

    /**
     * Add product to cart
     */
    public void addToCart(HttpSession session, Long productId, Integer quantity) {
        Product product = productService.findById(productId);
        if (product != null) {
            int stock = product.getStockQuantity() == null ? 0 : product.getStockQuantity();
            if (stock <= 0) {
                return;
            }

            int safeQuantity = quantity == null || quantity <= 0 ? 1 : quantity;
            Map<Long, CartItem> cart = getCart(session);
            
            if (cart.containsKey(productId)) {
                CartItem item = cart.get(productId);
                int nextQuantity = item.getQuantity() + safeQuantity;
                item.setQuantity(Math.min(nextQuantity, stock));
                item.updateSubTotal();
            } else {
                CartItem item = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getImageName(),
                    product.getPrice(),
                    Math.min(safeQuantity, stock)
                );
                cart.put(productId, item);
            }
        }
    }

    /**
     * Remove product from cart
     */
    public void removeFromCart(HttpSession session, Long productId) {
        Map<Long, CartItem> cart = getCart(session);
        cart.remove(productId);
    }

    /**
     * Update cart item quantity
     */
    public void updateCartQuantity(HttpSession session, Long productId, Integer quantity) {
        Map<Long, CartItem> cart = getCart(session);
        if (cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            Product product = productService.findById(productId);
            int stock = product == null || product.getStockQuantity() == null ? 0 : product.getStockQuantity();

            if (quantity > 0 && stock > 0) {
                item.setQuantity(Math.min(quantity, stock));
                item.updateSubTotal();
            } else {
                cart.remove(productId);
            }
        }
    }

    /**
     * Get all cart items as list
     */
    public List<CartItem> getCartItems(HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);
        return new ArrayList<>(cart.values());
    }

    /**
     * Calculate total price
     */
    public Double getCartTotal(HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);
        return cart.values().stream()
            .mapToDouble(CartItem::getSubTotal)
            .sum();
    }

    /**
     * Clear cart
     */
    public void clearCart(HttpSession session) {
        Map<Long, CartItem> cart = new LinkedHashMap<>();
        session.setAttribute("cart", cart);
    }

    /**
     * Get cart item count
     */
    public Integer getCartItemCount(HttpSession session) {
        Map<Long, CartItem> cart = getCart(session);
        return cart.values().stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }
}
