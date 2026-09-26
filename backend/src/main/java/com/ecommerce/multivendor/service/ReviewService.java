package com.ecommerce.multivendor.service;

import com.ecommerce.multivendor.entity.Product;
import com.ecommerce.multivendor.entity.Review;
import com.ecommerce.multivendor.entity.User;
import com.ecommerce.multivendor.repository.ProductRepository;
import com.ecommerce.multivendor.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Transactional
    public Review addReview(Product product, User user, Integer rating, String comment) {
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);

        Review savedReview = reviewRepository.save(review);

        // Increment the number of reviews on the product
        product.setNoOfReviews(product.getNoOfReviews() + 1);
        productRepository.save(product);

        return savedReview;
    }
}
