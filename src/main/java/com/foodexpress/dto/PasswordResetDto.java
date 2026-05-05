package com.foodexpress.dto;

public class PasswordResetDto {

    private String token;
    private String email;
    private String newPassword;
    private String confirmPassword;

    public PasswordResetDto() {}

    // Validation method
    public boolean passwordsMatch() {
        return newPassword != null &&
               confirmPassword != null &&
               newPassword.equals(confirmPassword);
    }

    public boolean isValid() {
        return token != null && 
               email != null && 
               passwordsMatch();
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}