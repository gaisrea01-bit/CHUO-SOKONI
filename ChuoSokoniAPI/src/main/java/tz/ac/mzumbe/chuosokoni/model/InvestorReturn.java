package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "returns")
public class InvestorReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "return_id")
    private Integer returnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

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

    public InvestorReturn() {
    }

    public InvestorReturn(Transaction transaction,
                  Order order,
                  String reason,
                  BigDecimal amount,
                  String status,
                  LocalDateTime createdAt) {
        this.transaction = transaction;
        this.order = order;
        this.reason = reason;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
        return "Return{" +
                "returnId=" + returnId +
                ", reason='" + reason + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvestorReturn)) return false;
        InvestorReturn aReturn = (InvestorReturn) o;
        return Objects.equals(returnId, aReturn.returnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnId);
    }
}
