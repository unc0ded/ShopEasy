package com.unc0ded.shopeasy;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.unc0ded.shopeasy.utils.SessionManager;

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
