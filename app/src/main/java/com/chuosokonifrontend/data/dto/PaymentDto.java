package com.chuosokonifrontend.data.dto;

import java.math.BigDecimal;

public class PaymentDto {
    private Long paymentId;
    private Long userId;
    private Long orderId;
    private String paymentProvider;
    private String referenceNumber;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String status;

    public PaymentDto() {}

    public PaymentDto(Long paymentId, Long userId, Long orderId, String paymentProvider, String referenceNumber, BigDecimal amount, String currency, String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.orderId = orderId;
        this.paymentProvider = paymentProvider;
        this.referenceNumber = referenceNumber;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getPaymentProvider() { return paymentProvider; }
    public void setPaymentProvider(String paymentProvider) { this.paymentProvider = paymentProvider; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
