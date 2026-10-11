package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Agent;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.model.ReferralCode;
import tz.ac.mzumbe.chuosokoni.model.Seller;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.AgentRepository;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;
import tz.ac.mzumbe.chuosokoni.repository.ReferralCodeRepository;
import tz.ac.mzumbe.chuosokoni.repository.SellerRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class DirectRetailService {

    // Target configuration parameters per SRS (BR-008 & BR-009)
    private static final BigDecimal DEFAULT_AGENT_COMMISSION = new BigDecimal("500.00"); // TZS 500
    private static final BigDecimal MINIMUM_PLATFORM_FEE = new BigDecimal("500.00");    // TZS 500
    private static final BigDecimal PLATFORM_FEE_PERCENTAGE = new BigDecimal("0.05");   // 5%

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ReferralCodeRepository referralCodeRepository;

    /**
     * Calculates fee allocations for a Direct Retail sale based on final agreed price (BR-008, BR-009)[cite: 3].
     * Platform Fee = max(TZS 500, 5% of final agreed price)[cite: 3]
     * Agent Commission = TZS 500[cite: 3]
     */
    public Map<String, BigDecimal> calculateDirectRetailFees(BigDecimal finalAgreedPrice) {
        if (finalAgreedPrice == null || finalAgreedPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Final agreed price must be greater than zero.");
        }

        // Calculate 5% of final price
        BigDecimal percentageFee = finalAgreedPrice.multiply(PLATFORM_FEE_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_UP);

        // Platform fee is max(TZS 500, 5% of price)
        BigDecimal platformFee = percentageFee.max(MINIMUM_PLATFORM_FEE);

        // Fixed agent commission target (TZS 500)
        BigDecimal agentCommission = DEFAULT_AGENT_COMMISSION;

        // Net Seller Proceeds = Final Price - (Platform Fee + Agent Commission)
        BigDecimal netSellerProceeds = finalAgreedPrice.subtract(platformFee).subtract(agentCommission);

        Map<String, BigDecimal> fees = new HashMap<>();
        fees.put("finalAgreedPrice", finalAgreedPrice);
        fees.put("platformFee", platformFee);
        fees.put("agentCommission", agentCommission);
        fees.put("netSellerProceeds", netSellerProceeds);

        return fees;
    }

    /**
     * Processes a completed Direct Retail sale, attributes commissions, and calculates net proceeds[cite: 3].
     */
    @Transactional
    public Map<String, Object> processDirectRetailSale(
            Integer productId,
            Integer buyerUserId,
            BigDecimal finalSalePrice
    ) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        if (product.getStockQuantity() < 1) {
            throw new IllegalStateException("Product is out of stock.");
        }

        User buyer = userRepository.findById(buyerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer user not found with ID: " + buyerUserId));

        Seller seller = product.getSeller();

        // 1. Calculate fees according to business rules[cite: 3]
        Map<String, BigDecimal> feeBreakdown = calculateDirectRetailFees(finalSalePrice);
        BigDecimal platformFee = feeBreakdown.get("platformFee");
        BigDecimal agentCommission = feeBreakdown.get("agentCommission");
        BigDecimal netSellerProceeds = feeBreakdown.get("netSellerProceeds");

        // 2. Reduce product stock by 1 unit[cite: 3]
        product.setStockQuantity(product.getStockQuantity() - 1);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        // 3. Attribute agent commission (BR-002, BR-008, FR-DIR-006)[cite: 3]
        // If buyer has a referring agent, credit that agent; otherwise commission retained by platform default[cite: 3]
        String commissionRecipient = "CHUO_SOKONI_DEFAULT";
        if (buyer.getReferredBy() != null) {
            Agent agent = agentRepository.findByUserUserId(buyer.getReferredBy().getUserId()).orElse(null);
            if (agent != null) {
                BigDecimal currentComm = agent.getTotalCommissions() != null ? agent.getTotalCommissions() : BigDecimal.ZERO;
                agent.setTotalCommissions(currentComm.add(agentCommission));
                agentRepository.save(agent);

                // Fetch active code from ReferralCodeRepository
                commissionRecipient = referralCodeRepository.findByAgentAndIsDefaultTrue(agent)
                        .or(() -> referralCodeRepository.findFirstByAgentOrderByCreatedAtDesc(agent))
                        .map(ReferralCode::getCode)
                        .orElse("UNKNOWN_AGENT_CODE");
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("productId", product.getProductId());
        response.put("productName", product.getName());
        response.put("sellerId", seller.getSellerId());
        response.put("sellerBusinessName", seller.getBusinessName());
        response.put("buyerUserId", buyer.getUserId());
        response.put("finalSalePrice", finalSalePrice);
        response.put("platformFee", platformFee);
        response.put("agentCommission", agentCommission);
        response.put("commissionRecipient", commissionRecipient);
        response.put("netSellerProceeds", netSellerProceeds);
        response.put("remainingStock", product.getStockQuantity());
        response.put("transactionStatus", "COMPLETED");
        response.put("processedAt", LocalDateTime.now());

        return response;
    }

    /**
     * Alias method for processing direct retail checkout[cite: 3].
     */
    @Transactional
    public Map<String, Object> processDirectRetailCheckout(
            Integer productId,
            Integer buyerUserId,
            BigDecimal finalSalePrice
    ) {
        return processDirectRetailSale(productId, buyerUserId, finalSalePrice);
    }
}