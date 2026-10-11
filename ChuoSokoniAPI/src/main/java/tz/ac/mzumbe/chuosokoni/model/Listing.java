package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    private Integer listingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "transaction_model", length = 30)
    private String transactionModel;

    @Column(name = "listing_type", length = 30)
    private String listingType;

    @Column(name = "approved_retail_price", precision = 12, scale = 2)
    private BigDecimal approvedRetailPrice;

    @Column(name = "wholesale_price", precision = 12, scale = 2)
    private BigDecimal wholesalePrice;

    @Column(name = "negotiation_enabled")
    private Boolean negotiationEnabled;

    @Column(name = "registration_enabled")
    private Boolean registrationEnabled;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Listing() {
    }

    public Listing(Product product,
                   Seller seller,
                   Category category,
                   String name,
                   String description,
                   BigDecimal price,
                   Integer stockQuantity,
                   String status,
                   String transactionModel,
                   String listingType,
                   BigDecimal approvedRetailPrice,
                   BigDecimal wholesalePrice,
                   Boolean negotiationEnabled,
                   Boolean registrationEnabled,
                   LocalDateTime createdAt) {

        this.product = product;
        this.seller = seller;
        this.category = category;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.transactionModel = transactionModel;
        this.listingType = listingType;
        this.approvedRetailPrice = approvedRetailPrice;
        this.wholesalePrice = wholesalePrice;
        this.negotiationEnabled = negotiationEnabled;
        this.registrationEnabled = registrationEnabled;
        this.createdAt = createdAt;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public String getTransactionModel() {
        return transactionModel;
    }

    public void setTransactionModel(String transactionModel) {
        this.transactionModel = transactionModel;
    }

    public String getListingType() {
        return listingType;
    }

    public void setListingType(String listingType) {
        this.listingType = listingType;
    }

    public BigDecimal getApprovedRetailPrice() {
        return approvedRetailPrice;
    }

    public void setApprovedRetailPrice(BigDecimal approvedRetailPrice) {
        this.approvedRetailPrice = approvedRetailPrice;
    }

    public BigDecimal getWholesalePrice() {
        return wholesalePrice;
    }

    public void setWholesalePrice(BigDecimal wholesalePrice) {
        this.wholesalePrice = wholesalePrice;
    }

    public Boolean getNegotiationEnabled() {
        return negotiationEnabled;
    }

    public void setNegotiationEnabled(Boolean negotiationEnabled) {
        this.negotiationEnabled = negotiationEnabled;
    }

    public Boolean getRegistrationEnabled() {
        return registrationEnabled;
    }

    public void setRegistrationEnabled(Boolean registrationEnabled) {
        this.registrationEnabled = registrationEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Listing{" +
                "listingId=" + listingId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Listing)) return false;
        Listing listing = (Listing) o;
        return Objects.equals(listingId, listing.listingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listingId);
    }
}
