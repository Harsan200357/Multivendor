package com.ecommerce.multivendor.controller;

import com.ecommerce.multivendor.entity.*;
import com.ecommerce.multivendor.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAll());
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/";
    }

    @GetMapping("/products")
    public String productsPage(@RequestParam(required = false) String query,
                               @RequestParam(required = false) BigDecimal minPrice,
                               @RequestParam(required = false) BigDecimal maxPrice,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) String sort,
                               Model model) {
        List<Product> products;
        if (query == null && minPrice == null && maxPrice == null && categoryId == null) {
            products = new java.util.ArrayList<>(productService.getAllProducts());
        } else {
            products = new java.util.ArrayList<>(productService.searchAndFilterProducts(query, minPrice, maxPrice, categoryId));
        }

        if (sort != null) {
            if (sort.equals("price_asc")) {
                products.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
            } else if (sort.equals("price_desc")) {
                products.sort((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
            } else if (sort.equals("newest")) {
                products.sort((p1, p2) -> p2.getId().compareTo(p1.getId()));
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("query", query);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        model.addAttribute("categories", categoryService.getAll());
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetailsPage(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("reviews", reviewService.getReviewsByProduct(id));
        return "product";
    }

    @PostMapping("/products/{id}/review")
    public String submitReview(@PathVariable Long id, 
                               @RequestParam Integer rating, 
                               @RequestParam String comment, 
                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productService.getProductById(id);
        reviewService.addReview(product, user, rating, comment);
        return "redirect:/products/" + id;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);

        if (user.getRole() == Role.USER) {
            Cart cart = cartService.getOrCreateCart(user);
            model.addAttribute("orderCount", orderService.getOrdersByUser(user).size());
            model.addAttribute("cartCount", cart.getCartItems() != null ? cart.getCartItems().size() : 0);
        } else if (user.getRole() == Role.SELLER) {
            model.addAttribute("productCount", productService.getProductsBySeller(user).size());
            model.addAttribute("sellerOrderCount", orderService.getOrderItemsBySeller(user.getId()).size());
        } else if (user.getRole() == Role.ADMIN) {
            model.addAttribute("totalUsers", userService.findAll().size());
            model.addAttribute("totalProducts", productService.getAllProducts().size());
            model.addAttribute("totalOrders", orderService.getAllOrders().size());
        }
        return "dashboard";
    }

    @GetMapping("/cart")
    public String cartPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = cartService.getOrCreateCart(user);
        model.addAttribute("cart", cart);

        BigDecimal total = BigDecimal.ZERO;
        if (cart.getCartItems() != null) {
            for (CartItem item : cart.getCartItems()) {
                total = total.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        model.addAttribute("total", total);
        return "cart";
    }

    @GetMapping("/orders")
    public String ordersPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("orders", orderService.getOrdersByUser(user));
        return "orders";
    }
}
