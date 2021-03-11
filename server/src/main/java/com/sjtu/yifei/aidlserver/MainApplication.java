package com.sjtu.yifei.aidlserver;

import android.app.Application;

import com.sjtu.yifei.IBridge;

/**
 * [description] author: yifei created at 18/6/3 下午8:40
 */

public class MainApplication extends Application {

    private final String mPackageName = "com.sjtu.yifei.aidlserver";

    @Override
    public void onCreate() {
        super.onCreate();
        IBridge.init(this, mPackageName, IBridge.AbridgeType.AIDL);
    }

    @Override
    public void onTerminate() {
        IBridge.recycle();
        super.onTerminate();
    }
}
