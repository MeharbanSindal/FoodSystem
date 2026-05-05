package com.foodexpress.dto;

import java.io.Serializable;

public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;
    private String productName;
    private String productImage;
    private Double unitPrice;
    private Integer quantity;
    private Double subTotal;

    public CartItem() {}

    public CartItem(Long productId, String productName, String productImage, Double unitPrice, Integer quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        updateSubTotal();
    }

    // Safe calculation
    public void updateSubTotal() {
        if (unitPrice != null && quantity != null) {
            this.subTotal = unitPrice * quantity;
        } else {
            this.subTotal = 0.0;
        }
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { 
        this.unitPrice = unitPrice; 
        updateSubTotal();
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
        updateSubTotal();
    }

    public Double getSubTotal() { return subTotal; }
}