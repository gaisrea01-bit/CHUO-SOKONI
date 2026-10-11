package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "disputes")
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispute_id")
    private Integer disputeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by", nullable = false)
    private User initiatedBy;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "resolution", columnDefinition = "TEXT")
    private String resolution;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "type", length = 50)
    private String disputeType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Dispute() {
    }

    public Dispute(Order order,
                   User initiatedBy,
                   String reason,
                   String resolution,
                   String status,
                   LocalDateTime createdAt) {
        this.order = order;
        this.initiatedBy = initiatedBy;
        this.reason = reason;
        this.resolution = resolution;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getDisputeId() {
        return disputeId;
    }

    public void setDisputeId(Integer disputeId) {
        this.disputeId = disputeId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(User initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDisputeType() {
        return disputeType;
    }

    public void setDisputeType(String disputeType) {
        this.disputeType = disputeType;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
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
        return "Dispute{"
               + "disputeId=" + disputeId
               + ", reason='" + reason + '\''
               + ", resolution='" + resolution + '\''
               + ", status='" + status + '\''
               + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dispute)) {
            return false;
        }
        Dispute dispute = (Dispute) o;
        return Objects.equals(disputeId, dispute.disputeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(disputeId);
    }
}
