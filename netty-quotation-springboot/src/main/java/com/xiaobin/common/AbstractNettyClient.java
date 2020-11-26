package com.xiaobin.common;

import com.xiaobin.task.CheckTimeTask;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/2 21:51
 * @desc
 */
@Slf4j
public abstract class AbstractNettyClient implements NettyClientConnect,ClientChannel,HandSubMessageTask {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    protected EventLoopGroup loopGroup;
    protected Bootstrap bootstrap;
    @Getter
    protected WebSocketClientHandshaker handshaker;
    protected SimpleChannelInboundHandler channelHandler;
    protected URI uri;
    protected String host;
    protected Integer port;
    protected SslContext sslCtx;

    protected Channel channel;

    // 超时检测任务
    protected CheckTimeTask checkTimeTask;

    private String websocketUrl;
    public AbstractNettyClient(String websocketUrl) {
       this.websocketUrl = websocketUrl;
       initParam(websocketUrl);
    }

    public abstract void clientConnect();

    private void initParam(String websocketUrl){
        try {
            this.uri = new URI(websocketUrl);
            this.host = this.uri.getHost() == null ? "127.0.0.1" : this.uri.getHost();
            String scheme = this.uri.getScheme() == null ? "http" : this.uri.getScheme();
            if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                log.error("只能支持ws链接");
                throw new UnsupportedAddressTypeException();
            }
            if ( this.uri.getPort() == -1) {
                if ("http".equalsIgnoreCase(scheme) || "ws".equalsIgnoreCase(scheme)) {
                    this.port = 80;
                } else if ("wss".equalsIgnoreCase(scheme)) {
                    this.port = 443;
                } else {
                    this.port = -1;
                }
            } else {
                this.port = this.uri.getPort();
            }
            final boolean ssl = "wss".equalsIgnoreCase(scheme);
            this.sslCtx = null;
            if (ssl) {
                try {
                    sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                } catch (SSLException e) {
                    e.printStackTrace();
                }
            } else {
                sslCtx = null;
            }
            this.checkTimeTask = new CheckTimeTask(this);
        } catch (URISyntaxException e) {
            log.error("websocketUrl URISyntaxException.... msg:{}", e);
        }
    }

    @Override
    public void initConnect(URI websocketURI, SimpleChannelInboundHandler channelHandler) {
        loopGroup = new NioEventLoopGroup(2);
        this.channelHandler = channelHandler;
        this.bootstrap = new Bootstrap().option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true).group(loopGroup).channel(NioSocketChannel.class).handler(new NettyClientHandler(this.sslCtx, this.host, this.port, channelHandler));
    }

    @Override
    public boolean connect(SimpleChannelInboundHandler channelHandler) {

        // 初始化连接
        initConnect(this.uri, channelHandler);

        try {
            this.channel = bootstrap.connect(this.host,this.port).sync().channel();
//            this.handshaker.handshake(this.channel);
            log.info("连接成功");
            return true;
        } catch (Exception e) {
            log.error("连接失败");
            this.closeChannel();
            return false;

        }

    }

    // 检测队列消息
    public ScheduledFuture startTradingTask(){
        return scheduledExecutorService.schedule(()->{
            this.process();
        },10, TimeUnit.SECONDS);
    }

    // 检查时间
    public ScheduledFuture startCheckTimeTask(){
        return scheduledExecutorService.scheduleAtFixedRate(this.checkTimeTask, 10, 30, TimeUnit.SECONDS);
    }

    // 重新订阅
    public ScheduledFuture reSubTask(){
        return scheduledExecutorService.scheduleAtFixedRate(()->{
            this.reSub();
        },10,30, TimeUnit.SECONDS);
    }


    @Override
    public void reConnect() {
        this.closeChannel();
        this.clientConnect();
        this.reSub();
    }


    //初始订阅
    public abstract void initSub();
}
