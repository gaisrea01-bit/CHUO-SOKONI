package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.model.OrderItem;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.model.Seller;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.OrderItemRepository;
import tz.ac.mzumbe.chuosokoni.repository.OrderRepository;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;
import tz.ac.mzumbe.chuosokoni.repository.SellerRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class RatingService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Submits a rating and review for a product after verifying customer order completion (FR-CUS-007)[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> submitProductRating(
            Integer buyerUserId,
            Integer productId,
            Integer orderId,
            Integer ratingValue,
            String reviewText
    ) {
        // 1. Validate rating bounds (1 to 5 stars)
        if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating value must be an integer between 1 and 5.");
        }

        User buyer = userRepository.findById(buyerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer user not found with ID: " + buyerUserId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // 2. Validate order ownership and completion (FR-CUS-007)[cite: 2, 3]
        if (!order.getBuyer().getUserId().equals(buyerUserId)) {
            throw new SecurityException("Unauthorized action. Customer does not own this order.");
        }

        if (!"COMPLETED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new IllegalStateException("Ratings can only be submitted for completed orders.");
        }

        // 3. Verify that the product was actually purchased in this order
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        boolean productInOrder = orderItems.stream()
                .anyMatch(item -> item.getProduct().getProductId().equals(productId));

        if (!productInOrder) {
            throw new IllegalArgumentException("Product with ID " + productId + " was not part of order ID " + orderId);
        }

        // 4. Update seller's aggregated rating profile[cite: 2]
        Seller seller = product.getSeller();
        updateSellerRatingAverage(seller, BigDecimal.valueOf(ratingValue));

        Map<String, Object> response = new HashMap<>();
        response.put("productId", product.getProductId());
        response.put("productName", product.getName());
        response.put("buyerUserId", buyer.getUserId());
        response.put("orderId", order.getOrderId());
        response.put("rating", ratingValue);
        response.put("review", reviewText);
        response.put("updatedSellerRating", seller.getRating());
        response.put("submittedAt", LocalDateTime.now());

        return response;
    }

    /**
     * Updates seller's cumulative average rating score in the database[cite: 2].
     */
    private void updateSellerRatingAverage(Seller seller, BigDecimal newRating) {
        BigDecimal currentRating = seller.getRating() != null ? seller.getRating() : BigDecimal.ZERO;

        // If initial rating, set directly; otherwise compute moving average
        if (currentRating.compareTo(BigDecimal.ZERO) == 0) {
            seller.setRating(newRating.setScale(2, RoundingMode.HALF_UP));
        } else {
            BigDecimal updatedAverage = currentRating.add(newRating)
                    .divide(new BigDecimal("2.00"), 2, RoundingMode.HALF_UP);
            seller.setRating(updatedAverage);
        }

        sellerRepository.save(seller);
    }
}