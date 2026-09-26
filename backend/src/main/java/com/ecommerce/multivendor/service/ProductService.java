package com.ecommerce.multivendor.service;

import com.ecommerce.multivendor.dto.ProductDto;
import com.ecommerce.multivendor.entity.Category;
import com.ecommerce.multivendor.entity.Product;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.repository.CategoryRepository;
import com.ecommerce.multivendor.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product addProduct(ProductDto dto, User seller) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setSeller(seller);
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            product.setCategory(cat);
        }
        product.setSizes(dto.getSizes() != null ? new ArrayList<>(dto.getSizes()) : new ArrayList<>());
        handleImage(product, dto);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductDto dto, User seller) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You can only edit your own products");
        }
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            product.setCategory(cat);
        }
        product.setSizes(dto.getSizes() != null ? new ArrayList<>(dto.getSizes()) : new ArrayList<>());
        handleImage(product, dto);
        return productRepository.save(product);
    }

    private void handleImage(Product product, ProductDto dto) {
        List<String> currentUrls = product.getImageUrls() != null ? product.getImageUrls() : new ArrayList<>();
        
        if (dto.getImageFiles() != null && !dto.getImageFiles().isEmpty()) {
            for (MultipartFile file : dto.getImageFiles()) {
                if (file.isEmpty()) continue;
                try {
                    String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                    Path uploadPath = Paths.get("uploads");
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    Path filePath = uploadPath.resolve(filename);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    currentUrls.add("/uploads/" + filename);
                } catch (Exception e) {
                    throw new RuntimeException("Could not store image file: " + e.getMessage());
                }
            }
        } 
        
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            for (String url : dto.getImageUrls()) {
                if (url != null && !url.trim().isEmpty() && !currentUrls.contains(url.trim())) {
                    currentUrls.add(url.trim());
                }
            }
        }
        
        product.setImageUrls(currentUrls);
    }

    public void deleteProduct(Long id, User seller) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new RuntimeException("You can only delete your own products");
        }
        productRepository.delete(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getProductsBySeller(User seller) {
        return productRepository.findBySeller(seller);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Product> searchAndFilterProducts(String query, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, Long categoryId) {
        if (query != null && query.trim().isEmpty()) {
            query = null;
        }
        return productRepository.searchAndFilterProducts(query, minPrice, maxPrice, categoryId);
    }

    public ProductDto toDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        if (product.getSeller() != null) {
            dto.setSellerUsername(product.getSeller().getUsername());
        }
        dto.setImageUrls(product.getImageUrls());
        dto.setSizes(product.getSizes() != null ? new ArrayList<>(product.getSizes()) : new ArrayList<>());
        return dto;
    }
}
