package com.ecommerce.multivendor.security;

import com.ecommerce.multivendor.entity.Role;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email != null) {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                User user = new User();
                user.setEmail(email);
                // Use email prefix or name as username, ensure it's unique
                String username = name != null ? name.replaceAll("\\s+", "").toLowerCase() : email.split("@")[0];
                if (userRepository.existsByUsername(username)) {
                    username = username + UUID.randomUUID().toString().substring(0, 5);
                }
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // dummy password
                user.setRole(Role.USER); // Default role
                userRepository.save(user);
            }
        }

        return oAuth2User;
    }
}
