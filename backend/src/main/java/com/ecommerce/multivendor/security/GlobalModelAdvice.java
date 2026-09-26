package com.ecommerce.multivendor.security;

import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {
    private final UserRepository userRepository;

    @ModelAttribute
    public void addAuthAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String username = userDetails.getUsername();
            model.addAttribute("currentUser", username);
            model.addAttribute("isLoggedIn", true);

            userRepository.findByUsername(username).ifPresent(user ->
                model.addAttribute("currentRole", user.getRole().name())
            );
        } else {
            model.addAttribute("currentUser", null);
            model.addAttribute("currentRole", null);
            model.addAttribute("isLoggedIn", false);
        }
    }
}
