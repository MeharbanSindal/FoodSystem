package com.foodexpress.controller;

import com.foodexpress.model.Order;
import com.foodexpress.model.Payment;
import com.foodexpress.service.OrderService;
import com.foodexpress.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/razorpay/create-order")
    public ResponseEntity<?> createRazorpayOrder(@RequestParam("orderId") Long orderId, Principal principal) {
        try {
            Order order = orderService.getOrderByIdForUser(orderId, principal.getName());
            
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));
            }

            if (!order.getPaymentMethod().equals("RAZORPAY")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid payment method"));
            }

            Payment payment = paymentService.createRazorpayOrder(order);
            
            if (payment == null) {
                return ResponseEntity.status(500).body(Map.of("error", "Failed to create Razorpay order"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("razorpayOrderId", payment.getRazorpayOrderId());
            response.put("amount", payment.getAmount());
            response.put("currency", payment.getCurrency());
            response.put("keyId", System.getenv("RAZORPAY_KEY_ID"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @PostMapping("/razorpay/verify")
    public ResponseEntity<?> verifyRazorpayPayment(
            @RequestParam("razorpay_order_id") String razorpayOrderId,
            @RequestParam("razorpay_payment_id") String razorpayPaymentId,
            @RequestParam("razorpay_signature") String razorpaySignature,
            Principal principal) {

        try {
            Payment payment = paymentService.getPaymentByRazorpayOrderId(razorpayOrderId);
            
            if (payment == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Payment record not found"));
            }

            // Verify order belongs to current user
            Order order = orderService.getOrderByIdForUser(payment.getOrderId(), principal.getName());
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Order not found"));
            }

            boolean isVerified = paymentService.verifyRazorpayPayment(
                    razorpayOrderId, 
                    razorpayPaymentId, 
                    razorpaySignature
            );

            if (isVerified) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Payment verified successfully",
                        "orderId", order.getId()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "failed",
                        "message", "Payment verification failed"
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}
