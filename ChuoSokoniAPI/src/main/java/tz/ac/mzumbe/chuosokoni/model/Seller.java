package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_id")
    private Integer sellerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "business_name", length = 100)
    private String businessName;

    @Column(name = "business_type", length = 50)
    private String businessType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "verification_status", length = 30)
    private String verificationStatus;

    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Seller() {
    }

    public Seller(User user,
                  String businessName,
                  String businessType,
                  String description,
                  String verificationStatus,
                  BigDecimal rating,
                  LocalDateTime createdAt) {
        this.user = user;
        this.businessName = businessName;
        this.businessType = businessType;
        this.description = description;
        this.verificationStatus = verificationStatus;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Seller{" +
                "sellerId=" + sellerId +
                ", businessName='" + businessName + '\'' +
                ", businessType='" + businessType + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", rating=" + rating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seller)) return false;
        Seller seller = (Seller) o;
        return Objects.equals(sellerId, seller.sellerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellerId);
    }
}
