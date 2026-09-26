package com.ecommerce.multivendor.controller;

import com.ecommerce.multivendor.dto.AuthResponse;
import com.ecommerce.multivendor.dto.LoginRequest;
import com.ecommerce.multivendor.dto.RegisterRequest;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.security.JwtUtil;
import com.ecommerce.multivendor.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, jakarta.servlet.http.HttpServletResponse response) {
        User user = userService.register(request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole());
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, jakarta.servlet.http.HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24);
        response.addCookie(cookie);

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole().name()));
    }

    @ExceptionHandler({RuntimeException.class, org.springframework.security.core.AuthenticationException.class})
    public ResponseEntity<java.util.Map<String, String>> handleExceptions(Exception ex) {
        java.util.Map<String, String> error = new java.util.HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).body(error);
    }
}
