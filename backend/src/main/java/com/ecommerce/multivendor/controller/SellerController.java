package com.ecommerce.multivendor.controller;

import com.ecommerce.multivendor.dto.ProductDto;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.service.CategoryService;
import com.ecommerce.multivendor.service.OrderService;
import com.ecommerce.multivendor.service.ProductService;
import com.ecommerce.multivendor.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {
    private final ProductService productService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    @GetMapping("/products")
    public String myProducts(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User seller = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("products", productService.getProductsBySeller(seller));
        return "seller/products";
    }

    @GetMapping("/add-product")
    public String addProductPage(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        return "seller/add-product";
    }

    @PostMapping("/add-product")
    public String addProduct(@AuthenticationPrincipal UserDetails userDetails,
                             @ModelAttribute ProductDto productDto,
                             RedirectAttributes redirectAttributes) {
        try {
            User seller = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            productService.addProduct(productDto, seller);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller/add-product";
    }

    @GetMapping("/edit-product/{id}")
    public String editProductPage(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        User seller = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAll());
        return "seller/edit-product";
    }

    @PostMapping("/edit-product/{id}")
    public String editProduct(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              @ModelAttribute ProductDto productDto,
                              RedirectAttributes redirectAttributes) {
        try {
            User seller = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            productService.updateProduct(id, productDto, seller);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller/products";
    }

    @PostMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            User seller = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            productService.deleteProduct(id, seller);
            redirectAttributes.addFlashAttribute("success", "Product deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/seller/products";
    }

    @GetMapping("/dashboard")
    public String sellerDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User seller = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("products", productService.getProductsBySeller(seller));
        return "seller/dashboard";
    }

    @GetMapping("/orders")
    public String sellerOrders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User seller = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("orderItems", orderService.getOrderItemsBySeller(seller.getId()));
        return "seller/orders";
    }
}
