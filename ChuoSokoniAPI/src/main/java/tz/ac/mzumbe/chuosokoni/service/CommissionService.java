package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Agent;
import tz.ac.mzumbe.chuosokoni.model.CommissionRecord;
import tz.ac.mzumbe.chuosokoni.model.CommissionReversal;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.repository.AgentRepository;
import tz.ac.mzumbe.chuosokoni.repository.CommissionRecordRepository;
import tz.ac.mzumbe.chuosokoni.repository.CommissionReversalRepository;
import tz.ac.mzumbe.chuosokoni.repository.OrderRepository;

@Service
public class CommissionService {

    @Autowired
    private CommissionRecordRepository commissionRecordRepository;

    @Autowired
    private CommissionReversalRepository commissionReversalRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Records a qualifying agent commission after a verified sale (FR-AGT-004)[cite: 3].
     */
    @Transactional
    public Map<String, Object> recordCommission(
            Integer agentId,
            Integer orderId,
            BigDecimal commissionAmount,
            String transactionType
    ) {
        if (commissionAmount == null || commissionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Commission amount must be greater than zero.");
        }

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + agentId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        CommissionRecord record = new CommissionRecord();
        record.setAgent(agent);
        record.setOrder(order);
        record.setAgentAmount(commissionAmount);
        record.setType(transactionType != null ? transactionType.toUpperCase() : "DIRECT_RETAIL");
        record.setStatus("EARNED");

        CommissionRecord savedRecord = commissionRecordRepository.save(record);

        // Update running total commission balance on Agent profile[cite: 2]
        BigDecimal currentTotal = agent.getTotalCommissions() != null ? agent.getTotalCommissions() : BigDecimal.ZERO;
        agent.setTotalCommissions(currentTotal.add(commissionAmount));
        agentRepository.save(agent);

        Map<String, Object> response = new HashMap<>();
        response.put("commissionId", savedRecord.getCommissionId());
        response.put("agentId", agent.getAgentId());
        response.put("orderId", order.getOrderId());
        response.put("amount", savedRecord.getAgentAmount());
        response.put("type", savedRecord.getType());
        response.put("status", savedRecord.getStatus());
        response.put("recordedAt", savedRecord.getCreatedAt());

        return response;
    }

    /**
     * Reverses a previously awarded commission in cases of order disputes or refunds[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> reverseCommission(
            Integer commissionId,
            String reason
    ) {
        CommissionRecord record = commissionRecordRepository.findById(commissionId)
                .orElseThrow(() -> new IllegalArgumentException("Commission record not found with ID: " + commissionId));

        if ("REVERSED".equalsIgnoreCase(record.getStatus())) {
            throw new IllegalStateException("Commission has already been reversed.");
        }

        record.setStatus("REVERSED");
        commissionRecordRepository.save(record);

        // Record reversal entry in commission_reversals table[cite: 2]
        CommissionReversal reversal = new CommissionReversal();
        reversal.setCommission(record);
        reversal.setAmount(record.getAgentAmount());
        reversal.setReason(reason != null ? reason : "Order refund or dispute resolution");
        reversal.setStatus("COMPLETED");

        CommissionReversal savedReversal = commissionReversalRepository.save(reversal);

        // Deduct commission from Agent running balance[cite: 2]
        Agent agent = record.getAgent();
        BigDecimal currentTotal = agent.getTotalCommissions() != null ? agent.getTotalCommissions() : BigDecimal.ZERO;
        BigDecimal newTotal = currentTotal.subtract(record.getAgentAmount()).max(BigDecimal.ZERO);
        agent.setTotalCommissions(newTotal);
        agentRepository.save(agent);

        Map<String, Object> response = new HashMap<>();
        response.put("reversalId", savedReversal.getReversalId());
        response.put("commissionId", record.getCommissionId());
        response.put("reversedAmount", savedReversal.getAmount());
        response.put("reason", savedReversal.getReason());
        response.put("reversedAt", savedReversal.getCreatedAt());

        return response;
    }

    /**
     * Retrieves commission ledger history for an individual agent[cite: 2, 3].
     */
    public List<Map<String, Object>> getAgentCommissions(Integer agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found with ID: " + agentId));

        List<CommissionRecord> records = commissionRecordRepository.findByAgent(agent);
        return records.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("commissionId", r.getCommissionId());
            map.put("orderId", r.getOrder().getOrderId());
            map.put("amount", r.getAgentAmount());
            map.put("type", r.getType());
            map.put("status", r.getStatus());
            map.put("createdAt", r.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
    }
}