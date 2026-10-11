package tz.ac.mzumbe.chuosokoni.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Dispute;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.DisputeRepository;
import tz.ac.mzumbe.chuosokoni.repository.OrderRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class DisputeService {

    @Autowired
    private DisputeRepository disputeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Submits a new dispute linked to an order (FR-DSP-001, FR-DSP-002)[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> createDispute(
            Integer orderId,
            Integer initiatedByUserId,
            String reason,
            String disputeType
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        User initiatedBy = userRepository.findById(initiatedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + initiatedByUserId));

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Dispute reason is required.");
        }

        Dispute dispute = new Dispute();
        dispute.setOrder(order);
        dispute.setInitiatedBy(initiatedBy);
        dispute.setReason(reason.trim());
        dispute.setDisputeType(disputeType != null ? disputeType.toUpperCase() : "REFUND_REQUEST");
        dispute.setStatus("OPEN"); // FR-DSP-002: Initial open state[cite: 3]

        Dispute savedDispute = disputeRepository.save(dispute);
        return buildDisputeResponseMap(savedDispute);
    }

    /**
     * Updates dispute evidence details (FR-DSP-002)[cite: 3].
     */
    @Transactional
    public Map<String, Object> addDisputeEvidence(Integer disputeId, String evidenceDetails) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new IllegalArgumentException("Dispute not found with ID: " + disputeId));

        if ("RESOLVED".equalsIgnoreCase(dispute.getStatus()) || "REJECTED".equalsIgnoreCase(dispute.getStatus())) {
            throw new IllegalStateException("Cannot append evidence to a closed or resolved dispute.");
        }

        dispute.setReason(dispute.getReason() + "\n\nEvidence Added (" + LocalDateTime.now() + "): " + evidenceDetails);
        dispute.setStatus("UNDER_REVIEW");
        
        Dispute savedDispute = disputeRepository.save(dispute);
        return buildDisputeResponseMap(savedDispute);
    }

    /**
     * Admin Operation: Reviews, resolves, or rejects disputes and records outcome (FR-DSP-003, FR-DSP-004)[cite: 3].
     */
    @Transactional
    public Map<String, Object> resolveDispute(
            Integer disputeId,
            Integer adminUserId,
            String newStatus,
            String resolution
    ) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new IllegalArgumentException("Dispute not found with ID: " + disputeId));

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found with ID: " + adminUserId));

        // Verify administrative authorization[cite: 2, 3]
        if (!"ADMINISTRATOR".equalsIgnoreCase(admin.getRole().getRoleName()) &&
            !"ADMIN".equalsIgnoreCase(admin.getRole().getRoleName())) {
            throw new SecurityException("Only administrators can resolve or reject disputes.");
        }

        String sanitizedStatus = newStatus.toUpperCase().trim();
        if (!List.of("RESOLVED", "REJECTED", "UNDER_REVIEW").contains(sanitizedStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus + ". Use RESOLVED, REJECTED, or UNDER_REVIEW.");
        }

        dispute.setStatus(sanitizedStatus);
        dispute.setResolution(resolution);
        
        // Update associated order status if dispute is resolved with refund[cite: 2, 3]
        if ("RESOLVED".equalsIgnoreCase(sanitizedStatus)) {
            Order order = dispute.getOrder();
            order.setOrderStatus("DISPUTE_RESOLVED_REFUNDED");
            orderRepository.save(order);
        }

        Dispute savedDispute = disputeRepository.save(dispute);
        return buildDisputeResponseMap(savedDispute);
    }

    /**
     * Retrieves all open or under-review disputes for admin audit queue (FR-DSP-003, FR-ADM-007)[cite: 3].
     */
    public List<Map<String, Object>> getPendingDisputes() {
        List<Dispute> disputes = disputeRepository.findByStatusIn(List.of("OPEN", "UNDER_REVIEW"));
        return disputes.stream().map(this::buildDisputeResponseMap).toList();
    }

    /**
     * Helper to package dispute response maps.
     */
    private Map<String, Object> buildDisputeResponseMap(Dispute dispute) {
        Map<String, Object> map = new HashMap<>();
        map.put("disputeId", dispute.getDisputeId());
        map.put("orderId", dispute.getOrder().getOrderId());
        map.put("initiatedByUserId", dispute.getInitiatedBy().getUserId());
        map.put("initiatedByName", dispute.getInitiatedBy().getFullName());
        map.put("disputeType", dispute.getDisputeType());
        map.put("reason", dispute.getReason());
        map.put("resolution", dispute.getResolution());
        map.put("status", dispute.getStatus());
        map.put("createdAt", dispute.getCreatedAt());
        return map;
    }
}