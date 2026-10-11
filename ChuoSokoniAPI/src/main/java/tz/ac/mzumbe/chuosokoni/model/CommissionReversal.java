package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "commission_reversals")
public class CommissionReversal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reversal_id")
    private Integer reversalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_id", nullable = false)
    private CommissionRecord commission;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt= LocalDateTime.now();

    public CommissionReversal() {
    }

    public CommissionReversal(CommissionRecord commission,
                              String reason,
                              BigDecimal amount,
                              String status,
                              LocalDateTime createdAt) {
        this.commission = commission;
        this.reason = reason;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getReversalId() {
        return reversalId;
    }

    public void setReversalId(Integer reversalId) {
        this.reversalId = reversalId;
    }

    public CommissionRecord getCommission() {
        return commission;
    }

    public void setCommission(CommissionRecord commission) {
        this.commission = commission;
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
        return "CommissionReversal{" +
                "reversalId=" + reversalId +
                ", reason='" + reason + '\'' +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommissionReversal)) return false;
        CommissionReversal that = (CommissionReversal) o;
        return Objects.equals(reversalId, that.reversalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reversalId);
    }
}
