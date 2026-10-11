package tz.ac.mzumbe.chuosokoni.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * Initiates a payment transaction for an order (FR-PAY-001, FR-PAY-002).
     * Expected JSON body: { "orderId", "buyerUserId", "paymentProvider" (optional), "paymentMethod" (optional) }
     */
    @PostMapping("/initiate")
    public ResponseEntity<Map<String, Object>> initiatePayment(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("orderId") == null || request.get("buyerUserId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "orderId and buyerUserId are required."));
            }

            Integer orderId = Integer.parseInt(request.get("orderId").toString());
            Integer buyerUserId = Integer.parseInt(request.get("buyerUserId").toString());
            String paymentProvider = (String) request.get("paymentProvider");
            String paymentMethod = (String) request.get("paymentMethod");

            Map<String, Object> response = paymentService.initiatePayment(
                    orderId, buyerUserId, paymentProvider, paymentMethod
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to initiate payment: " + e.getMessage()));
        }
    }

    /**
     * External Payment Gateway Webhook/Callback Endpoint (FR-PAY-003 to FR-PAY-005).
     * Verifies payment before updating order fulfillment status and processing fee splits.
     * Expected JSON body: { "referenceNumber", "providerResponse", "callbackStatus" }
     */
    @PostMapping("/callback")
    public ResponseEntity<Map<String, Object>> handlePaymentCallback(@RequestBody Map<String, String> request) {
        try {
            String referenceNumber = request.get("referenceNumber");
            String providerResponse = request.get("providerResponse");
            String callbackStatus = request.get("callbackStatus");

            if (referenceNumber == null || callbackStatus == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "referenceNumber and callbackStatus are required."));
            }

            Map<String, Object> response = paymentService.handlePaymentCallback(
                    referenceNumber, providerResponse, callbackStatus
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process payment callback: " + e.getMessage()));
        }
    }

    /**
     * Fetches payment attempts/history for a specific order (FR-PAY-003).
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Map<String, Object>>> getPaymentsByOrder(@PathVariable Integer orderId) {
        try {
            List<Map<String, Object>> payments = paymentService.getPaymentsByOrder(orderId);
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}