package tz.ac.mzumbe.chuosokoni.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.model.Payment;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.OrderRepository;
import tz.ac.mzumbe.chuosokoni.repository.PaymentRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DirectRetailService directRetailService;

    @Autowired
    private WholesaleService wholesaleService;

    /**
     * Initiates a payment transaction for an order (FR-PAY-001, FR-PAY-002)[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> initiatePayment(
            Integer orderId,
            Integer buyerUserId,
            String paymentProvider,
            String paymentMethod
    ) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        User buyer = userRepository.findById(buyerUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + buyerUserId));

        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
            throw new IllegalStateException("Order has already been paid.");
        }

        // Generate unique transaction reference (e.g., TXN-CS-8F3A2B1C)[cite: 2, 3]
        String referenceNumber = "TXN-CS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setBuyer(buyer);
        payment.setPaymentProvider(paymentProvider != null ? paymentProvider : "MOBILE_MONEY");
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "M-PESA");
        payment.setReferenceNumber(referenceNumber);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency("TZS");
        payment.setStatus("PENDING"); // FR-PAY-006: Initial pending state[cite: 3]

        Payment savedPayment = paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", savedPayment.getPaymentId());
        response.put("orderId", order.getOrderId());
        response.put("referenceNumber", savedPayment.getReferenceNumber());
        response.put("amount", savedPayment.getAmount());
        response.put("currency", savedPayment.getCurrency());
        response.put("provider", savedPayment.getPaymentProvider());
        response.put("status", savedPayment.getStatus());
        response.put("initiatedAt", savedPayment.getCreatedAt());

        return response;
    }

    /**
     * Handles external payment gateway callback verification (FR-PAY-003 to FR-PAY-005)[cite: 2, 3].
     * Verifies payment before releasing order fulfillment and distributing financial shares[cite: 3].
     */
    @Transactional
    public Map<String, Object> handlePaymentCallback(
            String referenceNumber,
            String providerResponse,
            String callbackStatus
    ) {
        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new IllegalArgumentException("Payment record not found for reference: " + referenceNumber));

        String sanitizedStatus = callbackStatus.toUpperCase().trim();
        payment.setProviderResponse(providerResponse);
        payment.setStatus(sanitizedStatus);

        Order order = payment.getOrder();

        if ("SUCCESS".equalsIgnoreCase(sanitizedStatus) || "COMPLETED".equalsIgnoreCase(sanitizedStatus)) {
            payment.setPaidAt(LocalDateTime.now());
            order.setPaymentStatus("PAID");
            order.setOrderStatus("PROCESSING");
            
            // Route revenue allocations based on transaction model[cite: 2, 3]
            if ("DIRECT_RETAIL".equalsIgnoreCase(order.getTransactionModel())) {
                directRetailService.calculateDirectRetailFees(order.getTotalAmount());
            }
        } else {
            payment.setStatus("FAILED");
            order.setPaymentStatus("FAILED");
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", payment.getPaymentId());
        response.put("orderId", order.getOrderId());
        response.put("referenceNumber", payment.getReferenceNumber());
        response.put("paymentStatus", payment.getStatus());
        response.put("orderPaymentStatus", order.getPaymentStatus());
        response.put("paidAt", payment.getPaidAt());

        return response;
    }

    /**
     * Fetches payment history for a specific order (FR-PAY-003)[cite: 2, 3].
     */
    public List<Map<String, Object>> getPaymentsByOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        List<Payment> payments = paymentRepository.findByOrder(order);
        return payments.stream().map(p -> {
            Map<String, Object> map = new HashMap<>();
            map.put("paymentId", p.getPaymentId());
            map.put("referenceNumber", p.getReferenceNumber());
            map.put("amount", p.getAmount());
            map.put("currency", p.getCurrency());
            map.put("paymentProvider", p.getPaymentProvider());
            map.put("paymentMethod", p.getPaymentMethod());
            map.put("status", p.getStatus());
            map.put("paidAt", p.getPaidAt());
            return map;
        }).toList();
    }
}