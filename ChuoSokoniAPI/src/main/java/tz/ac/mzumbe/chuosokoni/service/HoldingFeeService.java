package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;

@Service
public class HoldingFeeService {

    // Configurable pilot parameters per SRS (BR-011, FR-HLD-002 to FR-HLD-004)
    private static final long GRACE_PERIOD_DAYS = 14; 
    private static final BigDecimal BASE_HOLDING_FEE_PER_TIER = new BigDecimal("1000.00"); // Pilot baseline: TZS 1,000 per 14-day period

    @Autowired
    private ProductRepository productRepository;

    /**
     * Evaluates the age of a Direct Retail listing and calculates accumulated holding fees (FR-HLD-001 to FR-HLD-005).
     */
    public Map<String, Object> evaluateListingHoldingFee(Product product) {
        if (product.getCreatedAt() == null) {
            throw new IllegalArgumentException("Product creation timestamp is missing.");
        }

        LocalDateTime now = LocalDateTime.now();
        long daysActive = ChronoUnit.DAYS.between(product.getCreatedAt(), now);

        BigDecimal accumulatedFee = BigDecimal.ZERO;
        int feeTier = 0;
        boolean isFeeFreePeriod = true;

        // SRS Rule: First 14 days are completely fee-free (FR-HLD-002, BR-011)
        if (daysActive > GRACE_PERIOD_DAYS) {
            isFeeFreePeriod = false;
            long daysSubjectToFee = daysActive - GRACE_PERIOD_DAYS;
            
            // Calculates escalating 14-day fee tiers (FR-HLD-003, FR-HLD-004)
            feeTier = (int) Math.ceil((double) daysSubjectToFee / GRACE_PERIOD_DAYS);
            
            // Tier 1 = Base Fee, Tier 2 = 2x Base Fee, Tier 3 = 3x Base Fee (Escalating logic)
            BigDecimal totalEscalatedFee = BigDecimal.ZERO;
            for (int t = 1; t <= feeTier; t++) {
                totalEscalatedFee = totalEscalatedFee.add(BASE_HOLDING_FEE_PER_TIER.multiply(new BigDecimal(t)));
            }
            accumulatedFee = totalEscalatedFee;
        }

        Map<String, Object> feeData = new HashMap<>();
        feeData.put("productId", product.getProductId());
        feeData.put("productName", product.getName());
        feeData.put("listingCreatedAt", product.getCreatedAt());
        feeData.put("daysActive", daysActive);
        feeData.put("isFeeFreePeriod", isFeeFreePeriod);
        feeData.put("currentFeeTier", feeTier);
        feeData.put("accumulatedHoldingFee", accumulatedFee);

        return feeData;
    }

    /**
     * Deducts accumulated holding fee balance from gross sale proceeds upon completed sale (FR-HLD-006).
     */
    @Transactional
    public Map<String, BigDecimal> deductHoldingFeesFromSale(
            BigDecimal grossSellerProceeds,
            BigDecimal accumulatedHoldingFee
    ) {
        if (grossSellerProceeds == null || grossSellerProceeds.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gross seller proceeds must be greater than zero.");
        }

        BigDecimal actualFeeToDeduct = accumulatedHoldingFee != null ? accumulatedHoldingFee : BigDecimal.ZERO;

        // Ensure holding fee deduction does not result in negative net proceeds
        if (actualFeeToDeduct.compareTo(grossSellerProceeds) > 0) {
            actualFeeToDeduct = grossSellerProceeds; // Cap deduction to gross proceeds
        }

        BigDecimal netSellerProceedsAfterHoldingFee = grossSellerProceeds.subtract(actualFeeToDeduct);

        Map<String, BigDecimal> deductionResult = new HashMap<>();
        deductionResult.put("grossSellerProceeds", grossSellerProceeds);
        deductionResult.put("holdingFeeDeducted", actualFeeToDeduct);
        deductionResult.put("netSellerProceeds", netSellerProceedsAfterHoldingFee);

        return deductionResult;
    }

    /**
     * Batch job or administrative check to scan unsold Direct Retail listings and generate holding fee reports (FR-HLD-007, FR-ADM-008).
     */
    public List<Map<String, Object>> generateBatchHoldingFeeReport() {
        List<Product> activeProducts = productRepository.findByStatus("APPROVED");
        return activeProducts.stream().map(this::evaluateListingHoldingFee).toList();
    }
}