package com.foodexpress.service;

import com.foodexpress.dao.ReviewDataRepo;
import com.foodexpress.dao.ProductRepo;
import com.foodexpress.dao.OrderRepo;
import com.foodexpress.dao.UserRepo;
import com.foodexpress.model.Review;
import com.foodexpress.model.Product;
import com.foodexpress.model.Order;
import com.foodexpress.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewDataRepo reviewRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;

    /**
     * Add a review for a product after order delivery
     */
    public Review addReview(Long userId, Long productId, Long orderId, 
                          Integer rating, String title, String comment) {
        try {
            // Validate inputs
            if (rating == null || rating < 1 || rating > 5) {
                return null;
            }

            User user = userRepo.findById(userId);
            if (user == null) {
                return null;
            }

            Product product = productRepo.findById(productId);
            if (product == null) {
                return null;
            }

            Order order = orderRepo.findById(orderId);
            if (order == null || !order.getUser().getId().equals(userId)) {
                return null; // Order doesn't belong to user
            }

            // Check if user already reviewed this product for this order
            Optional<Review> existingReview = reviewRepo.findByUserIdAndProductIdAndOrderId(userId, productId, orderId);
            if (existingReview.isPresent()) {
                // Update existing review
                Review review = existingReview.get();
                review.setRating(rating);
                review.setTitle(title);
                review.setComment(comment);
                return reviewRepo.save(review);
            }

            // Create new review
            Review review = new Review();
            review.setUser(user);
            review.setProduct(product);
            review.setOrderId(orderId);
            review.setRating(rating);
            review.setTitle(title);
            review.setComment(comment);
            review.setVerifiedPurchase(true);

            return reviewRepo.save(review);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get all reviews for a product, sorted by most recent
     */
    public List<Review> getReviewsForProduct(Long productId) {
        return reviewRepo.findByProductIdOrderByCreatedAtDesc(productId);
    }

    /**
     * Get most helpful reviews for a product (top 5)
     */
    public List<Review> getMostHelpfulReviews(Long productId) {
        List<Review> reviews = reviewRepo.findMostHelpfulReviewsForProduct(productId);
        return reviews.size() > 5 ? reviews.subList(0, 5) : reviews;
    }

    /**
     * Get average rating for a product
     */
    public Double getAverageRating(Long productId) {
        Double avgRating = reviewRepo.getAverageRatingForProduct(productId);
        return avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
    }

    /**
     * Get total review count for a product
     */
    public Long getReviewCount(Long productId) {
        return reviewRepo.getReviewCountForProduct(productId);
    }

    /**
     * Get reviews by user
     */
    public List<Review> getReviewsByUser(String userEmail) {
        User user = userRepo.findByEmail(userEmail);
        if (user == null) {
            return List.of();
        }
        return reviewRepo.findByUserId(user.getId());
    }

    /**
     * Delete a review (only by user who posted it)
     */
    public boolean deleteReview(Long reviewId, String userEmail) {
        try {
            Review review = reviewRepo.findById(reviewId).orElse(null);
            if (review == null) {
                return false;
            }

            if (!review.getUser().getEmail().equalsIgnoreCase(userEmail)) {
                return false; // User not authorized
            }

            reviewRepo.deleteById(reviewId);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mark review as helpful
     */
    public void markHelpful(Long reviewId) {
        Review review = reviewRepo.findById(reviewId).orElse(null);
        if (review != null) {
            review.setHelpfulCount(review.getHelpfulCount() != null ? review.getHelpfulCount() + 1 : 1);
            reviewRepo.save(review);
        }
    }

    /**
     * Get review statistics for a product
     */
    public java.util.Map<String, Object> getProductReviewStats(Long productId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        Double avgRating = getAverageRating(productId);
        Long reviewCount = getReviewCount(productId);

        stats.put("averageRating", avgRating);
        stats.put("reviewCount", reviewCount);
        stats.put("percentage_5star", calculateStarPercentage(productId, 5));
        stats.put("percentage_4star", calculateStarPercentage(productId, 4));
        stats.put("percentage_3star", calculateStarPercentage(productId, 3));
        stats.put("percentage_2star", calculateStarPercentage(productId, 2));
        stats.put("percentage_1star", calculateStarPercentage(productId, 1));

        return stats;
    }

    private Double calculateStarPercentage(Long productId, Integer rating) {
        Long totalReviews = reviewRepo.getReviewCountForProduct(productId);
        if (totalReviews == 0) {
            return 0.0;
        }

        Long starCount = reviewRepo.countByRating(productId, rating);
        return (starCount.doubleValue() / totalReviews.doubleValue()) * 100;
    }
}
