package com.sjtu.yifei;

import android.app.Application;
import android.os.Message;
import android.support.annotation.NonNull;

/**
 * test 2
 */

public final class IBridge {

    private IBridge() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(@NonNull final Application app, String servicePkgName, String clientId) {
        AbridgeManager.getInstance().init(app, servicePkgName,clientId);
        AbridgeManager.getInstance().startAndBindService();
    }

    public static void recycle() {
        AbridgeManager.getInstance().unBindService();
    }

    public static void sendAIDLMessage(String message) {
        AbridgeManager.getInstance().callRemote(message);
    }

    public static void registerAIDLCallBack(AbridgeCallBack callBack) {
        AbridgeManager.getInstance().registerRemoteCallBack(callBack);
    }

    public static void uRegisterAIDLCallBack(AbridgeCallBack callBack) {
        AbridgeManager.getInstance().uRegisterRemoteCallBack(callBack);
    }
}
