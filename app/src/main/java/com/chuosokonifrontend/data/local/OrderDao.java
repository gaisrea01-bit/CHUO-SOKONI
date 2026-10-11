package com.chuosokonifrontend.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface OrderDao {
    @Query("SELECT * FROM cached_orders")
    List<CachedOrder> getAllCachedOrders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrders(List<CachedOrder> orders);

    @Query("DELETE FROM cached_orders")
    void clearCachedOrders();
}
