package com.chuosokonifrontend.data.network;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREF_NAME = "ChuoSokoniPrefs";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    
    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void saveUserId(Long userId) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public Long getUserId() {
        long id = prefs.getLong(KEY_USER_ID, -1L);
        return id != -1L ? id : null;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
