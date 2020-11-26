package com.xiaobin.task;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.xiaobin.constant.GlobalConstant;
import com.xiaobin.constant.HuobiQuotationConstant;
import com.xiaobin.protobuf.QuotationMessage;
import com.xiaobin.socketjs.QuotationGroupSendMsg;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 收到服务端发来的消息，进行处理
 */
@Slf4j
public class WebsocketMessageHandlerTask implements Runnable {


    private static final String KLINE_PATTERN = HuobiQuotationConstant.KLINE_SUB.replace(".", "\\.").replace("%s", "(.*)");
    private static final String DEPTH_PATTERN = HuobiQuotationConstant.MARKET_DEPTH_SUB.replace(".", "\\.").replace("%s", "(.*)");
    private static final String TRADE_PATTERN = HuobiQuotationConstant.TRADE_DETAIL_SUB.replace(".", "\\.").replace("%s", "(.*)");
    private static final String MARKET_PATTERN = HuobiQuotationConstant.MARKET_DETAIL_SUB.replace(".", "\\.").replace("%s", "(.*)");

    @Override
    public void run() {
        for (; ; ) {
            try {
                String msg = GlobalConstant.wsMsgQueue.take();
                log.info(msg);
                QuotationMessage.Message message = builderMessage(msg);
                if(!Objects.isNull(message)){
                    QuotationGroupSendMsg.msgQueue.put(message);
                }
            } catch (Exception e) {
                log.error("websocket send message error,异常信息：{}",e);
                continue;
            }
        }
    }



    private  QuotationMessage.Message builderMessage(String wsMsg){
        QuotationMessage.Message.Builder message = QuotationMessage.Message.newBuilder();
        JSONObject jsonObject = new JSONObject(wsMsg);
        String chMsg = (String)jsonObject.get("ch");
        Long ts = jsonObject.getLong("ts");
        if(!Objects.isNull(chMsg)){
            QuotationMessage.Message.MessageType messageType = messageTypePredicate(chMsg);
            if(!Objects.isNull(messageType)){
                message.setCh(chMsg);
                message.setTs(ts);
                message.setMessageType(messageType);
                switch (messageType){
                    case KLineType:{
                        Pattern p = Pattern.compile(KLINE_PATTERN);
                        Matcher m = p.matcher(chMsg);
                        if(m.find()) {
                            message.setKline(builderKLineData(m.group(2),new JSONObject(jsonObject.get("tick"))));
                            message.setSymbol(GlobalConstant.symbolMap.get(m.group(1)));
                        }
                        break;
                    }case DepthType:{
                        message.setDepth(builderDepthData(new JSONObject(jsonObject.get("tick"))));
                        Pattern p = Pattern.compile(DEPTH_PATTERN);
                        Matcher m = p.matcher(chMsg);
                        if(m.find()) {
                            message.setSymbol(GlobalConstant.symbolMap.get(m.group(1)));
                        }
                        break;
                    }case TradeType:{
                        message.setTrade(builderTradeData(new JSONObject(jsonObject.get("tick"))));
                        Pattern p = Pattern.compile(TRADE_PATTERN);
                        Matcher m = p.matcher(chMsg);
                        if(m.find()) {
                            message.setSymbol(GlobalConstant.symbolMap.get(m.group(1)));
                        }
                        break;
                    }
                    case MarketType:{
                        message.setMarket(builderMarketData(new JSONObject(jsonObject.get("tick"))));
                        Pattern p = Pattern.compile(MARKET_PATTERN);
                        Matcher m = p.matcher(chMsg);
                        if(m.find()) {
                            message.setSymbol(GlobalConstant.symbolMap.get(m.group(1)));
                        }
                        break;
                    }
                }
                return message.build();
            }
        }

        return null;

    }

