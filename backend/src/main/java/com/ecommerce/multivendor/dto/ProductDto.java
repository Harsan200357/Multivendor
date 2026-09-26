package com.ecommerce.multivendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    @Positive
    private BigDecimal price;
    @NotNull
    @Positive
    private Integer stockQuantity;
    private Long categoryId;
    private String categoryName;
    private String sellerUsername;
    private List<String> imageUrls;
    private List<MultipartFile> imageFiles;
    private List<String> sizes;
}
