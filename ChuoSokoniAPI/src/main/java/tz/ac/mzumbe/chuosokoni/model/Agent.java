package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_id")
    private Integer agentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "agent_level", length = 20)
    private String agentLevel;

    @Column(name = "total_referrals")
    private Integer totalReferrals;

    @Column(name = "total_commissions", precision = 15, scale = 2)
    private BigDecimal totalCommissions;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Agent() {
    }

    public Agent(User user,
                 String agentLevel,
                 Integer totalReferrals,
                 BigDecimal totalCommissions,
                 String status,
                 LocalDateTime createdAt) {
        this.user = user;
        this.agentLevel = agentLevel;
        this.totalReferrals = totalReferrals;
        this.totalCommissions = totalCommissions;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getAgentId() {
        return agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAgentLevel() {
        return agentLevel;
    }

    public void setAgentLevel(String agentLevel) {
        this.agentLevel = agentLevel;
    }

    public Integer getTotalReferrals() {
        return totalReferrals;
    }

    public void setTotalReferrals(Integer totalReferrals) {
        this.totalReferrals = totalReferrals;
    }

    public BigDecimal getTotalCommissions() {
        return totalCommissions;
    }

    public void setTotalCommissions(BigDecimal totalCommissions) {
        this.totalCommissions = totalCommissions;
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
        return "Agent{" +
                "agentId=" + agentId +
                ", agentLevel='" + agentLevel + '\'' +
                ", totalReferrals=" + totalReferrals +
                ", totalCommissions=" + totalCommissions +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Agent)) return false;
        Agent agent = (Agent) o;
        return Objects.equals(agentId, agent.agentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentId);
    }

}
