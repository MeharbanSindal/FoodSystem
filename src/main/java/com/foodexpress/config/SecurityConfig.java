package com.foodexpress.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.foodexpress.model.User;
import com.foodexpress.service.UserService;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.foodexpress.service")
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/resources/**", "/webjars/**", "/css/**", "/js/**", "/uploads/**").permitAll()
                .requestMatchers("/", "/products", "/products/**", "/login", "/register", "/forgot-password", "/authenticate", "/admin/login", "/admin/authenticate", "/delivery/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/delivery/**").hasRole("DELIVERY")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )

            .authenticationProvider(authenticationProvider())

            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler((request, response, authentication) -> {
                    HttpSession session = request.getSession();
                    User user = userService.findByEmail(authentication.getName());
                    if (user != null) {
                        session.setAttribute("loggedUser", user);
                    }

                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
                    boolean isDelivery = authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_DELIVERY".equals(a.getAuthority()));

                    if (isAdmin) {
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                    } else if (isDelivery) {
                        response.sendRedirect(request.getContextPath() + "/delivery/dashboard");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/user/dashboard");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )

            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                            .getContext()
                            .getAuthentication();

                    if (authentication != null && authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                        return;
                    }

                    if (authentication != null && authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_DELIVERY".equals(a.getAuthority()))) {
                        response.sendRedirect(request.getContextPath() + "/delivery/dashboard");
                        return;
                    }

                    if (authentication != null && authentication.getAuthorities().stream()
                            .anyMatch(a -> "ROLE_USER".equals(a.getAuthority()))) {
                        response.sendRedirect(request.getContextPath() + "/user/dashboard");
                        return;
                    }

                    response.sendRedirect(request.getContextPath() + "/login");
                })
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )

            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}