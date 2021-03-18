package com.sjtu.yifei;

import com.sjtu.yifei.aidl.Msg;

/**
 * @author juneyang
 */
public interface AbridgeCallBack {
    /**
     * 接收消息
     *
     * @param message 收到的消息内容
     */
    void receiveMessage(String message);

    /**
     * 接收的消息 java bean格式
     */
    void receiveMsg(Msg msg);
}
