package com.foodexpress.controller;

import com.foodexpress.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        model.addAttribute("assignedOrders", orderService.getAssignedOrdersForDeliveryBoy(principal.getName()));
        return "delivery/dashboard";
    }

    @GetMapping("/orders")
    public String orders(Principal principal, Model model) {
        model.addAttribute("assignedOrders", orderService.getAssignedOrdersForDeliveryBoy(principal.getName()));
        return "delivery/dashboard";
    }

    @PostMapping("/order/status")
    public String updateOrderStatus(@RequestParam("orderId") Long orderId,
                                    @RequestParam("status") String status,
                                    @RequestParam(value = "otp", required = false) String otp,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        boolean updated = orderService.updateStatusByDeliveryBoy(orderId, status, principal.getName(), otp);

        if (updated) {
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to update order status.");
        }

        return "redirect:/delivery/orders";
    }
}
