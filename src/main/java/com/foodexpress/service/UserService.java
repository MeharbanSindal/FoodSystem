package com.foodexpress.service;

import com.foodexpress.dao.UserRepo;
import com.foodexpress.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired 
    private UserRepo userRepo;

    @Autowired 
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired 
    private EmailService emailService;

    @Transactional
    public boolean registerUser(User user) {

        if (userRepo.findByEmail(user.getEmail()) != null) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setEnabled(true);

        userRepo.save(user);

        emailService.sendWelcomeEmail(
                user.getEmail(),
                user.getFullName()
        );

        return true;
    }

    @Transactional
    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Transactional
    public boolean sendResetToken(String email) {

        User user = userRepo.findByEmail(email);

        if (user != null) {

            emailService.sendSimpleEmail(
                    email,
                    "Password Reset",
                    "Click to reset: http://localhost:8080/forgot-password?token=XYZ"
            );

            return true;
        }

        return false;
    }

    @Transactional
    public void ensureAdminUser(String email, String rawPassword, String fullName) {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setEnabled(true);
            user.setRole("ROLE_ADMIN");
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepo.save(user);
            return;
        }

        user.setFullName(fullName);
        user.setEnabled(true);
        user.setRole("ROLE_ADMIN");
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepo.update(user);
    }

    @Transactional
    public void ensureDeliveryUser(String email, String rawPassword, String fullName) {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setMobileNumber("9999999999");
            user.setEnabled(true);
            user.setRole("ROLE_DELIVERY");
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepo.save(user);
            return;
        }

        user.setFullName(fullName);
        user.setEnabled(true);
        user.setRole("ROLE_DELIVERY");
        if (user.getMobileNumber() == null || user.getMobileNumber().trim().isEmpty()) {
            user.setMobileNumber("9999999999");
        }
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepo.update(user);
    }

    @Transactional
    public List<User> getUsersByRole(String role) {
        return userRepo.findByRole(role);
    }

        @Transactional
        public List<User> getAllUsers() {
            return userRepo.findAll();
        }

    @Transactional
    public boolean createDeliveryUser(String fullName, String email, String mobileNumber, String rawPassword) {
        if (email == null || userRepo.findByEmail(email) != null) {
            return false;
        }

        String normalizedMobile = mobileNumber == null ? "" : mobileNumber.trim();
        if (normalizedMobile.isEmpty()) {
            return false;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setMobileNumber(normalizedMobile);
        user.setEnabled(true);
        user.setRole("ROLE_DELIVERY");
        user.setPassword(passwordEncoder.encode(rawPassword));
        userRepo.save(user);
        return true;
    }
}