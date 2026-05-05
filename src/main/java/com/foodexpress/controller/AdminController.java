package com.foodexpress.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.foodexpress.model.Product;
import com.foodexpress.service.ProductService;
import com.foodexpress.service.OrderService;
import com.foodexpress.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private Cloudinary cloudinary;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        model.addAttribute("revenue", orderService.calculateTotalRevenue());
        model.addAttribute("recentOrders", orderService.getRecentOrders());
        model.addAttribute("productsCount", productService.getAllProducts().size());
        model.addAttribute("pendingOrdersCount", orderService.getPendingOrdersCount());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts(5));
        model.addAttribute("lowStockCount", productService.getLowStockProducts(5).size());
        model.addAttribute("outOfStockCount", productService.getOutOfStockCount());
        return "admin/dashboard";
    }

        // Enhanced analytics endpoints
        @GetMapping("/api/analytics/summary")
        @ResponseBody
        public java.util.Map<String, Object> getAnalyticsSummary() {
            java.util.Map<String, Object> summary = new java.util.HashMap<>();
        
            Double totalRevenue = orderService.calculateTotalRevenue();
            long totalOrders = orderService.getAllOrders().size();
            long pendingOrders = orderService.getPendingOrdersCount();
            long totalProducts = productService.getAllProducts().size();
            long totalUsers = userService.getAllUsers().size();
            double safeRevenue = totalRevenue == null ? 0.0 : totalRevenue;
        
            summary.put("totalRevenue", String.format("%.2f", safeRevenue));
            summary.put("totalOrders", totalOrders);
            summary.put("pendingOrders", pendingOrders);
            summary.put("completedOrders", totalOrders - pendingOrders);
            summary.put("totalProducts", totalProducts);
            summary.put("totalUsers", totalUsers);
            summary.put("averageOrderValue", totalOrders > 0 ? String.format("%.2f", safeRevenue / totalOrders) : "0.00");
        
            return summary;
        }

        @GetMapping("/api/analytics/revenue-trend")
        @ResponseBody
        public java.util.Map<String, Object> getRevenueTrend() {
            java.util.Map<String, Object> trendData = new java.util.HashMap<>();
        
            // This would ideally fetch data from database with date grouping
            // For now, returning sample data structure
            trendData.put("labels", new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun"});
            trendData.put("data", new int[]{5000, 7500, 6200, 8900, 10500, 12000});
        
            return trendData;
        }

        @GetMapping("/api/analytics/orders-by-status")
        @ResponseBody
        public java.util.Map<String, Object> getOrdersByStatus() {
            java.util.Map<String, Object> statusData = new java.util.HashMap<>();
        
            java.util.List<com.foodexpress.model.Order> allOrders = orderService.getAllOrders();
        
            long pending = allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
            long confirmed = allOrders.stream().filter(o -> "CONFIRMED".equals(o.getStatus())).count();
            long outForDelivery = allOrders.stream().filter(o -> "OUT_FOR_DELIVERY".equals(o.getStatus())).count();
            long delivered = allOrders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();
        
            statusData.put("labels", new String[]{"Pending", "Confirmed", "Out for Delivery", "Delivered"});
            statusData.put("data", new long[]{pending, confirmed, outForDelivery, delivered});
            statusData.put("backgroundColor", new String[]{"#FFC300", "#17a2b8", "#007bff", "#28a745"});
        
            return statusData;
        }

    @GetMapping("/products")
    public String manageProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("product", new Product());
        return "admin/products";
    }

    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("product", product != null ? product : new Product());
        return "admin/products";
    }

//    @PostMapping("/product/save")
//    public String saveProduct(@ModelAttribute Product product,
//                              @RequestParam("imageFile") MultipartFile file,
//                              HttpServletRequest request) {
//        try {
//            if (!file.isEmpty()) {
//                String uploadPath = request.getServletContext().getRealPath("/resources/uploads/");
//                File uploadDir = new File(uploadPath);
//
//                if (!uploadDir.exists()) uploadDir.mkdirs();
//
//                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//                file.transferTo(new File(uploadPath + File.separator + fileName));
//
//                product.setImageName(fileName);
//            }
//
//            productService.saveOrUpdate(product);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return "redirect:/admin/products";
//    }

    @PostMapping("/product/save")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("imageFile") MultipartFile file) {
        try {
            if (!file.isEmpty()) {

                Map uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.emptyMap()
                );

                String imageUrl = uploadResult.get("secure_url").toString();

                product.setImageName(imageUrl); // storing URL
            }

            productService.saveOrUpdate(product);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/products";
    }
    
    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/order/update-status")
    public String updateOrderStatus(@RequestParam("orderId") Long id,
                                    @RequestParam("status") String status,
                                    @RequestParam(value = "deliveryBoy", required = false) String deliveryBoy) {

        orderService.updateStatus(id, status, deliveryBoy);
        
        return "redirect:/admin/orders";
    }

    /**
     * View all orders for admin
     */
    @GetMapping("/orders")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("deliveryBoys", userService.getUsersByRole("ROLE_DELIVERY"));
        return "admin/orders";
    }

    @GetMapping("/delivery-boys")
    public String deliveryBoys(Model model) {
        model.addAttribute("deliveryBoys", userService.getUsersByRole("ROLE_DELIVERY"));
        return "admin/delivery-boys";
    }

    @PostMapping("/delivery-boys")
    public String createDeliveryBoy(@RequestParam("fullName") String fullName,
                                    @RequestParam("email") String email,
                                    @RequestParam("mobileNumber") String mobileNumber,
                                    @RequestParam("password") String password,
                                    RedirectAttributes redirectAttributes) {

        boolean created = userService.createDeliveryUser(fullName, email, mobileNumber, password);
        if (created) {
            redirectAttributes.addFlashAttribute("success", "Delivery partner created successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to create partner. Check email/mobile details.");
        }

        return "redirect:/admin/delivery-boys";
    }

    /**
     * Update order status for delivery
     */
    @GetMapping("/order/{id}")
    public String viewOrderDetail(@PathVariable("id") Long id, Model model) {
        // implement order detail view if needed
        return "redirect:/admin/orders";
    }

}