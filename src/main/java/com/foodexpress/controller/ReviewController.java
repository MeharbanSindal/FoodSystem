package com.foodexpress.controller;

import com.foodexpress.model.Review;
import com.foodexpress.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<?> addReview(
            @RequestParam("productId") Long productId,
            @RequestParam("orderId") Long orderId,
            @RequestParam("rating") Integer rating,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "comment", required = false) String comment,
            Principal principal) {

        try {
            if (rating == null || rating < 1 || rating > 5) {
                return ResponseEntity.badRequest().body(Map.of("error", "Rating must be between 1 and 5"));
            }

            // Get user ID from email (would need user service to get ID)
            // For now, we'll need to modify this to pass user ID properly
            // This is a limitation of current architecture
            
            Review review = reviewService.addReview(1L, productId, orderId, rating, title, comment);
            
            if (review == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to add review"));
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Review added successfully",
                    "reviewId", review.getId()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable("productId") Long productId) {
        try {
            List<Review> reviews = reviewService.getReviewsForProduct(productId);
            Double avgRating = reviewService.getAverageRating(productId);
            Long reviewCount = reviewService.getReviewCount(productId);

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);
            response.put("averageRating", avgRating);
            response.put("reviewCount", reviewCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<?> markHelpful(@PathVariable("reviewId") Long reviewId) {
        try {
            reviewService.markHelpful(reviewId);
            return ResponseEntity.ok(Map.of("status", "success"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable("reviewId") Long reviewId, Principal principal) {
        try {
            boolean deleted = reviewService.deleteReview(reviewId, principal.getName());
            
            if (!deleted) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cannot delete this review"));
            }

            return ResponseEntity.ok(Map.of("status", "success", "message", "Review deleted successfully"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Server error: " + e.getMessage()));
        }
    }
}
