package com.ecommerce.multivendor.service;

import com.ecommerce.multivendor.entity.*;
import com.ecommerce.multivendor.repository.CartRepository;
import com.ecommerce.multivendor.repository.CartItemRepository;
import com.ecommerce.multivendor.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            return cartRepository.save(cart);
        });
    }

    public Cart addToCart(User user, Long productId, int quantity, String size) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already in cart
        for (CartItem item : cart.getCartItems()) {
            boolean sameProduct = item.getProduct().getId().equals(productId);
            boolean sameSize = (size == null && item.getSize() == null) || (size != null && size.equals(item.getSize()));
            if (sameProduct && sameSize) {
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
                return cart;
            }
        }

        CartItem newItem = new CartItem();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(quantity);
        newItem.setSize(size);
        cart.getCartItems().add(newItem);
        cartRepository.save(cart);
        return cart;
    }

    public Cart removeFromCart(User user, Long cartItemId) {
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
        return cart;
    }

    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}