    private QuotationMessage.KLineData builderKLineData(String type,JSONObject data){
        QuotationMessage.KLineData.Builder klineBuilder = QuotationMessage.KLineData.newBuilder();
        klineBuilder.setId(data.getLong("id"));
        klineBuilder.setOpen(data.getDouble("open"));
        klineBuilder.setClose(data.getDouble("close"));
        klineBuilder.setLow(data.getDouble("low"));
        klineBuilder.setHigh(data.getDouble("high"));
        klineBuilder.setAmount(data.getDouble("amount"));
        klineBuilder.setVol(data.getDouble("vol"));
        klineBuilder.setCount(data.getLong("count"));
        klineBuilder.setType(type);
        return klineBuilder.build();
    }
    private QuotationMessage.DepthData builderDepthData(JSONObject data){
        QuotationMessage.DepthData.Builder depthBuilder = QuotationMessage.DepthData.newBuilder();
        JSONArray bids = data.getJSONArray("bids");
        for (int i = 0; i < bids.size(); i++) {
            QuotationMessage.DepthTickArray.Builder depthTickuilder = QuotationMessage.DepthTickArray.newBuilder();
            JSONArray jsonArray = new JSONArray(bids.get(i));
            depthTickuilder.addTickArray((Double) jsonArray.get(0));
            depthTickuilder.addTickArray((Double) jsonArray.get(1));
            depthBuilder.addBids(depthTickuilder.build());
        }

        JSONArray asks = data.getJSONArray("asks");
        for (int i = 0; i < asks.size(); i++) {
            QuotationMessage.DepthTickArray.Builder depthTickuilder = QuotationMessage.DepthTickArray.newBuilder();
            JSONArray jsonArray = new JSONArray(asks.get(i));
            depthTickuilder.addTickArray((Double) jsonArray.get(0));
            depthTickuilder.addTickArray((Double) jsonArray.get(1));
            depthBuilder.addAsks(depthTickuilder.build());
        }
        depthBuilder.setVersion(data.getLong("version"));
        depthBuilder.setTs(data.getLong("ts"));
        return depthBuilder.build();
    }
    private QuotationMessage.TradeData builderTradeData(JSONObject data){
        QuotationMessage.TradeData.Builder tradeBuilder = QuotationMessage.TradeData.newBuilder();
        tradeBuilder.setId(data.getLong("id"));
        tradeBuilder.setTs(data.getLong("ts"));
        JSONArray jsonArray = data.getJSONArray("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            QuotationMessage.TradeDetailData.Builder tradeDetailBuilder = QuotationMessage.TradeDetailData.newBuilder();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            tradeDetailBuilder.setAmount(jsonObject.getDouble("amount"));
            tradeDetailBuilder.setTs(jsonObject.getLong("ts"));
            tradeDetailBuilder.setTradeId(jsonObject.getLong("tradeId"));
            tradeDetailBuilder.setPrice(jsonObject.getDouble("price"));
            tradeDetailBuilder.setDirection(jsonObject.getStr("direction"));
            tradeBuilder.addData(tradeDetailBuilder.build());
        }
        return tradeBuilder.build();
    }
    private QuotationMessage.MarketData builderMarketData(JSONObject data){
        QuotationMessage.MarketData.Builder marketBuilder = QuotationMessage.MarketData.newBuilder();
        marketBuilder.setAmount(data.getDouble("amount"));
        marketBuilder.setOpen(data.getDouble("open"));
        marketBuilder.setClose(data.getDouble("close"));
        marketBuilder.setHigh(data.getDouble("high"));
        marketBuilder.setId(data.getLong("id"));
        marketBuilder.setCount(data.getLong("count"));
        marketBuilder.setLow(data.getDouble("low"));
        marketBuilder.setVol(data.getDouble("vol"));
        return marketBuilder.build();
    }


    private  QuotationMessage.Message.MessageType messageTypePredicate(String chMsg){
        Predicate<String> klineTypePredicate = str -> Pattern.matches(KLINE_PATTERN,str);
        Predicate<String> depthTypePredicate = str -> Pattern.matches(DEPTH_PATTERN,str);
        Predicate<String> tradeTypePredicate = str -> Pattern.matches(TRADE_PATTERN,str);
        Predicate<String> marketTypePredicate = str ->Pattern.matches(MARKET_PATTERN,str);

        if(klineTypePredicate.test(chMsg)){
            return QuotationMessage.Message.MessageType.KLineType;
        }else if(depthTypePredicate.test(chMsg)){
            return QuotationMessage.Message.MessageType.DepthType;
        }else if(tradeTypePredicate.test(chMsg)){
            return QuotationMessage.Message.MessageType.TradeType;
        }else if(marketTypePredicate.test(chMsg)){
            return QuotationMessage.Message.MessageType.MarketType;
        }
        return null;
    }
}
