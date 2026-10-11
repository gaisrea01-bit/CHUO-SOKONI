package com.chuosokonifrontend.data.dto;

import java.math.BigDecimal;

public class ProductDto {
    private Long productId;
    private Long sellerId;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private Integer stockQuantity;
    private String status;

    public ProductDto() {}

    public ProductDto(Long productId, Long sellerId, Long categoryId, String name, String description, BigDecimal unitPrice, Integer stockQuantity, String status) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.status = status;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
