package com.unc0ded.shopdeliver.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private SharedPreferences tokenPrefs;
    private final static String AUTH_TOKEN = "JWT";

    public SessionManager(Context context) {
        tokenPrefs = context.getSharedPreferences("Auth Credential", Context.MODE_PRIVATE);
    }

    public void saveAuthToken(String token) {
        tokenPrefs.edit().putString(AUTH_TOKEN, token).apply();
    }

    public String fetchAuthToken() {
        return tokenPrefs.getString(AUTH_TOKEN, null);
    }
}
