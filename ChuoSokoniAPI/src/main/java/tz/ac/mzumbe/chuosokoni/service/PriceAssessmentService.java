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
public class PriceAssessmentService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Conducts price assessment and approves retail and wholesale prices for a product.
     * Standard wholesale price target is 90% of approved retail price (10% commercial margin).
     */
    @Transactional
    public Map<String, Object> assessAndApprovePricing(
            Integer productId,
            Integer adminUserId,
            BigDecimal currentMarketPrice,
            BigDecimal competitorPrice,
            BigDecimal approvedRetailPrice,
            BigDecimal customWholesalePrice
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found with ID: " + adminUserId));

        // Validate administrative role permissions
        if (!"ADMINISTRATOR".equalsIgnoreCase(adminUser.getRole().getRoleName()) &&
            !"ADMIN".equalsIgnoreCase(adminUser.getRole().getRoleName())) {
            throw new SecurityException("Only administrative accounts can assess and approve pricing.");
        }

        if (approvedRetailPrice == null || approvedRetailPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Approved retail price must be greater than zero.");
        }

        // Calculate wholesale price: Default to 90% of approved retail price if custom price not provided[cite: 1, 3]
        BigDecimal calculatedWholesalePrice;
        if (customWholesalePrice != null && customWholesalePrice.compareTo(BigDecimal.ZERO) > 0) {
            calculatedWholesalePrice = customWholesalePrice;
        } else {
            calculatedWholesalePrice = approvedRetailPrice.multiply(new BigDecimal("0.90"))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Commercial Margin = Approved Retail Price - Wholesale Price[cite: 1, 3]
        BigDecimal commercialMargin = approvedRetailPrice.subtract(calculatedWholesalePrice);

        // Calculate distribution shares based on standard SRS rules (35% Investor, 25% Agent, 40% Platform)[cite: 3]
        BigDecimal investorShare = commercialMargin.multiply(new BigDecimal("0.35"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal agentShare = commercialMargin.multiply(new BigDecimal("0.25"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal platformShare = commercialMargin.multiply(new BigDecimal("0.40"))
                .setScale(2, RoundingMode.HALF_UP);

        // Approve and update product status[cite: 2, 3]
        product.setStatus("APPROVED");
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        Map<String, Object> response = new HashMap<>();
        response.put("productId", product.getProductId());
        response.put("productName", product.getName());
        response.put("reviewedByAdminId", adminUser.getUserId());
        response.put("currentMarketPrice", currentMarketPrice);
        response.put("competitorPrice", competitorPrice);
        response.put("approvedRetailPrice", approvedRetailPrice);
        response.put("wholesalePrice", calculatedWholesalePrice);
        response.put("commercialMargin", commercialMargin);
        
        Map<String, BigDecimal> marginDistribution = new HashMap<>();
        marginDistribution.put("investorShare (35%)", investorShare);
        marginDistribution.put("agentShare (25%)", agentShare);
        marginDistribution.put("platformShare (40%)", platformShare);
        response.put("marginDistribution", marginDistribution);
        
        response.put("productStatus", product.getStatus());
        response.put("assessedAt", LocalDateTime.now());

        return response;
    }

    
    
    /**
     * Computes quick wholesale projections for a given retail price proposal[cite: 1, 3].
     */
    public Map<String, Object> calculateWholesaleProjections(BigDecimal retailPrice) {
        if (retailPrice == null || retailPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Retail price must be greater than zero.");
        }

        BigDecimal wholesalePrice = retailPrice.multiply(new BigDecimal("0.90"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal margin = retailPrice.subtract(wholesalePrice);

        Map<String, Object> projection = new HashMap<>();
        projection.put("proposedRetailPrice", retailPrice);
        projection.put("targetWholesalePrice", wholesalePrice);
        projection.put("targetMargin", margin);
        projection.put("investorReturn", margin.multiply(new BigDecimal("0.35")).setScale(2, RoundingMode.HALF_UP));
        projection.put("agentCommission", margin.multiply(new BigDecimal("0.25")).setScale(2, RoundingMode.HALF_UP));
        projection.put("platformRevenue", margin.multiply(new BigDecimal("0.40")).setScale(2, RoundingMode.HALF_UP));

        return projection;
    }
}