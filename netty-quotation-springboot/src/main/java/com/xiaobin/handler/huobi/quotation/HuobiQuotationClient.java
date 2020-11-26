package com.xiaobin.handler.huobi.quotation;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xiaobin.common.AbstractNettyClient;
import com.xiaobin.constant.GlobalConstant;
import com.xiaobin.constant.HuobiQuotationConstant;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/2 22:00
 * @desc
 */
@Slf4j
public class HuobiQuotationClient extends AbstractNettyClient{

    private LongAdder longAdder = new LongAdder();

    public HuobiQuotationClient(String websocketUrl) {
        super(websocketUrl);
    }

    public void clientConnect() {
        this.handshaker = WebSocketClientHandshakerFactory.newHandshaker(this.uri, WebSocketVersion.V13, (String) null, true, new DefaultHttpHeaders());
        HuobiQuotationClientHandler clientHandler = new HuobiQuotationClientHandler(this.handshaker, this);
        boolean connectFlag = this.connect(clientHandler);
        if(!connectFlag){
            log.error("连接火币失败，请检查地址是否正确");
        }else{
           this.initSub();
        }
    }

    @Override
    public void initSub() {
        log.info("初始订阅");
        subKline();
        subMarketData();
        subMarketDepth();
    }

    /**
     * 订阅k线数据
     */
    private void subKline(){
        HuobiQuotationConstant.TradingPair[] tradingPairs = HuobiQuotationConstant.TradingPair.values();
        for (HuobiQuotationConstant.TradingPair tradingPair : tradingPairs) {
            HuobiQuotationConstant.Period[] periods = HuobiQuotationConstant.Period.values();
            for (HuobiQuotationConstant.Period period : periods) {
                String subMsg = HuobiQuotationConstant.formatKlineSub(tradingPair, period);
                try {
                    GlobalConstant.symbolMap.put(tradingPair.getTrade(),tradingPair.getName());
                    GlobalConstant.linkedBlockingQueue.put(subMsg);
                } catch (InterruptedException e) {
                    log.error("添加默认订阅交易对异常,e:{}",e);
                }
            }
        }
    }

    /**
     * 订阅市场概要数据
     * @return
     */
    private void subMarketData() {
        HuobiQuotationConstant.TradingPair[] tradingPairs = HuobiQuotationConstant.TradingPair.values();
        for (HuobiQuotationConstant.TradingPair tradingPair : tradingPairs) {
            String subMsg = HuobiQuotationConstant.formatMarketDetailSub(tradingPair);
            try {
                GlobalConstant.linkedBlockingQueue.put(subMsg);
            } catch (InterruptedException e) {
                log.error("添加默认订阅交易对异常,e:{}",e);
            }
        }
    }

    /**
     * 订阅市场深度行情数据
     */
    private void subMarketDepth(){
        HuobiQuotationConstant.TradingPair[] tradingPairs = HuobiQuotationConstant.TradingPair.values();
        for (HuobiQuotationConstant.TradingPair tradingPair : tradingPairs) {
            String subMsg = HuobiQuotationConstant.formatMarketDepthSub(tradingPair,HuobiQuotationConstant.DepthType.STEP0);
            try {
                GlobalConstant.linkedBlockingQueue.put(subMsg);
            } catch (InterruptedException e) {
                log.error("添加默认订阅交易对异常,e:{}",e);
            }
        }
    }

    @Override
    public boolean isAlive() {
        return this.channel != null && this.channel.isActive();
    }

    @Override
    public void sendPing() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ping", System.currentTimeMillis());
        this.send(jsonObject.toString());
    }

    @Override
    public void sendPong() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pong", System.currentTimeMillis());
        this.send(jsonObject.toString());
    }

    @Override
    public void onReceiveMessage(String msg) {
        // 更新收到消息最新时间
        this.checkTimeTask.updateLatest();
        if (msg.contains("ping")) {
            this.send(msg.replace("ping", "pong"));
            return;
        }
        if (msg.contains("pong")) {
            this.sendPing();
            return;
        }
        try {
            this.sendPong();
            GlobalConstant.wsMsgQueue.put(msg);
        } catch (InterruptedException e) {
            log.error("websocket message queue put 异常，异常信息：{}",e);
        }
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
        log.info("重新订阅");
        for (String  trade: GlobalConstant.set) {
            String subId = UUID.randomUUID().toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sub", trade);
            jsonObject.put("id", subId);
            String msg = jsonObject.toString();
            try {
                GlobalConstant.linkedBlockingQueue.put(msg);
            } catch (InterruptedException e) {
                log.error("添加默认订阅交易对异常,e:{}",e);
            }
        }
    }

    @Override
    public void process() {
        //　获取订阅的消息
        for(;;){
            try {
                String subMsg = GlobalConstant.linkedBlockingQueue.take();
                JSONObject jsonObject = JSONUtil.parseObj(subMsg);
                String sub = (String)jsonObject.get("sub");
                GlobalConstant.set.add(sub);
                this.send(subMsg);
            } catch (InterruptedException e) {
                log.error("获取队列消息失败,e:{}",e);
            }
        }
    }

    @Override
    public void closeChannel() {
        try{

            if(this.channel != null){
                this.channel.pipeline().remove(HttpClientCodec.class.getSimpleName());
                this.channel.pipeline().remove(HttpObjectAggregator.class.getSimpleName());
                this.channel.pipeline().remove(HuobiQuotationClientHandler.class.getSimpleName());
                this.channel.close();
            }
            if(this.loopGroup != null){
                this.loopGroup.shutdownGracefully();
            }
        }catch (Exception e){
            log.warn("关闭连接出现异常，e:{}",e);
        }finally {
            this.loopGroup = null;
            this.channel = null;
        }
    }
}
