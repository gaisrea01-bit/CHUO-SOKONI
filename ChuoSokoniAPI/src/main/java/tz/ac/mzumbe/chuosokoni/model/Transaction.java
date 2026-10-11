package tz.ac.mzumbe.chuosokoni.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Agent agent;

    @Column(name = "transaction_model", length = 30)
    private String transactionModel;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "platform_fee", precision = 15, scale = 2)
    private BigDecimal platformFee;

    @Column(name = "agent_commission", precision = 15, scale = 2)
    private BigDecimal agentCommission;

    @Column(name = "investor_return", precision = 15, scale = 2)
    private BigDecimal investorReturn;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Transaction() {
    }

    public Transaction(Listing listing,
                       User buyer,
                       User seller,
                       Agent agent,
                       String transactionModel,
                       BigDecimal totalAmount,
                       BigDecimal platformFee,
                       BigDecimal agentCommission,
                       BigDecimal investorReturn,
                       String status,
                       LocalDateTime createdAt) {
        this.listing = listing;
        this.buyer = buyer;
        this.seller = seller;
        this.agent = agent;
        this.transactionModel = transactionModel;
        this.totalAmount = totalAmount;
        this.platformFee = platformFee;
        this.agentCommission = agentCommission;
        this.investorReturn = investorReturn;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public String getTransactionModel() {
        return transactionModel;
    }

    public void setTransactionModel(String transactionModel) {
        this.transactionModel = transactionModel;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getAgentCommission() {
        return agentCommission;
    }

    public void setAgentCommission(BigDecimal agentCommission) {
        this.agentCommission = agentCommission;
    }

    public BigDecimal getInvestorReturn() {
        return investorReturn;
    }

    public void setInvestorReturn(BigDecimal investorReturn) {
        this.investorReturn = investorReturn;
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
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", transactionModel='" + transactionModel + '\'' +
                ", totalAmount=" + totalAmount +
                ", platformFee=" + platformFee +
                ", agentCommission=" + agentCommission +
                ", investorReturn=" + investorReturn +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }
}
