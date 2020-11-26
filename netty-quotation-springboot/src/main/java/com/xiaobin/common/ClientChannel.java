package com.xiaobin.common;
/**
 * @author xiaobin qq:944484545
 * @date 2020/4/3 17:51
 * @desc channel操作模板方法
 */
public interface ClientChannel {


    // 是否存活
    boolean isAlive();

    // 发送 ping
    void sendPing();

    // 发送 pong
    void sendPong();

    // 接收消息
    void onReceiveMessage(String msg);

    // 发送信息
    void send(String msg);

    // 重新订阅
    void reSub();


}
