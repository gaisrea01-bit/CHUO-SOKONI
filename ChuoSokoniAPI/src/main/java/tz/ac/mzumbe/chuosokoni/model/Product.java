package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id")
    private Investor investor;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // --- TARGET RETAIL PRICE FIELD ---
    @Column(name = "target_retail_price", precision = 15, scale = 2)
    private BigDecimal targetRetailPrice;

    @Column(name = "final_retail_price", precision = 15, scale = 2)
    private BigDecimal finalRetailPrice;
    
    @Column(name = "wholesale_price", precision = 12, scale = 2)
    private BigDecimal wholesalePrice;

    @Column(name = "unit", length = 20)
    private String unit = "item";
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Product() {
    }

    public Product(Seller seller,
                   Category category,
                   Investor investor,
                   String name,
                   String description,
                   BigDecimal wholesalePrice,
                   Integer stockQuantity,
                   String status,
                   LocalDateTime createdAt) {
        this.seller = seller;
        this.category = category;
        this.investor = investor;
        this.name = name;
        this.description = description;
        this.wholesalePrice = wholesalePrice;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTargetRetailPrice() {
        return targetRetailPrice;
    }

    public void setTargetRetailPrice(BigDecimal targetRetailPrice) {
        this.targetRetailPrice = targetRetailPrice;
    }

    public BigDecimal getFinalRetailPrice() {
        return finalRetailPrice;
    }

    public void setFinalRetailPrice(BigDecimal finalRetailPrice) {
        this.finalRetailPrice = finalRetailPrice;
    }
    
    public BigDecimal getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(BigDecimal wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
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

    // --- GETTERS AND SETTERS FOR UNIT ---
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // --- GETTERS AND SETTERS FOR UPDATED_AT ---
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", wholesalePrice=" + wholesalePrice +
                ", stockQuantity=" + stockQuantity +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
    
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
