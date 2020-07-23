package com.unc0ded.shopdeliver;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class ShopEasy extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
