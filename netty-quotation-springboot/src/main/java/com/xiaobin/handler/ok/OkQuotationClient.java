package com.xiaobin.handler.ok;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiaobin.common.AbstractNettyClient;
import com.xiaobin.constant.GlobalConstant;
import com.xiaobin.constant.OkQuotationConstant;
import com.xiaobin.handler.huobi.quotation.HuobiQuotationClientHandler;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/2 22:00
 * @desc
 */
@Slf4j
public class OkQuotationClient extends AbstractNettyClient{

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);

    private LongAdder longAdder = new LongAdder();

    public OkQuotationClient(String websocketUrl) {
        super(websocketUrl);
    }

    public void clientConnect() {
        this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(this.uri, WebSocketVersion.V13, (String) null, true, new DefaultHttpHeaders());
        OkQuotationClientHandler clientHandler = new OkQuotationClientHandler(this.handshaker, this);
        boolean connectFlag = this.connect(clientHandler);
        if(!connectFlag){
            log.error("连接ok失败，请检查地址是否正确");
        }else{
           this.initSub();
        }
    }

    @Override
    public void initSub() {
        log.info("初始订阅");
        scheduledExecutorService.scheduleAtFixedRate(()->{
            String s = "ping";
            this.send(s);
        },3,1,TimeUnit.SECONDS);
        try {
            Thread.sleep(3000);
            String msg = OkQuotationConstant.loginOkWs();
//                GlobalConstant.linkedBlockingQueue.put(msg);
            String s = "{\"op\": \"subscribe\", \"args\": [\"spot/candle60s:BTC-USDT\",\"spot/candle60s:ETH-USDT\"]}";
            this.send(s);
        } catch (Exception e) {
            log.error("添加默认订阅交易对异常,e:{}",e);
        }
    }

    @Override
    public boolean isAlive() {
        return this.channel != null && this.channel.isActive();
    }

    @Override
    public void sendPing() {
        this.send("ping");
    }

    @Override
    public void sendPong() {
        this.send("pong");
    }

    @Override
    public void onReceiveMessage(String msg) {
        log.info("收到OK消息："+msg);
    }

    @Override
    public void send(String msg) {
        if(!this.isAlive()){
            log.error("channel未激活，发送消息失败");
            return;
        }
        this.channel.writeAndFlush(new TextWebSocketFrame(msg));
        log.info("发送消息：{}",msg);
    }

    @Override
    public void reSub() {

    }

    @Override
    public void process() {
        //　获取订阅的消息
        for(;;){
            try {
                String subMsg = GlobalConstant.linkedBlockingQueue.take();
                JSONObject jsonObject = JSONUtil.parseObj(subMsg);
                String sub = (String)jsonObject.get("op");
                if(!sub.equals("login")){
                    GlobalConstant.set.add(sub);
                }
                this.send(subMsg);
            } catch (InterruptedException e) {
                log.error("获取队列消息失败,e:{}",e);
            }
        }
    }

    @Override
    public void closeChannel() {
        try{
            if(this.loopGroup != null){
                this.loopGroup.shutdownGracefully();
            }
            if(this.channel != null){
                this.channel.pipeline().remove(HttpClientCodec.class.getSimpleName());
                this.channel.pipeline().remove(HttpObjectAggregator.class.getSimpleName());
                this.channel.pipeline().remove(HuobiQuotationClientHandler.class.getSimpleName());
                this.channel.close();
            }

        }catch (Exception e){
            log.warn("关闭连接出现异常，e:{}",e);
        }finally {
            this.loopGroup = null;
            this.channel = null;
        }
    }
}
