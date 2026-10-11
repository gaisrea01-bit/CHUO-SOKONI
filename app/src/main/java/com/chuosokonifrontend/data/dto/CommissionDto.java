package com.chuosokonifrontend.data.dto;

import java.math.BigDecimal;

public class CommissionDto {
    private Long commissionId;
    private Long transactionId;
    private Long agentId;
    private Long investorId;
    private BigDecimal chuosokoniAmount;
    private BigDecimal agentAmount;
    private BigDecimal investorAmount;
    private String type;

    public CommissionDto() {}

    public CommissionDto(Long commissionId, Long transactionId, Long agentId, Long investorId, BigDecimal chuosokoniAmount, BigDecimal agentAmount, BigDecimal investorAmount, String type) {
        this.commissionId = commissionId;
        this.transactionId = transactionId;
        this.agentId = agentId;
        this.investorId = investorId;
        this.chuosokoniAmount = chuosokoniAmount;
        this.agentAmount = agentAmount;
        this.investorAmount = investorAmount;
        this.type = type;
    }

    public Long getCommissionId() { return commissionId; }
    public void setCommissionId(Long commissionId) { this.commissionId = commissionId; }

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getAgentId() { return agentId; }
    public void setAgentId(Long agentId) { this.agentId = agentId; }

    public Long getInvestorId() { return investorId; }
    public void setInvestorId(Long investorId) { this.investorId = investorId; }

    public BigDecimal getChuosokoniAmount() { return chuosokoniAmount; }
    public void setChuosokoniAmount(BigDecimal chuosokoniAmount) { this.chuosokoniAmount = chuosokoniAmount; }

    public BigDecimal getAgentAmount() { return agentAmount; }
    public void setAgentAmount(BigDecimal agentAmount) { this.agentAmount = agentAmount; }

    public BigDecimal getInvestorAmount() { return investorAmount; }
    public void setInvestorAmount(BigDecimal investorAmount) { this.investorAmount = investorAmount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
