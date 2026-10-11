package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tz.ac.mzumbe.chuosokoni.model.Payment;
import tz.ac.mzumbe.chuosokoni.repository.AgentRepository;
import tz.ac.mzumbe.chuosokoni.repository.DisputeRepository;
import tz.ac.mzumbe.chuosokoni.repository.OrderRepository;
import tz.ac.mzumbe.chuosokoni.repository.PaymentRepository;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class AdminDashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private DisputeRepository disputeRepository;

    /**
     * Generates a high-level system overview dashboard for administrative users (FR-ADM-005).
     */
    public Map<String, Object> getSystemOverview() {
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long totalAgents = agentRepository.count();
        long openDisputes = disputeRepository.findByStatusIn(List.of("OPEN", "UNDER_REVIEW")).size();

        Map<String, Object> overview = new HashMap<>();
        overview.put("totalUsers", totalUsers);
        overview.put("totalProducts", totalProducts);
        overview.put("totalOrders", totalOrders);
        overview.put("totalAgents", totalAgents);
        overview.put("openDisputes", openDisputes);

        return overview;
    }

    /**
     * Calculates gross platform revenue and financial metrics across successful payments (FR-ADM-005, FR-ADM-008).
     */
    public Map<String, Object> getFinancialSummary() {
        List<Payment> successfulPayments = paymentRepository.findAll().stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()) || "COMPLETED".equalsIgnoreCase(p.getStatus()))
                .toList();

        BigDecimal grossTransactionVolume = successfulPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Platform revenue target estimated based on completed transaction volume
        BigDecimal estimatedPlatformRevenue = grossTransactionVolume.multiply(new BigDecimal("0.05"));

        Map<String, Object> financialData = new HashMap<>();
        financialData.put("totalSuccessfulPayments", successfulPayments.size());
        financialData.put("grossTransactionVolume", grossTransactionVolume);
        financialData.put("estimatedPlatformRevenue", estimatedPlatformRevenue);
        financialData.put("currency", "TZS");

        return financialData;
    }

    /**
     * Retrieves listing approvals and review queues for products pending market assessment (FR-ADM-002, FR-ADM-003).
     */
    public Map<String, Object> getProductReviewQueueMetrics() {
        long pendingProductsCount = productRepository.findAll().stream()
                .filter(p -> "PENDING".equalsIgnoreCase(p.getStatus()))
                .count();

        long approvedProductsCount = productRepository.findAll().stream()
                .filter(p -> "APPROVED".equalsIgnoreCase(p.getStatus()))
                .count();

        Map<String, Object> queueData = new HashMap<>();
        queueData.put("pendingApprovalCount", pendingProductsCount);
        queueData.put("approvedProductsCount", approvedProductsCount);

        return queueData;
    }

    /**
     * Aggregates referral program metrics and agent performance summaries (FR-ADM-006)[cite: 3].
     */
    public Map<String, Object> getReferralProgramMetrics() {
        long totalAgents = agentRepository.count();
        long flaggedAgents = agentRepository.findAll().stream()
                .filter(a -> "FLAGGED".equalsIgnoreCase(a.getStatus()))
                .count();

        Map<String, Object> referralData = new HashMap<>();
        referralData.put("totalRegisteredAgents", totalAgents);
        referralData.put("flaggedAgentsCount", flaggedAgents);

        return referralData;
    }
}