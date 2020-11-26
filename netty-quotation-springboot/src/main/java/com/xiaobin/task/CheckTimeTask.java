package com.xiaobin.task;

import com.xiaobin.common.AbstractNettyClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/6 12:54
 * @desc 收到交易所的websocket信息，最后一次收到的信息时间超过指定时间，说明断开连接，需要重新连接
 */
@Slf4j
public class CheckTimeTask implements Runnable {

    // netty连接客户端
    private AbstractNettyClient abstractNettyClient;
    // 限定时间(毫秒)
    private long limitedTime = 5000;
    // 收到信息最新时间
    private long latestTime;

    public CheckTimeTask(AbstractNettyClient abstractNettyClient){
        this.abstractNettyClient = abstractNettyClient;
    }

    public void updateLatest(){
        this.latestTime = System.currentTimeMillis();
    }
    @Override
    public void run() {
        log.info("检查时间");
        if(System.currentTimeMillis() - this.latestTime >= this.limitedTime){
            log.error("收消息超时，超过限定值{},准备重新订阅",this.limitedTime);
            abstractNettyClient.reConnect();
        }
    }
}
