package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Investor;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.repository.InvestorRepository;
import tz.ac.mzumbe.chuosokoni.repository.OrderRepository;

@Service
public class InvestorReturnService {

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Records an investor return share (35% of commercial margin) after a qualifying wholesale product resale[cite: 1, 3].
     */
    @Transactional
    public Map<String, Object> creditInvestorReturn(
            Integer investorUserId,
            Integer orderId,
            BigDecimal returnAmount
    ) {
        if (returnAmount == null || returnAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Return amount must be greater than zero.");
        }

        Investor investor = investorRepository.findByUserUserId(investorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Investor profile not found for user ID: " + investorUserId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        // Update available capital with earned return + principal payout
        BigDecimal currentCapital = investor.getAvailableCapital() != null ? investor.getAvailableCapital() : BigDecimal.ZERO;
        investor.setAvailableCapital(currentCapital.add(returnAmount));
        
        Investor savedInvestor = investorRepository.save(investor);

        Map<String, Object> response = new HashMap<>();
        response.put("investorId", savedInvestor.getInvestorId());
        response.put("orderId", order.getOrderId());
        response.put("creditedReturnAmount", returnAmount);
        response.put("updatedAvailableCapital", savedInvestor.getAvailableCapital());
        response.put("status", "CREDITED");
        response.put("timestamp", LocalDateTime.now());

        return response;
    }

    /**
     * Processes an investor capital withdrawal request[cite: 2].
     */
    @Transactional
    public Map<String, Object> requestCapitalWithdrawal(Integer investorUserId, BigDecimal withdrawalAmount) {
        if (withdrawalAmount == null || withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        Investor investor = investorRepository.findByUserUserId(investorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Investor profile not found for user ID: " + investorUserId));

        BigDecimal currentCapital = investor.getAvailableCapital() != null ? investor.getAvailableCapital() : BigDecimal.ZERO;

        if (currentCapital.compareTo(withdrawalAmount) < 0) {
            throw new IllegalStateException("Insufficient available capital for withdrawal.");
        }

        investor.setAvailableCapital(currentCapital.subtract(withdrawalAmount));
        Investor updatedInvestor = investorRepository.save(investor);

        Map<String, Object> response = new HashMap<>();
        response.put("investorId", updatedInvestor.getInvestorId());
        response.put("withdrawnAmount", withdrawalAmount);
        response.put("remainingCapital", updatedInvestor.getAvailableCapital());
        response.put("status", "PENDING_DISBURSEMENT");
        response.put("timestamp", LocalDateTime.now());

        return response;
    }

    /**
     * Fetches investor portfolio summary (Available Capital, Risk Preference, Investment Type)[cite: 2, 3].
     */
    public Map<String, Object> getInvestorPortfolioSummary(Integer investorUserId) {
        Investor investor = investorRepository.findByUserUserId(investorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Investor profile not found for user ID: " + investorUserId));

        Map<String, Object> summary = new HashMap<>();
        summary.put("investorId", investor.getInvestorId());
        summary.put("userId", investor.getUser().getUserId());
        summary.put("investmentType", investor.getInvestmentType());
        summary.put("availableCapital", investor.getAvailableCapital() != null ? investor.getAvailableCapital() : BigDecimal.ZERO);
        summary.put("riskPreference", investor.getRiskPreference());
        summary.put("status", investor.getStatus());
        summary.put("createdAt", investor.getCreatedAt());

        return summary;
    }
}