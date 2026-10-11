package com.chuosokonifrontend.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM cached_users LIMIT 1")
    CachedUser getCachedUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(CachedUser user);

    @Query("DELETE FROM cached_users")
    void clearCachedUser();
}
