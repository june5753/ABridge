// ICallback.aidl
package com.sjtu.yifei.aidl;

// Declare any non-default types here with import statements

interface IReceiverAidlInterface {
     // in 表示能读取到传入的对象中的值，而修改后不会修改传入对象的内容
    void receiveMessage(in String json);
}
