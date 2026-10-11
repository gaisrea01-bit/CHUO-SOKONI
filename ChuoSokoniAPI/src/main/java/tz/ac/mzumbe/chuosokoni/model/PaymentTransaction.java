package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_txn_id")
    private Integer paymentTxnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "provider_response", columnDefinition = "TEXT")
    private String providerResponse;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public PaymentTransaction() {
    }

    public PaymentTransaction(Payment payment,
                              String providerResponse,
                              String transactionId,
                              String status,
                              LocalDateTime createdAt) {
        this.payment = payment;
        this.providerResponse = providerResponse;
        this.transactionId = transactionId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getPaymentTxnId() {
        return paymentTxnId;
    }

    public void setPaymentTxnId(Integer paymentTxnId) {
        this.paymentTxnId = paymentTxnId;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
        return "PaymentTransaction{" +
                "paymentTxnId=" + paymentTxnId +
                ", transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentTransaction)) return false;
        PaymentTransaction that = (PaymentTransaction) o;
        return Objects.equals(paymentTxnId, that.paymentTxnId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentTxnId);
    }
}
