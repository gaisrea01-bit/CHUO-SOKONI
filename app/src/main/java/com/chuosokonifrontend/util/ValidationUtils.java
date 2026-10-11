package com.chuosokonifrontend.util;

import android.text.TextUtils;

public class ValidationUtils {

    public static boolean isValidTanzanianPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        // Tanzanian phone format: 07xxxxxxx, 06xxxxxxx, or +2557xxxxxxx, +2556xxxxxxx
        String cleaned = phone.replaceAll("\\s+", "");
        return cleaned.matches("^(\\+255|0)[67]\\d{8}$");
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    public static boolean isNotEmpty(String text) {
        return !TextUtils.isEmpty(text) && !text.trim().isEmpty();
    }
}
