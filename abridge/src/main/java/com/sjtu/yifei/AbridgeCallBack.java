package com.sjtu.yifei;

/**
 * 类描述： 创建人：yifei 创建时间：2018/12/18 修改人： 修改时间： 修改备注：
 */
public interface AbridgeCallBack {
    /**
     * 接收消息
     *
     * @param message 收到的消息内容
     */
    void receiveMessage(String message);
}
