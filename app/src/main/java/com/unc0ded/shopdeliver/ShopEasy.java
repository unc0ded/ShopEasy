package com.unc0ded.shopdeliver;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.unc0ded.shopdeliver.utils.SessionManager;

public class ShopEasy extends Application {

    private SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);

        sessionManager = new SessionManager(getApplicationContext());
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
