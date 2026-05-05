package com.foodexpress.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "label", length = 40)
    private String label;

    @Column(name = "address_line", length = 500, nullable = false)
    private String addressLine;

    @Column(name = "city", length = 120)
    private String city;

    @Column(name = "state", length = 120)
    private String state;

    @Column(name = "pincode", length = 20)
    private String pincode;

    @Column(name = "landmark", length = 200)
    private String landmark;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_default")
    private boolean defaultAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getDisplayAddress() {
        StringBuilder builder = new StringBuilder();
        if (addressLine != null && !addressLine.isBlank()) {
            builder.append(addressLine.trim());
        }
        if (landmark != null && !landmark.isBlank()) {
            builder.append(builder.length() > 0 ? ", " : "").append(landmark.trim());
        }
        if (city != null && !city.isBlank()) {
            builder.append(builder.length() > 0 ? ", " : "").append(city.trim());
        }
        if (state != null && !state.isBlank()) {
            builder.append(builder.length() > 0 ? ", " : "").append(state.trim());
        }
        if (pincode != null && !pincode.isBlank()) {
            builder.append(builder.length() > 0 ? " - " : "").append(pincode.trim());
        }
        return builder.toString();
    }
}
