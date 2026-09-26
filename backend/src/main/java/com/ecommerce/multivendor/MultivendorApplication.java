package com.ecommerce.multivendor;

import com.ecommerce.multivendor.entity.Category;
import com.ecommerce.multivendor.entity.Role;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.repository.CategoryRepository;
import com.ecommerce.multivendor.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class MultivendorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultivendorApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(UserRepository userRepository,
									  CategoryRepository categoryRepository,
									  PasswordEncoder passwordEncoder) {
		return args -> {
			// Create default admin user
			if (!userRepository.existsByUsername("admin")) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setEmail("admin@emarket.com");
				admin.setPassword(passwordEncoder.encode("admin123"));
				admin.setRole(Role.ADMIN);
				userRepository.save(admin);
				System.out.println("Default Admin User created successfully: admin / admin123");
			}

			// Seed default categories
			List<String[]> defaultCategories = List.of(
				new String[]{"Electronics", "Smartphones, laptops, tablets, and electronic accessories"},
				new String[]{"Clothing & Fashion", "Men's, women's, and kids' clothing, shoes, and accessories"},
				new String[]{"Home & Kitchen", "Furniture, appliances, cookware, and home decor"},
				new String[]{"Books & Stationery", "Novels, textbooks, notebooks, and office supplies"},
				new String[]{"Health & Beauty", "Skincare, haircare, supplements, and personal care"},
				new String[]{"Sports & Outdoors", "Fitness equipment, sportswear, and outdoor gear"},
				new String[]{"Toys & Games", "Kids' toys, board games, puzzles, and collectibles"},
				new String[]{"Grocery & Food", "Packaged food, beverages, snacks, and organic products"},
				new String[]{"Automotive", "Car accessories, parts, tools, and maintenance supplies"},
				new String[]{"Jewelry & Watches", "Rings, necklaces, bracelets, and watches"}
			);

			for (String[] cat : defaultCategories) {
				if (categoryRepository.findByName(cat[0]).isEmpty()) {
					Category category = new Category();
					category.setName(cat[0]);
					category.setDescription(cat[1]);
					categoryRepository.save(category);
				}
			}
			System.out.println("Default categories seeded: " + categoryRepository.count() + " categories available");
		};
	}
}

