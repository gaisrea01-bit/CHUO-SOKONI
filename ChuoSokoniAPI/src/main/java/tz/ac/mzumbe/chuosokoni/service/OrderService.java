package tz.ac.mzumbe.chuosokoni.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tz.ac.mzumbe.chuosokoni.model.Address;
import tz.ac.mzumbe.chuosokoni.model.Order;
import tz.ac.mzumbe.chuosokoni.model.OrderItem;
import tz.ac.mzumbe.chuosokoni.model.Product;
import tz.ac.mzumbe.chuosokoni.model.User;
import tz.ac.mzumbe.chuosokoni.repository.AddressRepository;
import tz.ac.mzumbe.chuosokoni.repository.OrderItemRepository;
import tz.ac.mzumbe.chuosokoni.repository.OrderRepository;
import tz.ac.mzumbe.chuosokoni.repository.ProductRepository;
import tz.ac.mzumbe.chuosokoni.repository.UserRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    /**
     * Creates a new customer order for selected products (FR-CUS-003)[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> createOrder(
            Integer buyerUserId,
            Integer addressId,
            List<Map<String, Object>> items,
            String transactionModel
    ) {
        User buyer = userRepository.findById(buyerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer user not found with ID: " + buyerUserId));

        Address shippingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Shipping address not found with ID: " + addressId));

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order items list cannot be empty.");
        }

        // 1. Create and initialize Order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setShippingAddress(shippingAddress);
        order.setOrderStatus("PENDING_PAYMENT");
        order.setPaymentStatus("UNPAID");
        order.setTransactionModel(transactionModel != null ? transactionModel.toUpperCase() : "DIRECT_RETAIL");

        Order savedOrder = orderRepository.save(order);

        BigDecimal calculatedTotalAmount = BigDecimal.ZERO;

        // 2. Process Order Items and calculate total amount
        for (Map<String, Object> itemData : items) {
            Integer productId = (Integer) itemData.get("productId");
            Integer quantity = (Integer) itemData.get("quantity");
            BigDecimal unitPrice = new BigDecimal(itemData.get("unitPrice").toString());

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

            if (product.getStockQuantity() < quantity) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal subtotal = unitPrice.multiply(new BigDecimal(quantity));
            calculatedTotalAmount = calculatedTotalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setSeller(product.getSeller());
            orderItem.setQuantity(quantity);
            orderItem.setUnitPrice(unitPrice);
            orderItem.setSubtotal(subtotal);

            orderItemRepository.save(orderItem);
        }

        savedOrder.setTotalAmount(calculatedTotalAmount);
        Order updatedOrder = orderRepository.save(savedOrder);

        return buildOrderResponseMap(updatedOrder);
    }

    /**
     * Updates order fulfillment and payment status upon payment verification (FR-PAY-004)[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> updateOrderStatus(Integer orderId, String orderStatus, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        if (orderStatus != null && !orderStatus.trim().isEmpty()) {
            order.setOrderStatus(orderStatus.toUpperCase().trim());
        }

        if (paymentStatus != null && !paymentStatus.trim().isEmpty()) {
            order.setPaymentStatus(paymentStatus.toUpperCase().trim());
        }

        Order savedOrder = orderRepository.save(order);
        return buildOrderResponseMap(savedOrder);
    }

    /**
     * Customer confirms delivery/receipt of order items (FR-CUS-006)[cite: 2, 3].
     */
    @Transactional
    public Map<String, Object> confirmReceipt(Integer orderId, Integer buyerUserId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));

        if (!order.getBuyer().getUserId().equals(buyerUserId)) {
            throw new SecurityException("Unauthorized action. Customer does not own this order.");
        }

        order.setOrderStatus("COMPLETED");
        order.setUpdatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        return buildOrderResponseMap(savedOrder);
    }

    /**
     * Retrieves customer order history (FR-CUS-004)[cite: 2, 3].
     */
    public List<Map<String, Object>> getCustomerOrders(Integer buyerUserId) {
        User buyer = userRepository.findById(buyerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Customer user not found with ID: " + buyerUserId));

        List<Order> orders = orderRepository.findByBuyer(buyer);
        return orders.stream().map(this::buildOrderResponseMap).toList();
    }

    /**
     * Helper method to format Order details into response maps.
     */
    private Map<String, Object> buildOrderResponseMap(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", order.getOrderId());
        map.put("buyerUserId", order.getBuyer().getUserId());
        map.put("buyerName", order.getBuyer().getFullName());
        map.put("totalAmount", order.getTotalAmount());
        map.put("orderStatus", order.getOrderStatus());
        map.put("paymentStatus", order.getPaymentStatus());
        map.put("transactionModel", order.getTransactionModel());
        map.put("createdAt", order.getCreatedAt());

        List<OrderItem> items = orderItemRepository.findByOrder(order);
        List<Map<String, Object>> itemDetails = items.stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("orderItemId", item.getOrderItemId());
            itemMap.put("productId", item.getProduct().getProductId());
            itemMap.put("productName", item.getProduct().getName());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("unitPrice", item.getUnitPrice());
            itemMap.put("subtotal", item.getSubtotal());
            return itemMap;
        }).toList();

        map.put("items", itemDetails);
        return map;
    }
}