package com.foodexpress.service;

import com.foodexpress.dao.OrderRepo;
import com.foodexpress.dao.UserRepo;
import com.foodexpress.dto.CartItem;
import com.foodexpress.model.Order;
import com.foodexpress.model.OrderItem;
import com.foodexpress.model.Product;
import com.foodexpress.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductService productService;

    @Autowired
    private EmailService emailService;

    public void placeOrder(List<CartItem> cartItems, String userEmail, String paymentMethod) {
        placeOrder(cartItems, userEmail, paymentMethod, null, null, null);
    }

    public void placeOrder(List<CartItem> cartItems,
                           String userEmail,
                           String paymentMethod,
                           String deliveryAddress,
                           Double deliveryLatitude,
                           Double deliveryLongitude) {

        User user = userRepo.findByEmail(userEmail);

        if (user == null || cartItems == null || cartItems.isEmpty()) {
            return;
        }

        String stockValidationError = productService.validateCartStock(cartItems);
        if (stockValidationError != null) {
            throw new IllegalStateException(stockValidationError);
        }

        Order order = new Order();
        order.setUser(user);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");
        order.setTrackingCode(generateTrackingCode());
        order.setDeliveryAddress(normalizeAddress(deliveryAddress));
        order.setDeliveryLatitude(deliveryLatitude);
        order.setDeliveryLongitude(deliveryLongitude);
        order.setSubtotal(cartItems.stream()
                .mapToDouble(item -> item.getSubTotal() == null ? 0.0 : item.getSubTotal())
                .sum());

        order.calculateFinalAmounts();

        List<OrderItem> items = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = productService.findById(cartItem.getProductId());
            if (product == null) {
                continue;
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getUnitPrice());
            items.add(orderItem);

            productService.deductStock(product.getId(), cartItem.getQuantity());
        }

        order.setItems(items);
        orderRepo.save(order);

        emailService.sendOrderConfirmationEmail(
                userEmail,
                order.getId(),
                order.getTrackingCode(),
                order.getTotalAmount(),
                user.getFullName()
        );
    }

    public void cancelOrder(Long orderId) {

        Order order = orderRepo.findById(orderId);

        if (order != null && !"DELIVERED".equals(order.getStatus())) {

            order.setStatus("CANCELLED");

            double refundAmount = order.getTotalAmount() * 0.80;

            orderRepo.update(order);

            emailService.sendOrderCancellationEmail(
                    order.getUser().getEmail(),
                    orderId,
                    order.getUser().getFullName(),
                    refundAmount
            );
        }
    }

    public void updateStatus(Long orderId, String status) {
        updateStatus(orderId, status, null);
    }

    public void updateStatus(Long orderId, String status, String deliveryBoy) {

        Order order = orderRepo.findById(orderId);

        if (order != null) {
            order.setStatus(status);
            if (deliveryBoy != null) {
                String trimmedDeliveryBoy = deliveryBoy.trim();
                if (trimmedDeliveryBoy.isEmpty()) {
                    order.setDeliveryBoy(null);
                } else {
                    User deliveryUser = userRepo.findByEmail(trimmedDeliveryBoy);
                    if (deliveryUser != null && "ROLE_DELIVERY".equals(deliveryUser.getRole())) {
                        order.setDeliveryBoy(trimmedDeliveryBoy);
                    }
                }
            }

            if ("OUT_FOR_DELIVERY".equals(status)) {
                String otp = generateDeliveryOtp();
                order.setDeliveryOtp(otp);
                order.setDeliveryOtpGeneratedAt(new Date());

                emailService.sendOrderStatusEmail(
                        order.getUser().getEmail(),
                        order.getId(),
                        status,
                        order.getUser().getFullName(),
                        otp
                );
            } else if ("DELIVERED".equals(status)) {
                order.setDeliveryOtp(null);
                order.setDeliveryOtpGeneratedAt(null);
                
                emailService.sendOrderStatusEmail(
                        order.getUser().getEmail(),
                        order.getId(),
                        status,
                        order.getUser().getFullName(),
                        null
                );
            } else if ("CONFIRMED".equals(status)) {
                emailService.sendOrderStatusEmail(
                        order.getUser().getEmail(),
                        order.getId(),
                        status,
                        order.getUser().getFullName(),
                        null
                );
            }

            orderRepo.update(order);
        }
    }

    public List<Order> getOrdersByUser(String email) {
        User user = userRepo.findByEmail(email);
        return (user != null) ? orderRepo.findByUser(user) : List.of();
    }

    public List<Order> getAllOrders() {
        return orderRepo.findAll();
    }

    public Double calculateTotalRevenue() {
        return orderRepo.getTotalRevenue();
    }

    public List<Order> getRecentOrders() {
        return orderRepo.findRecentOrders(5);
    }

    public Order getOrderById(Long id) {
        return orderRepo.findById(id);
    }

    public long getPendingOrdersCount() {
        return orderRepo.countByStatus("PENDING");
    }

    public Order getOrderByIdForUser(Long orderId, String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            return null;
        }
        Order order = orderRepo.findById(orderId);
        if (order == null || order.getUser() == null || !email.equalsIgnoreCase(order.getUser().getEmail())) {
            return null;
        }
        return order;
    }

    public List<Order> getAssignedOrdersForDeliveryBoy(String deliveryBoyEmail) {
        if (deliveryBoyEmail == null || deliveryBoyEmail.trim().isEmpty()) {
            return List.of();
        }
        return orderRepo.findByDeliveryBoy(deliveryBoyEmail.trim());
    }

    public boolean updateStatusByDeliveryBoy(Long orderId, String status, String deliveryBoyEmail, String otp) {
        if (orderId == null || status == null || deliveryBoyEmail == null) {
            return false;
        }

        if (!"OUT_FOR_DELIVERY".equals(status) && !"DELIVERED".equals(status)) {
            return false;
        }

        Order order = orderRepo.findById(orderId);
        if (order == null || order.getDeliveryBoy() == null) {
            return false;
        }

        if (!deliveryBoyEmail.equalsIgnoreCase(order.getDeliveryBoy())) {
            return false;
        }

        if ("DELIVERED".equals(status)) {
            if (order.getDeliveryOtp() == null || otp == null || !order.getDeliveryOtp().equals(otp.trim())) {
                return false;
            }
            order.setDeliveryOtp(null);
            order.setDeliveryOtpGeneratedAt(null);
        }

        order.setStatus(status);
        orderRepo.update(order);

        if ("DELIVERED".equals(status)) {
            emailService.sendSimpleEmail(
                    order.getUser().getEmail(),
                    "Order Delivered",
                    "Your order #" + order.getId() + " has been delivered successfully."
            );
        }

        return true;
    }

    @Transactional
    public void updateStatusWithoutEmail(Long orderId, String status) {
        Order order = orderRepo.findById(orderId);
        if (order != null) {
            order.setStatus(status);
            orderRepo.update(order);
        }
    }

    private String generateTrackingCode() {
        return "FX" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private String normalizeAddress(String deliveryAddress) {
        if (deliveryAddress == null) {
            return null;
        }
        String trimmed = deliveryAddress.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String generateDeliveryOtp() {
        int otp = 1000 + new Random().nextInt(9000);
        return String.valueOf(otp);
    }
}