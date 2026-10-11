package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "stock_movements")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movement_id")
    private Integer movementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "reference_id")
    private Integer referenceId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public StockMovement() {
    }

    public StockMovement(Product product,
                         String type,
                         Integer quantity,
                         Integer referenceId,
                         LocalDateTime createdAt) {
        this.product = product;
        this.type = type;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    public Integer getMovementId() {
        return movementId;
    }

    public void setMovementId(Integer movementId) {
        this.movementId = movementId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Integer referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "movementId=" + movementId +
                ", type='" + type + '\'' +
                ", quantity=" + quantity +
                ", referenceId=" + referenceId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockMovement)) return false;
        StockMovement that = (StockMovement) o;
        return Objects.equals(movementId, that.movementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movementId);
    }
}
