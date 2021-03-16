package com.sjtu.yifei.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.sjtu.yifei.aidl.IReceiverAidlInterface;
import com.sjtu.yifei.aidl.ISenderAidlInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端 同时把服务端配置到另外的进程 -> android:process=”:aidl”
 */
public class ABridgeService extends Service {

    private static final String TAG = "ABridgeService";
    private List<Client> mClients = new ArrayList<>();
    private RemoteCallbackList<IReceiverAidlInterface> mCallbacks = new RemoteCallbackList<>();

    public ABridgeService() {
        Log.e(TAG, "launched");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForeground(110, new Notification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        mCallbacks.kill();
    }

    private final ISenderAidlInterface.Stub mBinder = new ISenderAidlInterface.Stub() {
        @Override
        public void join(IBinder token) throws RemoteException {
            int idx = findClient(token);
            if (idx >= 0) {
                Log.d(TAG, token + " already joined , client size " + mClients.size());
                return;
            }
            Client client = new Client(token);
            // 注册客户端死掉的通知
            token.linkToDeath(client, 0);
            mClients.add(client);
            Log.d(TAG,
                    token + " join , client size " + mClients.size() + ",clientId:" + token.getInterfaceDescriptor());
        }

        @Override
        public void leave(IBinder token) throws RemoteException {
            int idx = findClient(token);
            if (idx < 0) {
                Log.d(TAG, token + " already left , client size " + mClients.size());
                return;
            }
            Client client = mClients.get(idx);
            mClients.remove(client);
            // 取消注册
            client.mToken.unlinkToDeath(client, 0);
            Log.d(TAG, token + " left , client size " + mClients.size());

            if (mClients.size() == 0) {
                stopSelf();//没有客户端就停止自己
            }
        }

        @Override
        public void sendMessage(String message) throws RemoteException {
            Log.d(TAG, " sendMessage :" + message);
            onSuccessCallBack(message);
        }

        @Override
        public void registerCallback(IReceiverAidlInterface cb) throws RemoteException {
            Log.d(TAG, "registerCallback " + cb);
            mCallbacks.register(cb);
        }

        @Override
        public void unregisterCallback(IReceiverAidlInterface cb) throws RemoteException {
            Log.d(TAG, "unregisterCallback " + cb);
            mCallbacks.unregister(cb);
        }

        /**此处可用于权限拦截**/
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            /**
             * 包名验证方式
             */
            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    private int findClient(IBinder token) {
        for (int i = 0; i < mClients.size(); i++) {
            if (mClients.get(i).mToken == token) {
                return i;
            }
        }
        return -1;
    }

    private void onSuccessCallBack(String message) {
        final int len = mCallbacks.beginBroadcast();
        for (int i = 0; i < len; i++) {
            try {
                // 通知回调
                mCallbacks.getBroadcastItem(i).receiveMessage(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }

    private final class Client implements IBinder.DeathRecipient {
        private final IBinder mToken;

        private Client(IBinder token) {
            mToken = token;
        }

        @Override
        public void binderDied() {
            // 客户端死掉，执行此回调
            int index = mClients.indexOf(this);
            if (index < 0) {
                return;
            }

            Log.d(TAG, "client died");
            mClients.remove(this);
        }
    }
}

