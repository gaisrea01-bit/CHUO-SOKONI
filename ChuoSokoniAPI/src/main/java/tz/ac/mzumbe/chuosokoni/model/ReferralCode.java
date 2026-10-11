package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "referral_codes")
public class ReferralCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referral_code_id")
    private Integer referralCodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @Column(name = "is_default")
    private Boolean isDefault;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ReferralCode() {
    }

    public ReferralCode(Agent agent,
                        String code,
                        Boolean isDefault,
                        LocalDateTime createdAt) {
        this.agent = agent;
        this.code = code;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }

    public Integer getReferralCodeId() {
        return referralCodeId;
    }

    public void setReferralCodeId(Integer referralCodeId) {
        this.referralCodeId = referralCodeId;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ReferralCode{" +
                "referralCodeId=" + referralCodeId +
                ", code='" + code + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferralCode)) return false;
        ReferralCode that = (ReferralCode) o;
        return Objects.equals(referralCodeId, that.referralCodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referralCodeId);
    }
}
