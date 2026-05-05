package com.foodexpress.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@foodexpress.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("no-reply@foodexpress.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void sendOrderConfirmationEmail(String to, Long orderId, String trackingCode, 
                                           Double totalAmount, String userName) {
        String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px;'>" +
                "<div style='background-color: #ff4d4d; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;'>" +
                "<h2>Order Confirmed ✓</h2>" +
                "</div>" +
                "<div style='padding: 20px;'>" +
                "<p>Hi " + userName + ",</p>" +
                "<p>Your order has been placed successfully!</p>" +
                "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
                "<p><strong>Order Details:</strong></p>" +
                "<p>Order ID: <strong>#" + orderId + "</strong></p>" +
                "<p>Tracking Code: <strong>" + trackingCode + "</strong></p>" +
                "<p>Total Amount: <strong>Rs. " + String.format("%.2f", totalAmount) + "</strong></p>" +
                "</div>" +
                "<p>You can track your order using the tracking code above.</p>" +
                "<p style='color: #666; font-size: 12px; margin-top: 20px;'>Thank you for ordering from Food Express!</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";
        
        sendHtmlEmail(to, "Order Confirmation #" + orderId, htmlContent);
    }

    @Async
    public void sendOrderStatusEmail(String to, Long orderId, String status, 
                                     String userName, String deliveryOtp) {
        String statusMessage = "";
        String statusColor = "#2c3e50";
        
        switch(status) {
            case "CONFIRMED":
                statusMessage = "Your order has been confirmed and is being prepared!";
                statusColor = "#17a2b8";
                break;
            case "OUT_FOR_DELIVERY":
                statusMessage = "Your order is on the way!";
                statusColor = "#007bff";
                break;
            case "DELIVERED":
                statusMessage = "Your order has been delivered successfully!";
                statusColor = "#28a745";
                break;
            default:
                statusMessage = "Your order status has been updated to: " + status;
        }
        
        String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px;'>" +
                "<div style='background-color: " + statusColor + "; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;'>" +
                "<h2>Order Update ✉</h2>" +
                "</div>" +
                "<div style='padding: 20px;'>" +
                "<p>Hi " + userName + ",</p>" +
                "<p>" + statusMessage + "</p>" +
                "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
                "<p><strong>Order #" + orderId + "</strong></p>" +
                "<p>Status: <strong>" + status.replace("_", " ") + "</strong></p>";
        
        if (deliveryOtp != null && !deliveryOtp.isEmpty()) {
            htmlContent += "<p style='color: #ff4d4d;'><strong>Delivery OTP: " + deliveryOtp + 
                          "</strong></p><p style='font-size: 12px; color: #666;'>Share this OTP with your delivery partner at the doorstep.</p>";
        }
        
        htmlContent += "</div>" +
                "<p style='color: #666; font-size: 12px; margin-top: 20px;'>Thank you for ordering from Food Express!</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";
        
        sendHtmlEmail(to, "Order Update: " + status.replace("_", " ") + " - Order #" + orderId, htmlContent);
    }

    @Async
    public void sendOrderCancellationEmail(String to, Long orderId, String userName, Double refundAmount) {
        String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px;'>" +
                "<div style='background-color: #dc3545; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;'>" +
                "<h2>Order Cancelled</h2>" +
                "</div>" +
                "<div style='padding: 20px;'>" +
                "<p>Hi " + userName + ",</p>" +
                "<p>Your order #" + orderId + " has been cancelled.</p>" +
                "<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
                "<p><strong>Refund Details:</strong></p>" +
                "<p>Refund Amount: <strong style='color: #28a745;'>Rs. " + String.format("%.2f", refundAmount) + "</strong></p>" +
                "<p style='font-size: 12px; color: #666;'>The refund will be processed to your original payment method within 3-5 business days.</p>" +
                "</div>" +
                "<p style='color: #666; font-size: 12px; margin-top: 20px;'>If you have any questions, please contact our support team.</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";
        
        sendHtmlEmail(to, "Order Cancellation #" + orderId, htmlContent);
    }

    @Async
    public void sendWelcomeEmail(String to, String userName) {
        String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px;'>" +
                "<div style='background-color: #ff4d4d; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;'>" +
                "<h2>Welcome to Food Express! 🍔</h2>" +
                "</div>" +
                "<div style='padding: 20px;'>" +
                "<p>Hi " + userName + ",</p>" +
                "<p>Welcome to Food Express! Your account has been created successfully.</p>" +
                "<p>You can now:</p>" +
                "<ul style='color: #666;'>" +
                "<li>Browse our delicious food items</li>" +
                "<li>Place orders for delivery</li>" +
                "<li>Track your orders in real-time</li>" +
                "<li>Rate and review your favorite dishes</li>" +
                "</ul>" +
                "<p><a href='http://localhost:8080/products' style='display: inline-block; background-color: #ff4d4d; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 15px;'>Start Ordering Now</a></p>" +
                "<p style='color: #666; font-size: 12px; margin-top: 20px;'>Happy ordering from Food Express!</p>" +
                "</div>" +
                "</div>" +
                "</body></html>";
        
        sendHtmlEmail(to, "Welcome to Food Express!", htmlContent);
    }
}