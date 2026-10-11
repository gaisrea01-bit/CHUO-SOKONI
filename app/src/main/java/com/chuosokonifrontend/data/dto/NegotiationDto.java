package com.chuosokonifrontend.data.dto;

import java.math.BigDecimal;

public class NegotiationDto {
    private Long negotiationId;
    private Long listingId;
    private Long buyerId;
    private Long sellerId;
    private BigDecimal offeredPrice;
    private String status;

    public NegotiationDto() {}

    public NegotiationDto(Long negotiationId, Long listingId, Long buyerId, Long sellerId, BigDecimal offeredPrice, String status) {
        this.negotiationId = negotiationId;
        this.listingId = listingId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.offeredPrice = offeredPrice;
        this.status = status;
    }

    public Long getNegotiationId() { return negotiationId; }
    public void setNegotiationId(Long negotiationId) { this.negotiationId = negotiationId; }

    public Long getListingId() { return listingId; }
    public void setListingId(Long listingId) { this.listingId = listingId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public BigDecimal getOfferedPrice() { return offeredPrice; }
    public void setOfferedPrice(BigDecimal offeredPrice) { this.offeredPrice = offeredPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

