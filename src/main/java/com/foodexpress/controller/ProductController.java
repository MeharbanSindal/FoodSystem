package com.foodexpress.controller;

import com.foodexpress.model.Product;
import com.foodexpress.service.CartService;
import com.foodexpress.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CartService cartService;

    /**
     * Display all products with search and category filter
     */
    @GetMapping
    public String showAllProducts(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "category", required = false, defaultValue = "") String category,
            Model model,
            HttpSession session) {

        List<Product> products;

        // Search and filter products
        if (!search.isEmpty() || !category.isEmpty()) {
            products = productService.searchProducts(0, search, category);
        } else {
            products = productService.getAllProducts();
        }

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

    /**
     * Show product detail
     */
    @GetMapping("/{id}")
    public String productDetail(
            @PathVariable("id") Long id,
            Model model,
            HttpSession session) {

        Product product = productService.findById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("cartCount", cartService.getCartItemCount(session));
            model.addAttribute("cartItems", cartService.getCartItems(session));
            model.addAttribute("cartTotal", cartService.getCartTotal(session));
            return "user/product-detail";
        }

        return "redirect:/products";
    }
}
