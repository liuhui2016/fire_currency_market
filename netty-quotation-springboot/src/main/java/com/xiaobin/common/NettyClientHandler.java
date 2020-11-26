package com.xiaobin.common;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/2 22:18
 * @desc
 */
public class NettyClientHandler extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;
    private String host;
    private Integer port;
    private SimpleChannelInboundHandler channelHandler;

    public NettyClientHandler(SslContext sslContext, String host, Integer port, SimpleChannelInboundHandler channelHandler){
        this.sslContext = sslContext;
        this.host = host;
        this.port = port;
        this.channelHandler = channelHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(socketChannel.alloc(), host, port));
        }
        //pipeline可以同时放入多个handler,最后一个为自定义hanler
        pipeline.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), channelHandler);
    }
}
