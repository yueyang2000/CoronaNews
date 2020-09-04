package com.java.coronanews;

import android.app.Application;
import android.util.Log;

import com.java.coronanews.data.ImageLoader;
import com.java.coronanews.data.Manager;

/**
 * Created by equation on 9/11/17.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoader.init(this);

        // 创建数据管理
        Manager.CreateI(this);
    }
}
