package com.foodexpress.controller;

import com.foodexpress.service.CartService;
import com.foodexpress.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    /**
     * Home page - redirect to products
     */
    @GetMapping("/")
    public String home(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            Model model,
            Authentication authentication,
            HttpSession session) {

        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            boolean isDelivery = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_DELIVERY".equals(a.getAuthority()));
            boolean isUser = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_USER".equals(a.getAuthority()));

            if (isAdmin) {
                return "redirect:/admin/dashboard";
            }
            if (isDelivery) {
                return "redirect:/delivery/dashboard";
            }
            if (isUser) {
                return "redirect:/user/dashboard";
            }
        }

        // Get all products or search results
        var products = (!search.isEmpty() || !category.isEmpty()) 
            ? productService.searchProducts(0, search, category)
            : productService.getAllProducts();

        // Add attributes to model
        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        model.addAttribute("cartItems", cartService.getCartItems(session));
        model.addAttribute("cartTotal", cartService.getCartTotal(session));

        return "index";
    }
}
