package tz.ac.mzumbe.chuosokoni.controller;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.NegotiationService;

@RestController
@RequestMapping("/api/v1/negotiations")
@CrossOrigin(origins = "*")
public class NegotiationController {

    @Autowired
    private NegotiationService negotiationService;

    /**
     * Calculates the minimum floor price based on the seller's minimum price requirement (BR-010, FR-NEG-007).
     * Expected JSON body: { "sellerMinimumPrice" }
     */
    @PostMapping("/floor-price")
    public ResponseEntity<Map<String, Object>> calculateMinimumFloorPrice(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("sellerMinimumPrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "sellerMinimumPrice is required."));
            }

            BigDecimal sellerMinimumPrice = new BigDecimal(request.get("sellerMinimumPrice").toString());
            BigDecimal minimumFloorPrice = negotiationService.calculateMinimumFloorPrice(sellerMinimumPrice);

            return ResponseEntity.ok(Map.of(
                    "sellerMinimumPrice", sellerMinimumPrice,
                    "minimumFloorPrice", minimumFloorPrice
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to calculate floor price: " + e.getMessage()));
        }
    }

    /**
     * Validates and submits an initial offer or buyer counter-offer (FR-NEG-002, FR-NEG-007).
     * Expected JSON body: { "productId", "buyerUserId", "proposedOfferPrice", "sellerMinimumPrice", "currentRound" }
     */
    @PostMapping("/offers")
    public ResponseEntity<Map<String, Object>> submitOffer(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("productId") == null || request.get("buyerUserId") == null ||
                request.get("proposedOfferPrice") == null || request.get("sellerMinimumPrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "productId, buyerUserId, proposedOfferPrice, and sellerMinimumPrice are required."));
            }

            Integer productId = Integer.parseInt(request.get("productId").toString());
            Integer buyerUserId = Integer.parseInt(request.get("buyerUserId").toString());
            BigDecimal proposedOfferPrice = new BigDecimal(request.get("proposedOfferPrice").toString());
            BigDecimal sellerMinimumPrice = new BigDecimal(request.get("sellerMinimumPrice").toString());
            
            int currentRound = request.get("currentRound") != null ? 
                    Integer.parseInt(request.get("currentRound").toString()) : 1;

            Map<String, Object> response = negotiationService.submitOffer(
                    productId, buyerUserId, proposedOfferPrice, sellerMinimumPrice, currentRound
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to submit offer: " + e.getMessage()));
        }
    }

    /**
     * Seller responds to an offer with ACCEPT, REJECT, or COUNTER (FR-NEG-003).
     * Expected JSON body: { "productId", "sellerUserId", "action", "counterOfferPrice" (optional), "sellerMinimumPrice", "currentRound" }
     */
    @PostMapping("/offers/respond")
    public ResponseEntity<Map<String, Object>> respondToOffer(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("productId") == null || request.get("sellerUserId") == null ||
                request.get("action") == null || request.get("sellerMinimumPrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "productId, sellerUserId, action, and sellerMinimumPrice are required."));
            }

            Integer productId = Integer.parseInt(request.get("productId").toString());
            Integer sellerUserId = Integer.parseInt(request.get("sellerUserId").toString());
            String action = (String) request.get("action");
            
            BigDecimal counterOfferPrice = request.get("counterOfferPrice") != null ? 
                    new BigDecimal(request.get("counterOfferPrice").toString()) : null;
                    
            BigDecimal sellerMinimumPrice = new BigDecimal(request.get("sellerMinimumPrice").toString());
            
            int currentRound = request.get("currentRound") != null ? 
                    Integer.parseInt(request.get("currentRound").toString()) : 1;

            Map<String, Object> response = negotiationService.respondToOffer(
                    productId, sellerUserId, action, counterOfferPrice, sellerMinimumPrice, currentRound
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to respond to offer: " + e.getMessage()));
        }
    }

    /**
     * Locks the final agreed price for order checkout (FR-NEG-008).
     * Expected JSON body: { "productId", "finalAgreedPrice", "sellerMinimumPrice" }
     */
    @PostMapping("/lock-price")
    public ResponseEntity<Map<String, Object>> lockFinalPrice(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("productId") == null || request.get("finalAgreedPrice") == null || request.get("sellerMinimumPrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "productId, finalAgreedPrice, and sellerMinimumPrice are required."));
            }

            Integer productId = Integer.parseInt(request.get("productId").toString());
            BigDecimal finalAgreedPrice = new BigDecimal(request.get("finalAgreedPrice").toString());
            BigDecimal sellerMinimumPrice = new BigDecimal(request.get("sellerMinimumPrice").toString());

            Map<String, Object> response = negotiationService.lockFinalPrice(productId, finalAgreedPrice, sellerMinimumPrice);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to lock price: " + e.getMessage()));
        }
    }
}