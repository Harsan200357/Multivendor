package com.ecommerce.multivendor.controller;

import com.ecommerce.multivendor.entity.Cart;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.service.CartService;
import com.ecommerce.multivendor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {
    private final CartService cartService;
    private final UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal UserDetails userDetails,
                                       @RequestBody Map<String, Object> body) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long productId = Long.valueOf(body.get("productId").toString());
        int quantity = body.containsKey("quantity") ? Integer.parseInt(body.get("quantity").toString()) : 1;
        String size = body.containsKey("size") ? body.get("size").toString() : null;
        cartService.addToCart(user, productId, quantity, size);
        return ResponseEntity.ok(Map.of("message", "Added to cart"));
    }  

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long cartItemId) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartService.removeFromCart(user, cartItemId);
        return ResponseEntity.ok(Map.of("message", "Removed from cart"));
    }
}
