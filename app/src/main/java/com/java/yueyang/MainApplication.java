package com.java.yueyang;

import android.app.Application;

import com.java.yueyang.data.ImageLoader;
import com.java.yueyang.data.Manager;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.init(this);
        Manager.CreateI(this);
    }
}
