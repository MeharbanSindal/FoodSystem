package com.foodexpress.controller;

import com.foodexpress.dto.CartItem;
import com.foodexpress.model.Address;
import com.foodexpress.model.Order;
import com.foodexpress.model.User;
import com.foodexpress.service.AddressService;
import com.foodexpress.service.CartService;
import com.foodexpress.service.OrderService;
import com.foodexpress.service.ProductService;
import com.foodexpress.service.UserService;
import com.foodexpress.util.PdfGenerator;
import com.foodexpress.util.QrGenerator;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired 
    private OrderService orderService;

    @Autowired 
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ProductService productService;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal,
                            @RequestParam(value = "search", required = false, defaultValue = "") String search,
                            @RequestParam(value = "category", required = false, defaultValue = "") String category,
                            HttpSession session,
                            Model model) {

        var products = (!search.isEmpty() || !category.isEmpty())
                ? productService.searchProducts(0, search, category)
                : productService.getAllProducts();

        model.addAttribute("products", products);
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        model.addAttribute("cartItems", cartService.getCartItems(session));
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("orders", orderService.getOrdersByUser(principal.getName()));

        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        model.addAttribute("user", userService.findByEmail(principal.getName()));
        model.addAttribute("addresses", addressService.getAddressesForUser(principal.getName()));
        return "user/profile";
    }

    @GetMapping("/orders")
    public String orderHistory(Principal principal, Model model) {
        model.addAttribute("orders", orderService.getOrdersByUser(principal.getName()));
        return "user/order-history";
    }

    @GetMapping("/checkout")
    public String checkout(Principal principal, HttpSession session, Model model) {
        List<CartItem> cartItems = cartService.getCartItems(session);

        if (cartItems.isEmpty()) {
            return "redirect:/user/cart";
        }

        double cartTotal = cartService.getCartTotal(session);

        Order order = new Order();
        order.setSubtotal(cartTotal);
        order.calculateFinalAmounts();

        model.addAttribute("order", order);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("savedAddresses", addressService.getAddressesForUser(principal.getName()));

        try {
            String upiUrl = "upi://pay?pa=foodexpress@bank&am=" + order.getTotalAmount();
            String qrCode = QrGenerator.generateQrBase64(upiUrl, 250, 250);
            model.addAttribute("qrCode", qrCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "user/checkout";
    }

    @GetMapping("/invoice/{id}")
    public void downloadInvoice(@PathVariable("id") Long id, HttpServletResponse response) {

        try {
            Order order = orderService.getOrderById(id);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=Invoice_" + id + ".pdf");

            PdfGenerator.generateInvoice(order, response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== CART MANAGEMENT ROUTES ==========

    /**
     * Add product to cart
     */
    @PostMapping("/cart/add/{productId}")
    public String addToCart(
            @PathVariable("productId") Long productId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
            HttpSession session) {

        cartService.addToCart(session, productId, quantity);
        return "redirect:/user/cart";
    }

    /**
     * View shopping cart
     */
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cartItems = cartService.getCartItems(session);
        Double cartTotal = cartService.getCartTotal(session);
        Integer cartCount = cartService.getCartItemCount(session);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        model.addAttribute("cartCount", cartCount);

        return "user/cart";
    }

    /**
     * Update cart item quantity
     */
    @PostMapping("/cart/update")
    public String updateCart(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session) {

        cartService.updateCartQuantity(session, productId, quantity);
        return "redirect:/user/cart";
    }

    /**
     * Remove item from cart
     */
    @PostMapping("/cart/remove/{productId}")
    public String removeFromCart(
            @PathVariable("productId") Long productId,
            HttpSession session) {

        cartService.removeFromCart(session, productId);
        return "redirect:/user/cart";
    }

    /**
     * Checkout - create order from cart
     */
    @PostMapping("/order/place")
    public String placeOrder(
            Principal principal,
            HttpSession session,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "savedAddressId", required = false) Long savedAddressId,
            @RequestParam(value = "deliveryAddress", required = false) String deliveryAddress,
            @RequestParam(value = "deliveryLatitude", required = false) Double deliveryLatitude,
            @RequestParam(value = "deliveryLongitude", required = false) Double deliveryLongitude,
            RedirectAttributes redirectAttributes) {

        List<CartItem> cartItems = cartService.getCartItems(session);

        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty!");
            return "redirect:/user/cart";
        }

        try {
            if (savedAddressId != null) {
                Address saved = addressService.getAddressForUser(savedAddressId, principal.getName());
                if (saved != null) {
                    deliveryAddress = saved.getDisplayAddress();
                    deliveryLatitude = saved.getLatitude();
                    deliveryLongitude = saved.getLongitude();
                }
            }

            orderService.placeOrder(
                    cartItems,
                    principal.getName(),
                    paymentMethod,
                    deliveryAddress,
                    deliveryLatitude,
                    deliveryLongitude);

            cartService.clearCart(session);

            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/user/orders";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error processing checkout: " + e.getMessage());
            return "redirect:/user/cart";
        }
    }

    @PostMapping("/order/cancel/{id}")
    public String cancelOrder(@PathVariable("id") Long id,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        Order order = orderService.getOrderByIdForUser(id, principal.getName());
        if (order == null) {
            redirectAttributes.addFlashAttribute("error", "Order not found.");
            return "redirect:/user/orders";
        }

        orderService.cancelOrder(id);
        redirectAttributes.addFlashAttribute("success", "Order cancelled successfully.");
        return "redirect:/user/orders";
    }

    @GetMapping("/order/track/{id}")
    public String trackOrder(@PathVariable("id") Long id, Principal principal, Model model) {
        Order order = orderService.getOrderByIdForUser(id, principal.getName());
        if (order == null) {
            return "redirect:/user/orders";
        }

        User deliveryPartner = null;
        if (order.getDeliveryBoy() != null && !order.getDeliveryBoy().trim().isEmpty()) {
            User mappedUser = userService.findByEmail(order.getDeliveryBoy().trim());
            if (mappedUser != null && "ROLE_DELIVERY".equals(mappedUser.getRole())) {
                deliveryPartner = mappedUser;
            }
        }

        model.addAttribute("order", order);
        model.addAttribute("deliveryPartner", deliveryPartner);
        return "user/track-order";
    }

    @PostMapping("/address/add")
    public String addAddress(Principal principal,
                             @RequestParam(value = "label", required = false) String label,
                             @RequestParam("addressLine") String addressLine,
                             @RequestParam(value = "city", required = false) String city,
                             @RequestParam(value = "state", required = false) String state,
                             @RequestParam(value = "pincode", required = false) String pincode,
                             @RequestParam(value = "landmark", required = false) String landmark,
                             @RequestParam(value = "latitude", required = false) Double latitude,
                             @RequestParam(value = "longitude", required = false) Double longitude,
                             @RequestParam(value = "defaultAddress", required = false, defaultValue = "false") boolean defaultAddress,
                             RedirectAttributes redirectAttributes) {

        boolean created = addressService.addAddress(
                principal.getName(),
                label,
                addressLine,
                city,
                state,
                pincode,
                landmark,
                latitude,
                longitude,
                defaultAddress
        );

        if (created) {
            redirectAttributes.addFlashAttribute("success", "Address saved successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to save address. Please check required fields.");
        }

        return "redirect:/user/profile";
    }

    @PostMapping("/address/delete/{id}")
    public String deleteAddress(@PathVariable("id") Long id,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        boolean deleted = addressService.deleteAddress(principal.getName(), id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Address removed.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Address not found.");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/address/default/{id}")
    public String makeDefaultAddress(@PathVariable("id") Long id,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        boolean updated = addressService.markDefaultAddress(principal.getName(), id);
        if (updated) {
            redirectAttributes.addFlashAttribute("success", "Default address updated.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Could not update default address.");
        }
        return "redirect:/user/profile";
    }
}