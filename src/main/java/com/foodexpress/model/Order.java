package com.foodexpress.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date")
    private Date orderDate = new Date();

    private Double subtotal;
    private Double gst;
    private Double deliveryCharge;
    private Double totalAmount;

    private String status;
    private String paymentMethod;
    private String deliveryBoy;
    @Column(length = 1000)
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
    @Column(unique = true)
    private String trackingCode;
    private String deliveryOtp;
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryOtpGeneratedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    public Order() {}

    // Calculation logic (safe)
    public void calculateFinalAmounts() {
        if (subtotal == null) subtotal = 0.0;

        this.gst = subtotal * 0.05;
        this.deliveryCharge = 40.0;
        this.totalAmount = subtotal + gst + deliveryCharge;
    }

    // Helper to set relation properly
    public void setItems(List<OrderItem> items) {
        this.items = items;
        if (items != null) {
            for (OrderItem item : items) {
                item.setOrder(this);
            }
        }
    }

    // Getters and Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Double getGst() { return gst; }
    public Double getDeliveryCharge() { return deliveryCharge; }
    public Double getTotalAmount() { return totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDeliveryBoy() { return deliveryBoy; }
    public void setDeliveryBoy(String deliveryBoy) { this.deliveryBoy = deliveryBoy; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public Double getDeliveryLatitude() { return deliveryLatitude; }
    public void setDeliveryLatitude(Double deliveryLatitude) { this.deliveryLatitude = deliveryLatitude; }

    public Double getDeliveryLongitude() { return deliveryLongitude; }
    public void setDeliveryLongitude(Double deliveryLongitude) { this.deliveryLongitude = deliveryLongitude; }

    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public String getDeliveryOtp() { return deliveryOtp; }
    public void setDeliveryOtp(String deliveryOtp) { this.deliveryOtp = deliveryOtp; }

    public Date getDeliveryOtpGeneratedAt() { return deliveryOtpGeneratedAt; }
    public void setDeliveryOtpGeneratedAt(Date deliveryOtpGeneratedAt) { this.deliveryOtpGeneratedAt = deliveryOtpGeneratedAt; }

    public List<OrderItem> getItems() { return items; }
}