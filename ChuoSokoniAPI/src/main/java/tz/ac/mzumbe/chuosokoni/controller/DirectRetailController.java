package tz.ac.mzumbe.chuosokoni.controller;

import java.math.BigDecimal;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.DirectRetailService;

@RestController
@RequestMapping("/api/v1/direct-retail")
@CrossOrigin(origins = "*")
public class DirectRetailController {

    @Autowired
    private DirectRetailService directRetailService;

    /**
     * Calculates fee allocations for a direct retail sale (BR-008, BR-009).
     * Expected JSON body: { "finalAgreedPrice" }
     */
    @PostMapping("/calculate-fees")
    public ResponseEntity<Map<String, BigDecimal>> calculateDirectRetailFees(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("finalAgreedPrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            BigDecimal finalAgreedPrice = new BigDecimal(request.get("finalAgreedPrice").toString());
            Map<String, BigDecimal> response = directRetailService.calculateDirectRetailFees(finalAgreedPrice);

            return ResponseEntity.ok(response);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Executes direct retail checkout / sale processing. Expected JSON body: {
     * "productId", "buyerUserId", "finalSalePrice" }
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> processDirectRetailCheckout(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("productId") == null || request.get("buyerUserId") == null || request.get("finalSalePrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "productId, buyerUserId, and finalSalePrice are required."));
            }

            Integer productId = Integer.parseInt(request.get("productId").toString());
            Integer buyerUserId = Integer.parseInt(request.get("buyerUserId").toString());
            BigDecimal finalSalePrice = new BigDecimal(request.get("finalSalePrice").toString());

            Map<String, Object> response = directRetailService.processDirectRetailCheckout(productId, buyerUserId, finalSalePrice);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process direct retail checkout: " + e.getMessage()));
        }
    }
}
