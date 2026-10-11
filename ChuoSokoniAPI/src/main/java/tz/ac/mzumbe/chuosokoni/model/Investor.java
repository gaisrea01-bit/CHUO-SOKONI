package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "investors")
public class Investor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "investor_id")
    private Integer investorId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "investment_type", length = 50)
    private String investmentType;

    @Column(name = "available_capital", precision = 15, scale = 2)
    private BigDecimal availableCapital;

    @Column(name = "risk_preference", length = 50)
    private String riskPreference;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Investor() {
    }

    public Investor(User user,
                    String investmentType,
                    BigDecimal availableCapital,
                    String riskPreference,
                    String status,
                    LocalDateTime createdAt) {
        this.user = user;
        this.investmentType = investmentType;
        this.availableCapital = availableCapital;
        this.riskPreference = riskPreference;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Integer investorId) {
        this.investorId = investorId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(String investmentType) {
        this.investmentType = investmentType;
    }

    public BigDecimal getAvailableCapital() {
        return availableCapital;
    }

    public void setAvailableCapital(BigDecimal availableCapital) {
        this.availableCapital = availableCapital;
    }

    public String getRiskPreference() {
        return riskPreference;
    }

    public void setRiskPreference(String riskPreference) {
        this.riskPreference = riskPreference;
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
        return "Investor{" +
                "investorId=" + investorId +
                ", investmentType='" + investmentType + '\'' +
                ", availableCapital=" + availableCapital +
                ", riskPreference='" + riskPreference + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Investor)) return false;
        Investor investor = (Investor) o;
        return Objects.equals(investorId, investor.investorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(investorId);
    }
}
