package com.chuosokonifrontend;

import android.app.Application;

public class chuosokoniApp extends Application {
    private static chuosokoniApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static chuosokoniApp getInstance() {
        return instance;
    }
}
