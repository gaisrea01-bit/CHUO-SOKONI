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
import tz.ac.mzumbe.chuosokoni.model.Investor;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.AgentRepository;
import tz.ac.mzumbe.chuosokoni.repository.InvestorRepository;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class WholesaleService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentRepository agentRepository;

    /**
     * Fetches all approved products available for wholesale investment[cite: 2, 3].
     */
    public List<Map<String, Object>> getWholesaleOpportunities() {
        List<Product> products = productRepository.findByStatus("APPROVED");
        return products.stream().map(this::buildWholesaleOpportunityMap).toList();
    }

    /**
     * Investor acquires stock units under the Wholesale-to-Investor model[cite: 1, 3].
     * Standard wholesale acquisition price is calculated as 90% of approved retail price[cite: 1, 3].
     */
    @Transactional
    public Map<String, Object> acquireWholesaleStock(
            Integer investorUserId,
            Integer productId,
            Integer quantityToAcquire,
            BigDecimal retailUnitPrice
    ) {
        if (quantityToAcquire == null || quantityToAcquire <= 0) {
            throw new IllegalArgumentException("Quantity to acquire must be greater than zero.");
        }

        Investor investor = investorRepository.findByUserUserId(investorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Investor profile not found for user ID: " + investorUserId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (!"APPROVED".equalsIgnoreCase(product.getStatus())) {
            throw new IllegalStateException("Product is not approved for wholesale investment.");
        }

        if (product.getStockQuantity() < quantityToAcquire) {
            throw new IllegalArgumentException("Requested quantity exceeds available stock.");
        }

        // Calculate wholesale acquisition price (90% target)[cite: 1, 3]
        BigDecimal wholesaleUnitPrice = retailUnitPrice.multiply(new BigDecimal("0.90"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWholesaleCost = wholesaleUnitPrice.multiply(new BigDecimal(quantityToAcquire));

        // Deduct available capital if tracked on investor profile[cite: 2]
        if (investor.getAvailableCapital() != null) {
            if (investor.getAvailableCapital().compareTo(totalWholesaleCost) < 0) {
                throw new IllegalStateException("Insufficient available investor capital.");
            }
            investor.setAvailableCapital(investor.getAvailableCapital().subtract(totalWholesaleCost));
            investorRepository.save(investor);
        }

        // Reduce product stock quantity as it transitions into managed platform inventory[cite: 2, 3]
        product.setStockQuantity(product.getStockQuantity() - quantityToAcquire);
        productRepository.save(product);

        Map<String, Object> response = new HashMap<>();
        response.put("investorId", investor.getInvestorId());
        response.put("productId", product.getProductId());
        response.put("productName", product.getName());
        response.put("acquiredQuantity", quantityToAcquire);
        response.put("retailUnitPrice", retailUnitPrice);
        response.put("wholesaleUnitPrice", wholesaleUnitPrice);
        response.put("totalWholesaleCost", totalWholesaleCost);
        response.put("status", "ACQUIRED");
        response.put("acquiredAt", LocalDateTime.now());

        return response;
    }

    /**
     * Executes wholesale margin distribution upon successful customer resale[cite: 1, 3].
     * Margin = Approved Retail Price - Wholesale Acquisition Price[cite: 1, 3]
     * Shares: 35% Investor Return, 25% Agent Commission, 40% Platform Revenue[cite: 3]
     */
    @Transactional
    public Map<String, Object> processWholesaleResaleMargin(
            BigDecimal retailUnitPrice,
            BigDecimal wholesaleUnitPrice,
            Integer customerUserId
    ) {
        BigDecimal commercialMargin = retailUnitPrice.subtract(wholesaleUnitPrice);

        BigDecimal investorReturn = commercialMargin.multiply(new BigDecimal("0.35"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal agentCommission = commercialMargin.multiply(new BigDecimal("0.25"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal platformRevenue = commercialMargin.multiply(new BigDecimal("0.40"))
                .setScale(2, RoundingMode.HALF_UP);

        // Credit agent commission if customer was referred by an active agent[cite: 2, 3]
        User customerUser = userRepository.findById(customerUserId).orElse(null);
        if (customerUser != null && customerUser.getReferredBy() != null) {
            agentRepository.findByUserUserId(customerUser.getReferredBy().getUserId())
                    .ifPresent(agent -> {
                        BigDecimal currentComm = agent.getTotalCommissions() != null ? agent.getTotalCommissions() : BigDecimal.ZERO;
                        agent.setTotalCommissions(currentComm.add(agentCommission));
                        agentRepository.save(agent);
                    });
        }

        Map<String, Object> distribution = new HashMap<>();
        distribution.put("retailUnitPrice", retailUnitPrice);
        distribution.put("wholesaleUnitPrice", wholesaleUnitPrice);
        distribution.put("commercialMargin", commercialMargin);
        distribution.put("investorReturn (35%)", investorReturn);
        distribution.put("agentCommission (25%)", agentCommission);
        distribution.put("platformRevenue (40%)", platformRevenue);

        return distribution;
    }

    /**
     * Helper to structure wholesale opportunities.
     */
    private Map<String, Object> buildWholesaleOpportunityMap(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", product.getProductId());
        map.put("productName", product.getName());
        map.put("categoryName", product.getCategory().getName());
        map.put("availableStock", product.getStockQuantity());
        map.put("unit", product.getUnit());
        map.put("status", product.getStatus());
        map.put("sellerBusinessName", product.getSeller().getBusinessName());
        return map;
    }
}