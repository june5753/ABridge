package com.fiture.platform;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.fiture.platform.aidl.IReceiverAidlInterface;
import com.fiture.platform.aidl.ISenderAidlInterface;
import com.fiture.platform.aidl.Msg;

import java.util.ArrayList;
import java.util.List;


final class AbridgeManager {
    private static final String TAG = "AbridgeManager";
    private static final String BIND_SERVICE_ACTION = "android.intent.action.ICALL_AIDL_FITURE";
    private static final String BIND_SERVICE_COMPONENT_NAME_CLS = "com.fiture.platform.service.AbridgeService";
    private static AbridgeManager instance;

    private Application mApplication;
    private String mServicePkgName;
    private Handler mHandler;
    private final List<AbridgeCallBack> mList;

    private IBinder mBinder;

    private AbridgeManager() {
        mList = new ArrayList<>();
    }

    public static AbridgeManager getInstance() {
        if (instance == null) {
            synchronized (AbridgeManager.class) {
                if (instance == null) {
                    instance = new AbridgeManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     */
    public void init(Application application, String servicePkgName) {
        this.mApplication = application;
        this.mServicePkgName = servicePkgName;
        mHandler = new Handler(application.getMainLooper());
        mBinder = new Binder();
    }

    public void registerRemoteCallBack(AbridgeCallBack callBack) {
        if (mList != null) {
            mList.add(callBack);
        }
    }

    public void uRegisterRemoteCallBack(AbridgeCallBack callBack) {
        if (mList != null && callBack != null) {
            mList.remove(callBack);
        }
    }

    public void callRemote(String message) {
        if (mSenderAidlInterface == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive");
            return;
        }
        if (!TextUtils.isEmpty(message)) {
            try {
                mSenderAidlInterface.sendMessage(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void callRemoteMsg(Msg message) {
        if (mSenderAidlInterface == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive");
            return;
        }
        if (!TextUtils.isEmpty(message.getMsg())) {
            try {
                mSenderAidlInterface.sendMsg(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private ISenderAidlInterface mSenderAidlInterface;

    private final IReceiverAidlInterface mReceiverAidlInterface = new IReceiverAidlInterface.Stub() {

        @Override
        public void receiveMessage(final String json) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (AbridgeCallBack medium : mList) {
                        medium.receiveMessage(json);
                    }
                }
            });
        }

        @Override
        public void onReceive(final Msg msg) throws RemoteException {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (AbridgeCallBack medium : mList) {
                        medium.receiveMsg(msg);
                    }
                }
            });
        }
    };

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mSenderAidlInterface = ISenderAidlInterface.Stub.asInterface(iBinder);
            if (mSenderAidlInterface == null) {
                Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive");
                return;
            }
            try {
                mSenderAidlInterface.join(mBinder);
                mSenderAidlInterface.registerCallback(mReceiverAidlInterface);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mSenderAidlInterface = null;
        }
    };

    /**
     * 启动服务
     */
    public void startAndBindService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(BIND_SERVICE_ACTION);
        serviceIntent.setComponent(new ComponentName(mServicePkgName, BIND_SERVICE_COMPONENT_NAME_CLS));
        //适配8.0以上的服务转前台服务 清单文件AndroidManifest中有配置 android.permission.FOREGROUND_SERVICE
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //适配8.0机制
            mApplication.startForegroundService(serviceIntent);
        } else {
            mApplication.startService(serviceIntent);
        }
        //多个client可以同时绑定一个Service 但是当所有Client unbind后，Service会退出
        //但是我们希望unbind后Service仍保持运行，这样的情况下，可以同时调用bindService和startService
        mApplication.bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 关闭服务
     */
    public void unBindService() {
        if (mSenderAidlInterface == null) {
            Log.e(TAG, "error: ipc process not started，please make sure ipc process is alive");
            return;
        }
        try {
            mSenderAidlInterface.unregisterCallback(mReceiverAidlInterface);
            mSenderAidlInterface.leave(mBinder);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mApplication.unbindService(mServiceConnection);
    }
}
