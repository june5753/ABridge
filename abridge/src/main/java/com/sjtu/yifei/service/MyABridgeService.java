package com.sjtu.yifei.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sjtu.yifei.aidl.IReceiverAidlInterface;
import com.sjtu.yifei.aidl.ISenderAidlInterface;

/**
 * <pre>
 *  author : juneYang
 *  time   : 2021/03/12 11:00 AM
 *  desc   : Binder实现 (在服务端返回其Binder以及所需实现的接口)
 *  version: 1.0
 * </pre>
 */
public class MyABridgeService extends Service {
    private static final String TAG = "MyABridgeService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return mBinder;
    }

    //实现AIDL中接口的方法
    private final ISenderAidlInterface.Stub mBinder = new ISenderAidlInterface.Stub() {
        @Override
        public void join(IBinder token) throws RemoteException {

        }

        @Override
        public void leave(IBinder token) throws RemoteException {

        }

        @Override
        public void sendMessage(String json) throws RemoteException {

        }

        @Override
        public void registerCallback(IReceiverAidlInterface cb) throws RemoteException {

        }

        @Override
        public void unregisterCallback(IReceiverAidlInterface cb) throws RemoteException {

        }
    };
}
