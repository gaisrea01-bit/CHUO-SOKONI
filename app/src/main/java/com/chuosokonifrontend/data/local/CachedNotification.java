package com.chuosokonifrontend.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_notifications")
public class CachedNotification {

    @PrimaryKey
    private Long notificationId;
    private String type;
    private String message;
    private Boolean isRead;
    private String createdAt;

    public CachedNotification() {}

    public CachedNotification(Long notificationId, String type, String message, Boolean isRead, String createdAt) {
        this.notificationId = notificationId;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
