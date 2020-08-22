package com.unc0ded.shopdeliver.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences tokenPrefs;
    private final static String AUTH_TOKEN = "JWT";
    private final static String USER_ID = "userId";

    public SessionManager(Context context) {
        tokenPrefs = context.getSharedPreferences("Credentials", Context.MODE_PRIVATE);
    }

    public void saveAuthToken(String token) {
        tokenPrefs.edit().putString(AUTH_TOKEN, token).apply();
    }

    public String fetchAuthToken() {
        return tokenPrefs.getString(AUTH_TOKEN, null);
    }

    public void saveUserId(String userId) {
        tokenPrefs.edit().putString(USER_ID, userId).apply();
    }

    public String fetchUserId() {
        return tokenPrefs.getString(USER_ID, null);
    }
}
