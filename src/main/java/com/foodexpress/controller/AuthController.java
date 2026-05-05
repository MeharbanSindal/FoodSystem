package com.foodexpress.controller;

import com.foodexpress.model.User;
import com.foodexpress.service.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "registered", required = false) String registered,
                        Model model) {

        if (error != null) model.addAttribute("msg", "Invalid username or password!");
        if (logout != null) model.addAttribute("msg", "You have been logged out successfully.");
        if (registered != null) model.addAttribute("msg", "Registration successful! Please login.");

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {

        boolean isSaved = userService.registerUser(user);

        if (!isSaved) {
            model.addAttribute("error", "Email already exists! Try another.");
            return "auth/register";
        }

        return "redirect:/login?registered=true";
    }

    @GetMapping("/authenticate")
    public String authenticateRedirect() {
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgot(@RequestParam("email") String email, Model model) {

        boolean sent = userService.sendResetToken(email);

        if (sent) {
            model.addAttribute("msg", "Reset link sent to your email.");
        } else {
            model.addAttribute("error", "Email not found.");
        }

        return "auth/forgot-password";
    }

    /**
     * Admin login page
     */
    @GetMapping("/admin/login")
    public String adminLoginPage(@RequestParam(value = "error", required = false) String error,
                                 @RequestParam(value = "logout", required = false) String logout,
                                 Model model) {

        if (error != null) model.addAttribute("msg", "Invalid admin credentials!");
        if (logout != null) model.addAttribute("msg", "You have been logged out successfully.");

        return "auth/admin-login";
    }

    @GetMapping("/delivery/login")
    public String deliveryLoginPage(@RequestParam(value = "error", required = false) String error,
                                    @RequestParam(value = "logout", required = false) String logout,
                                    Model model) {

        // Development bootstrap for delivery workflow.
        userService.ensureDeliveryUser("delivery@foodexpress.com", "123", "Default Delivery Boy");

        if (error != null) model.addAttribute("msg", "Invalid delivery credentials!");
        if (logout != null) model.addAttribute("msg", "You have been logged out successfully.");

        return "auth/delivery-login";
    }

    /**
     * Admin login authentication
     */
    @PostMapping("/admin/authenticate")
    public String adminAuthenticate(@RequestParam("email") String email,
                                    @RequestParam("password") String password,
                                    HttpSession session) {

        // Development bootstrap: keep requested admin credentials active.
        userService.ensureAdminUser("meharbansindal@gmail.com", "123", "Admin User");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

            if (!isAdmin) {
                return "redirect:/admin/login?error=true";
            }

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            User user = userService.findByEmail(email);
            if (user != null) {
                session.setAttribute("loggedUser", user);
            }
            session.setAttribute("isAdmin", true);

            return "redirect:/admin/dashboard";
        } catch (AuthenticationException ex) {
            return "redirect:/admin/login?error=true";
        }
    }
}