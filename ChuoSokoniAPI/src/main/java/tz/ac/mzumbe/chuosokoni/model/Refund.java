package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_id")
    private Integer refundId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Refund() {
    }

    public Refund(Payment payment,
                  Order order,
                  String reason,
                  BigDecimal amount,
                  String status,
                  LocalDateTime createdAt) {
        this.payment = payment;
        this.order = order;
        this.reason = reason;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getRefundId() {
        return refundId;
    }

    public void setRefundId(Integer refundId) {
        this.refundId = refundId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Refund{" +
                "refundId=" + refundId +
                ", reason='" + reason + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Refund)) return false;
        Refund refund = (Refund) o;
        return Objects.equals(refundId, refund.refundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refundId);
    }
}
