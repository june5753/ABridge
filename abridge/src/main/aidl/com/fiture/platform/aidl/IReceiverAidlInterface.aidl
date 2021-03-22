// ICallback.aidl
package com.fiture.platform.aidl;
import com.fiture.platform.aidl.Msg;
// Declare any non-default types here with import statements

interface IReceiverAidlInterface {
     // in 表示能读取到传入的对象中的值，而修改后不会修改传入对象的内容
    void receiveMessage(in String json);

    void onReceive(in Msg msg);
}
