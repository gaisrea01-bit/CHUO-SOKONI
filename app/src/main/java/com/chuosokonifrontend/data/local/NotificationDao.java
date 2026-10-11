package com.chuosokonifrontend.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM cached_notifications")
    List<CachedNotification> getAllCachedNotifications();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotifications(List<CachedNotification> notifications);

    @Query("DELETE FROM cached_notifications")
    void clearCachedNotifications();
}
