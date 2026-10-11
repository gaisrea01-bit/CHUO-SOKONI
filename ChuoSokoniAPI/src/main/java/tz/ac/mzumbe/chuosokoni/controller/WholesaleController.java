package tz.ac.mzumbe.chuosokoni.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.WholesaleService;

@RestController
@RequestMapping("/api/v1/wholesale")
@CrossOrigin(origins = "*")
public class WholesaleController {

    @Autowired
    private WholesaleService wholesaleService;

    /**
     * Retrieves all approved wholesale opportunities available for investor bulk acquisition.
     */
    @GetMapping("/opportunities")
    public ResponseEntity<List<Map<String, Object>>> getWholesaleOpportunities() {
        try {
            List<Map<String, Object>> opportunities = wholesaleService.getWholesaleOpportunities();
            return ResponseEntity.ok(opportunities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Investor acquires stock units under the Wholesale-to-Investor model.
     * Expected JSON body: { "investorUserId", "productId", "quantityToAcquire", "retailUnitPrice" }
     */
    @PostMapping("/acquire")
    public ResponseEntity<Map<String, Object>> acquireWholesaleStock(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("investorUserId") == null || request.get("productId") == null ||
                request.get("quantityToAcquire") == null || request.get("retailUnitPrice") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "investorUserId, productId, quantityToAcquire, and retailUnitPrice are required."));
            }

            Integer investorUserId = Integer.parseInt(request.get("investorUserId").toString());
            Integer productId = Integer.parseInt(request.get("productId").toString());
            Integer quantityToAcquire = Integer.parseInt(request.get("quantityToAcquire").toString());
            BigDecimal retailUnitPrice = new BigDecimal(request.get("retailUnitPrice").toString());

            Map<String, Object> response = wholesaleService.acquireWholesaleStock(
                    investorUserId, productId, quantityToAcquire, retailUnitPrice
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to acquire wholesale stock: " + e.getMessage()));
        }
    }

    /**
     * Executes wholesale margin distribution upon successful customer resale.
     * Expected JSON body: { "retailUnitPrice", "wholesaleUnitPrice", "customerUserId" }
     */
    @PostMapping("/margin-distribution")
    public ResponseEntity<Map<String, Object>> processWholesaleResaleMargin(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("retailUnitPrice") == null || request.get("wholesaleUnitPrice") == null || request.get("customerUserId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "retailUnitPrice, wholesaleUnitPrice, and customerUserId are required."));
            }

            BigDecimal retailUnitPrice = new BigDecimal(request.get("retailUnitPrice").toString());
            BigDecimal wholesaleUnitPrice = new BigDecimal(request.get("wholesaleUnitPrice").toString());
            Integer customerUserId = Integer.parseInt(request.get("customerUserId").toString());

            Map<String, Object> response = wholesaleService.processWholesaleResaleMargin(
                    retailUnitPrice, wholesaleUnitPrice, customerUserId
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process resale margin distribution: " + e.getMessage()));
        }
    }
}