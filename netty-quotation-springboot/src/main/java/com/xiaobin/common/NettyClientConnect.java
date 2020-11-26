package com.xiaobin.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.URI;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/2 21:33
 * @desc netty客户端连接模板接口方法
 */
public interface NettyClientConnect {


    // 初始化连接
    void initConnect(URI websocketURI, SimpleChannelInboundHandler channelHandler);

    // 进行连接
    boolean connect(SimpleChannelInboundHandler channelHandler);

    // 重新连接
    void reConnect();

    // 关闭连接
    void closeChannel();

}
