package tz.ac.mzumbe.chuosokoni.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tz.ac.mzumbe.chuosokoni.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Creates a new customer order for selected products (FR-CUS-003).
     * Expected JSON body: { "buyerUserId", "addressId", "items": [{"productId", "quantity", "unitPrice"}], "transactionModel" (optional) }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        try {
            if (request.get("buyerUserId") == null || request.get("addressId") == null || request.get("items") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "buyerUserId, addressId, and items list are required."));
            }

            Integer buyerUserId = Integer.parseInt(request.get("buyerUserId").toString());
            Integer addressId = Integer.parseInt(request.get("addressId").toString());
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
            
            String transactionModel = (String) request.get("transactionModel");

            Map<String, Object> response = orderService.createOrder(buyerUserId, addressId, items, transactionModel);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create order: " + e.getMessage()));
        }
    }

    /**
     * Retrieves customer order history (FR-CUS-004).
     */
    @GetMapping("/user/{buyerUserId}")
    public ResponseEntity<List<Map<String, Object>>> getCustomerOrders(@PathVariable Integer buyerUserId) {
        try {
            List<Map<String, Object>> orders = orderService.getCustomerOrders(buyerUserId);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Customer confirms receipt/delivery of order items (FR-CUS-006).
     * Expected JSON body: { "buyerUserId" }
     */
    @PutMapping("/{orderId}/confirm-receipt")
    public ResponseEntity<Map<String, Object>> confirmReceipt(
            @PathVariable Integer orderId,
            @RequestBody Map<String, Object> request
    ) {
        try {
            if (request.get("buyerUserId") == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "buyerUserId is required to confirm receipt."));
            }

            Integer buyerUserId = Integer.parseInt(request.get("buyerUserId").toString());
            Map<String, Object> response = orderService.confirmReceipt(orderId, buyerUserId);

            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to confirm receipt: " + e.getMessage()));
        }
    }

    /**
     * Internal/Fulfillment Operation: Updates order fulfillment and payment status (FR-PAY-004).
     * Expected JSON body: { "orderStatus", "paymentStatus" }
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestBody Map<String, String> request
    ) {
        try {
            String orderStatus = request.get("orderStatus");
            String paymentStatus = request.get("paymentStatus");

            Map<String, Object> response = orderService.updateOrderStatus(orderId, orderStatus, paymentStatus);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update order status: " + e.getMessage()));
        }
    }
}