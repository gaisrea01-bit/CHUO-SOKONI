package com.chuosokonifrontend.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CachedProduct.class}, version = 1, exportSchema = false)
public abstract class ChuoSokoniDatabase extends RoomDatabase {

    private static volatile ChuoSokoniDatabase INSTANCE;

    public abstract ProductDao productDao();

    public static ChuoSokoniDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ChuoSokoniDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ChuoSokoniDatabase.class, "chuosokoni_local_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
