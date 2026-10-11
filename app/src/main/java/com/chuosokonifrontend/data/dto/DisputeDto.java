package com.chuosokonifrontend.data.dto;

public class DisputeDto {
    private Long disputeId;
    private Long orderId;
    private Long initiatedBy;
    private String reason;
    private String resolution;
    private String status;

    public DisputeDto() {}

    public DisputeDto(Long disputeId, Long orderId, Long initiatedBy, String reason, String resolution, String status) {
        this.disputeId = disputeId;
        this.orderId = orderId;
        this.initiatedBy = initiatedBy;
        this.reason = reason;
        this.resolution = resolution;
        this.status = status;
    }

    public Long getDisputeId() { return disputeId; }
    public void setDisputeId(Long disputeId) { this.disputeId = disputeId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(Long initiatedBy) { this.initiatedBy = initiatedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
