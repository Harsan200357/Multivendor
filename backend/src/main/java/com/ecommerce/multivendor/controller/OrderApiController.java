package com.ecommerce.multivendor.controller;

import com.ecommerce.multivendor.entity.Order;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.service.OrderService;
import com.ecommerce.multivendor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;
    private final UserService userService;

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Order order = orderService.placeOrder(user);
        return ResponseEntity.ok(Map.of("message", "Order placed successfully", "orderId", order.getId()));
    }
