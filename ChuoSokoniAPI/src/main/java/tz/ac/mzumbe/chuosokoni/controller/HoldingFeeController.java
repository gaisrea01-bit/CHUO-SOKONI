package tz.ac.mzumbe.chuosokoni.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;
import tz.ac.mzumbe.chuosokoni.service.HoldingFeeService;

@RestController
@RequestMapping("/api/v1/admin/holding-fees")
@CrossOrigin(origins = "*")
public class HoldingFeeController {

    @Autowired
    private HoldingFeeService holdingFeeService;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Evaluates listing age, grace period status (14 days), current tier, and accumulated holding fee for a product (FR-HLD-001 to FR-HLD-005).
     */
    @GetMapping("/evaluate/{productId}")
    public ResponseEntity<Map<String, Object>> evaluateListingHoldingFee(@PathVariable Integer productId) {
        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

            Map<String, Object> response = holdingFeeService.evaluateListingHoldingFee(product);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to evaluate listing holding fee: " + e.getMessage()));
        }
    }

    /**
     * Calculates net seller proceeds after deducting accumulated holding fees upon sale completion (FR-HLD-006).
     * Expected JSON body: { "grossSellerProceeds", "accumulatedHoldingFee" }
     */
    @PostMapping("/deduct")
    public ResponseEntity<Map<String, BigDecimal>> deductHoldingFeesFromSale(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("grossSellerProceeds") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            BigDecimal grossSellerProceeds = new BigDecimal(request.get("grossSellerProceeds").toString());
            
            BigDecimal accumulatedHoldingFee = request.get("accumulatedHoldingFee") != null ?
                    new BigDecimal(request.get("accumulatedHoldingFee").toString()) : BigDecimal.ZERO;

            Map<String, BigDecimal> response = holdingFeeService.deductHoldingFeesFromSale(
                    grossSellerProceeds, accumulatedHoldingFee
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Administrative Operation / Batch Audit: Scans all unsold active listings and generates holding fee reports (FR-HLD-007, FR-ADM-008).
     */
    @GetMapping("/report")
    public ResponseEntity<List<Map<String, Object>>> generateBatchHoldingFeeReport() {
        try {
            List<Map<String, Object>> report = holdingFeeService.generateBatchHoldingFeeReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}