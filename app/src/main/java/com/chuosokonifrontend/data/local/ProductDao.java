package com.chuosokonifrontend.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM cached_products")
    List<CachedProduct> getAllCachedProducts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProducts(List<CachedProduct> products);

    @Query("DELETE FROM cached_products")
    void clearCachedProducts();
}
