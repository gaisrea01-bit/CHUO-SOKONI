package tz.ac.mzumbe.chuosokoni.controller;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.PriceAssessmentService;

@RestController
@RequestMapping("/api/v1/admin/price-assessments")
@CrossOrigin(origins = "*")
public class PriceAssessmentController {

    @Autowired
    private PriceAssessmentService priceAssessmentService;

    /**
     * Admin Operation: Assesses a pending product, sets prices, and approves listing.
     * Expected JSON body: { "productId", "adminUserId", "currentMarketPrice", "competitorPrice", "approvedRetailPrice", "customWholesalePrice" }
     */
    @PostMapping("/assess")
    public ResponseEntity<Map<String, Object>> assessAndApprovePricing(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("productId") == null || request.get("adminUserId") == null || request.get("approvedRetailPrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "productId, adminUserId, and approvedRetailPrice are required."));
            }

            Integer productId = Integer.parseInt(request.get("productId").toString());
            Integer adminUserId = Integer.parseInt(request.get("adminUserId").toString());
            
            BigDecimal currentMarketPrice = request.get("currentMarketPrice") != null ? 
                    new BigDecimal(request.get("currentMarketPrice").toString()) : null;
            
            BigDecimal competitorPrice = request.get("competitorPrice") != null ? 
                    new BigDecimal(request.get("competitorPrice").toString()) : null;
            
            BigDecimal approvedRetailPrice = new BigDecimal(request.get("approvedRetailPrice").toString());
            
            BigDecimal customWholesalePrice = request.get("customWholesalePrice") != null ? 
                    new BigDecimal(request.get("customWholesalePrice").toString()) : null;

            Map<String, Object> response = priceAssessmentService.assessAndApprovePricing(
                    productId, adminUserId, currentMarketPrice, competitorPrice, approvedRetailPrice, customWholesalePrice
            );
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | SecurityException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to assess pricing: " + e.getMessage()));
        }
    }

    /**
     * Admin Preview: Calculates quick wholesale projections for a retail price proposal.
     * Expected JSON body: { "retailPrice" }
     */
    @PostMapping("/projections")
    public ResponseEntity<Map<String, Object>> calculateWholesaleProjections(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("retailPrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "retailPrice is required."));
            }

            BigDecimal retailPrice = new BigDecimal(request.get("retailPrice").toString());
            Map<String, Object> projections = priceAssessmentService.calculateWholesaleProjections(retailPrice);

            return ResponseEntity.ok(projections);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to calculate projections: " + e.getMessage()));
        }
    }
}