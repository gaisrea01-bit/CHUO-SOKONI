package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "holding_fees")
public class HoldingFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Integer feeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(name = "fee_amount", precision = 12, scale = 2)
    private BigDecimal feeAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public HoldingFee() {
    }

    public HoldingFee(Listing listing,
                      BigDecimal feeAmount,
                      LocalDate dueDate,
                      String status,
                      LocalDateTime createdAt) {
        this.listing = listing;
        this.feeAmount = feeAmount;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getFeeId() {
        return feeId;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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
        return "HoldingFee{" +
                "feeId=" + feeId +
                ", feeAmount=" + feeAmount +
                ", dueDate=" + dueDate +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoldingFee)) return false;
        HoldingFee that = (HoldingFee) o;
        return Objects.equals(feeId, that.feeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feeId);
    }
}
