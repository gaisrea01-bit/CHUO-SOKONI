package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "withdrawals")
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdrawal_id")
    private Integer withdrawalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "method", length = 50)
    private String method;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Withdrawal() {
    }

    public Withdrawal(User user,
                      BigDecimal amount,
                      String method,
                      String status,
                      String referenceNumber,
                      LocalDateTime createdAt) {
        this.user = user;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.referenceNumber = referenceNumber;
        this.createdAt = createdAt;
    }

    public Integer getWithdrawalId() {
        return withdrawalId;
    }

    public void setWithdrawalId(Integer withdrawalId) {
        this.withdrawalId = withdrawalId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Withdrawal{" +
                "withdrawalId=" + withdrawalId +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", status='" + status + '\'' +
                ", referenceNumber='" + referenceNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Withdrawal)) return false;
        Withdrawal that = (Withdrawal) o;
        return Objects.equals(withdrawalId, that.withdrawalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(withdrawalId);
    }
}
