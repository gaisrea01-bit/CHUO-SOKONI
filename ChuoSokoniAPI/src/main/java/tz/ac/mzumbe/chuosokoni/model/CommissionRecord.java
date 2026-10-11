package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "commission_records")
public class CommissionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commission_id")
    private Integer commissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "status", length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id")
    private Investor investor;

    @Column(name = "chuo_sokoni_amount", precision = 15, scale = 2)
    private BigDecimal chuoSokoniAmount;

    @Column(name = "agent_amount", precision = 15, scale = 2)
    private BigDecimal agentAmount;

    @Column(name = "investor_amount", precision = 15, scale = 2)
    private BigDecimal investorAmount;

    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public CommissionRecord() {
    }

    public CommissionRecord(Transaction transaction,
                            Agent agent,
                            Investor investor,
                            BigDecimal chuoSokoniAmount,
                            BigDecimal agentAmount,
                            BigDecimal investorAmount,
                            String type,
                            LocalDateTime createdAt) {
        this.transaction = transaction;
        this.agent = agent;
        this.investor = investor;
        this.chuoSokoniAmount = chuoSokoniAmount;
        this.agentAmount = agentAmount;
        this.investorAmount = investorAmount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public Integer getCommissionId() {
        return commissionId;
    }

    public void setCommissionId(Integer commissionId) {
        this.commissionId = commissionId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    public BigDecimal getChuoSokoniAmount() {
        return chuoSokoniAmount;
    }

    public void setChuoSokoniAmount(BigDecimal chuoSokoniAmount) {
        this.chuoSokoniAmount = chuoSokoniAmount;
    }

    public BigDecimal getAgentAmount() {
        return agentAmount;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAgentAmount(BigDecimal agentAmount) {
        this.agentAmount = agentAmount;
    }

    public BigDecimal getInvestorAmount() {
        return investorAmount;
    }

    public void setInvestorAmount(BigDecimal investorAmount) {
        this.investorAmount = investorAmount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CommissionRecord{"
               + "commissionId=" + commissionId
               + ", chuoSokoniAmount=" + chuoSokoniAmount
               + ", agentAmount=" + agentAmount
               + ", investorAmount=" + investorAmount
               + ", type='" + type + '\''
               + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommissionRecord)) {
            return false;
        }
        CommissionRecord that = (CommissionRecord) o;
        return Objects.equals(commissionId, that.commissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commissionId);
    }
}
