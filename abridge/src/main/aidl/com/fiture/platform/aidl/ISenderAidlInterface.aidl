// ICall.aidl
package com.fiture.platform.aidl;
import com.fiture.platform.aidl.Msg;

import com.fiture.platform.aidl.IReceiverAidlInterface;

interface ISenderAidlInterface {

    void join(IBinder token);

    void leave(IBinder token);

     //客户端->服务端
    void sendMessage(String json);

    void sendMsg(in Msg msg);

    void registerCallback(IReceiverAidlInterface cb);

    void unregisterCallback(IReceiverAidlInterface cb);
}
