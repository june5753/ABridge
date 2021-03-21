// ICall.aidl
package com.fiture.platform.aidl;

import com.fiture.platform.aidl.IReceiverAidlInterface;

interface ISenderAidlInterface {

    void join(IBinder token);

    void leave(IBinder token);

    void sendMessage(String json);

    void registerCallback(IReceiverAidlInterface cb);

    void unregisterCallback(IReceiverAidlInterface cb);
}
