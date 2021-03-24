package com.fiture.platform.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fiture.platform.aidl.IReceiverAidlInterface;
import com.fiture.platform.aidl.ISenderAidlInterface;
import com.fiture.platform.aidl.Msg;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端 同时把服务端配置到另外的进程 -> android:process=”:aidl”
 * @author juneyang
 */
public class AbridgeService extends Service {

    private static final String TAG = "AbridgeService";
    private final List<Client> mClients = new ArrayList<>();
    private final RemoteCallbackList<IReceiverAidlInterface> mCallbacks = new RemoteCallbackList<>();

    public AbridgeService() {
        Log.e(TAG, "launched");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("default", "default",
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
            Notification notification = builder.setContentTitle("AIDL服务正在运行")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(false)
                    .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                    .build();
            startForeground(1, notification);
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
            Log.d(TAG, token + " join , client size " + mClients.size() +
                    ",clientId:" + token.getInterfaceDescriptor());
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
            onSuccessCallBackStr(message);
        }

        @Override
        public void sendMsg(Msg msg) {
            Log.d(TAG, " sendMessage :" + msg.getMsg() + "time:" + msg.getTime());
            onSuccessCallBackMsg(msg);
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
            //包名验证方式
            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            //其他方式验证
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

    private void onSuccessCallBackStr(String message) {
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

    private void onSuccessCallBackMsg(Msg message) {
        final int len = mCallbacks.beginBroadcast();
        for (int i = 0; i < len; i++) {
            try {
                // 通知回调
                mCallbacks.getBroadcastItem(i).onReceive(message);
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

