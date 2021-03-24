package com.fiture.platform;

import android.app.Application;
import android.support.annotation.NonNull;

import com.fiture.platform.aidl.Msg;

/**
 * @author juneyang
 */

public final class IBridge {

    private IBridge() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(@NonNull final Application app, String servicePkgName) {
        AbridgeManager.getInstance().init(app, servicePkgName);
        AbridgeManager.getInstance().startAndBindService();
    }

    public static void recycle() {
        AbridgeManager.getInstance().unBindService();
    }

    public static void sendAIDLMessage(String message) {
        AbridgeManager.getInstance().callRemote(message);
    }

    public static void sendAIDLMsg(Msg message) {
        AbridgeManager.getInstance().callRemoteMsg(message);
    }

    public static void registerAIDLCallBack(AbridgeCallBack callBack) {
        AbridgeManager.getInstance().registerRemoteCallBack(callBack);
    }

    public static void uRegisterAIDLCallBack(AbridgeCallBack callBack) {
        AbridgeManager.getInstance().uRegisterRemoteCallBack(callBack);
    }
}
