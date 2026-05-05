package com.foodexpress.service;

import com.foodexpress.dao.PaymentRepo;
import com.foodexpress.model.Order;
import com.foodexpress.model.Payment;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private OrderService orderService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    @Transactional
    public Payment createRazorpayOrder(Order order) {
        try {
            if (razorpayClient == null) {
                razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            }

            Payment payment = new Payment();
            payment.setOrderId(order.getId());
            payment.setAmount(order.getTotalAmount());
            payment.setCurrency("INR");
            payment.setStatus("PENDING");

            // Create Razorpay order
            JSONObject razorpayOrder = new JSONObject();
            razorpayOrder.put("amount", (int)(order.getTotalAmount() * 100)); // Amount in paise
            razorpayOrder.put("currency", "INR");
            razorpayOrder.put("receipt", "order_" + order.getId());
            razorpayOrder.put("payment_capture", 1); // Auto capture

            com.razorpay.Order order_response = razorpayClient.orders.create(razorpayOrder);
            payment.setRazorpayOrderId(order_response.get("id"));

            paymentRepo.save(payment);
            return payment;

        } catch (RazorpayException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public boolean verifyRazorpayPayment(String razorpayOrderId, String razorpayPaymentId, 
                                         String razorpaySignature) {
        try {
            Optional<Payment> paymentOpt = paymentRepo.findByRazorpayOrderId(razorpayOrderId);
            
            if (!paymentOpt.isPresent()) {
                return false;
            }

            Payment payment = paymentOpt.get();
            
            // Verify signature
            if (!verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature)) {
                payment.setStatus("FAILED");
                payment.setErrorMessage("Signature verification failed");
                paymentRepo.save(payment);
                return false;
            }

            // Update payment details
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setRazorpaySignature(razorpaySignature);
            payment.setStatus("COMPLETED");

            paymentRepo.save(payment);

            // Update order status
            Order order = orderService.getOrderById(payment.getOrderId());
            if (order != null) {
                order.setStatus("CONFIRMED");
                orderService.updateStatusWithoutEmail(order.getId(), "CONFIRMED");
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
                String data = razorpayOrderId + "|" + razorpayPaymentId;
                Mac mac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKeySpec = new SecretKeySpec(razorpayKeySecret.getBytes(), "HmacSHA256");
                mac.init(secretKeySpec);
                byte[] hashBytes = mac.doFinal(data.getBytes());

                return constantTimeEquals(bytesToHex(hashBytes), razorpaySignature);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        byte[] aBytes = a.getBytes();
        byte[] bBytes = b.getBytes();
        
        if (aBytes.length != bBytes.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }

    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepo.findByOrderId(orderId).orElse(null);
    }

    public Payment getPaymentByRazorpayOrderId(String razorpayOrderId) {
        return paymentRepo.findByRazorpayOrderId(razorpayOrderId).orElse(null);
    }
}
