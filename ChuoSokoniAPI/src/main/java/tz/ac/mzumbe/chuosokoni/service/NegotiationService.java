package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class NegotiationService {

    // Configurable business rule constants (BR-008, BR-009, BR-010)
    private static final BigDecimal DEFAULT_AGENT_COMMISSION = new BigDecimal("500.00"); // TZS 500
    private static final BigDecimal MINIMUM_PLATFORM_FEE = new BigDecimal("500.00");    // TZS 500
    private static final BigDecimal PLATFORM_FEE_PERCENTAGE = new BigDecimal("0.05");   // 5%
    private static final int MAX_NEGOTIATION_ROUNDS = 5; // SRS FR-NEG-009 default cap

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Enforces the minimum-offer floor rule (BR-010 & FR-NEG-007): Minimum
     * Floor = Seller Minimum Price + Agent Commission + Platform Fee
     */
    public BigDecimal calculateMinimumFloorPrice(BigDecimal sellerMinimumPrice) {
        if (sellerMinimumPrice == null || sellerMinimumPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Seller minimum price must be greater than zero.");
        }

        BigDecimal percentageFee = sellerMinimumPrice.multiply(PLATFORM_FEE_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal platformFee = percentageFee.max(MINIMUM_PLATFORM_FEE);

        return sellerMinimumPrice.add(DEFAULT_AGENT_COMMISSION).add(platformFee);
    }

    /**
     * Validates and submits an initial offer or counter-offer (FR-NEG-002,
     * FR-NEG-007).
     */
    @Transactional
    public Map<String, Object> submitOffer(
            Integer productId,
            Integer buyerUserId,
            BigDecimal proposedOfferPrice,
            BigDecimal sellerMinimumPrice,
            int currentRound
    ) {
        if (currentRound > MAX_NEGOTIATION_ROUNDS) {
            throw new IllegalStateException("Maximum negotiation rounds (" + MAX_NEGOTIATION_ROUNDS + ") reached.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        User buyer = userRepository.findById(buyerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer user not found with ID: " + buyerUserId));

        // Enforce price floor protection (BR-010 / FR-NEG-008)
        BigDecimal minimumFloor = calculateMinimumFloorPrice(sellerMinimumPrice);
        if (proposedOfferPrice.compareTo(minimumFloor) < 0) {
            throw new IllegalArgumentException(
                    "Offer TZS " + proposedOfferPrice + " is below the minimum required price floor of TZS " + minimumFloor
            );
        }

        Map<String, Object> response = new HashMap<>();
        response.put("productId", product.getProductId());
        response.put("buyerUserId", buyer.getUserId());
        response.put("proposedOfferPrice", proposedOfferPrice);
        response.put("minimumFloorPrice", minimumFloor);
        response.put("roundNumber", currentRound);
        response.put("negotiationStatus", "PENDING_SELLER_RESPONSE");
        response.put("timestamp", LocalDateTime.now());

        return response;
    }

    /**
     * Seller responds to a buyer's offer (ACCEPT, REJECT, COUNTER)
     * (FR-NEG-003).
     */
    @Transactional
    public Map<String, Object> respondToOffer(
            Integer productId,
            Integer sellerUserId,
            String action,
            BigDecimal counterOfferPrice,
            BigDecimal sellerMinimumPrice,
            int currentRound
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        User seller = userRepository.findById(sellerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Seller user not found with ID: " + sellerUserId));

        String sanitizedAction = action.toUpperCase().trim();
        Map<String, Object> response = new HashMap<>();
        response.put("productId", product.getProductId());
        response.put("sellerUserId", seller.getUserId());

        switch (sanitizedAction) {
            case "ACCEPT" -> {
                response.put("negotiationStatus", "ACCEPTED");
                response.put("message", "Seller accepted the offer. Price is locked for checkout."); // FR-NEG-008
            }
            case "REJECT" -> {
                response.put("negotiationStatus", "TERMINATED");
                response.put("message", "Seller rejected the offer. Negotiation ended.");
            }
            case "COUNTER" -> {
                if (counterOfferPrice == null) {
                    throw new IllegalArgumentException("Counter-offer price is required.");
                }
                BigDecimal minimumFloor = calculateMinimumFloorPrice(sellerMinimumPrice);
                if (counterOfferPrice.compareTo(minimumFloor) < 0) {
                    throw new IllegalArgumentException("Counter-offer cannot be lower than floor price: TZS " + minimumFloor);
                }
                response.put("counterOfferPrice", counterOfferPrice);
                response.put("minimumFloorPrice", minimumFloor);
                response.put("roundNumber", currentRound + 1);
                response.put("negotiationStatus", "PENDING_BUYER_RESPONSE");
            }
            default ->
                throw new IllegalArgumentException("Invalid action: " + action + ". Use ACCEPT, REJECT, or COUNTER.");
        }

        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    /**
     * Locks the final agreed price for order checkout (FR-NEG-008).
     */
    public Map<String, Object> lockFinalPrice(
            Integer productId,
            BigDecimal finalAgreedPrice,
            BigDecimal sellerMinimumPrice
    ) {
        BigDecimal minimumFloor = calculateMinimumFloorPrice(sellerMinimumPrice);
        if (finalAgreedPrice.compareTo(minimumFloor) < 0) {
            throw new IllegalStateException("Cannot lock final price below floor threshold of TZS " + minimumFloor);
        }

        Map<String, Object> lockData = new HashMap<>();
        lockData.put("productId", productId);
        lockData.put("finalAgreedPrice", finalAgreedPrice);
        lockData.put("isLocked", true);
        lockData.put("lockedAt", LocalDateTime.now());

        return lockData;
    }
}
