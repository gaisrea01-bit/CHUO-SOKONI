package com.chuosokonifrontend.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_products")
public class CachedProduct {
    @PrimaryKey
    private Long productId;
    private String name;
    private String description;
    private String unitPrice;
    private Integer stockQuantity;
    private String status;

    public CachedProduct(Long productId, String name, String description, String unitPrice, Integer stockQuantity, String status) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.status = status;
    }

    public Long getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUnitPrice() { return unitPrice; }
    public Integer getStockQuantity() { return stockQuantity; }
    public String getStatus() { return status; }
}
