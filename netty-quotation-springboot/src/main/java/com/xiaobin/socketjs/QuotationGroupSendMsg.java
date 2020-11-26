package com.xiaobin.socketjs;

import com.googlecode.protobuf.format.JsonFormat;
import com.xiaobin.protobuf.QuotationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class QuotationGroupSendMsg {

    public static LinkedBlockingQueue<QuotationMessage.Message> msgQueue = new LinkedBlockingQueue<QuotationMessage.Message>();

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

//    @Autowired
//    private MongoTemplate mongoTemplate;

    public QuotationGroupSendMsg() {
        Executors.newSingleThreadExecutor().execute(new QuotationMessageTask());
    }

    class QuotationMessageTask implements Runnable {
        @Override
        public void run() {
            for (; ; ) {
                try {
                    QuotationMessage.Message take = msgQueue.take();
                    groupSend(take);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }

        private void groupSend(QuotationMessage.Message msg) {
            QuotationMessage.Message.MessageType messageType = msg.getMessageType();
            switch (messageType) {
                case KLineType: {
                    simpMessagingTemplate.convertAndSend("/topic/kline/" + msg.getSymbol()+"/"+msg.getKline().getType(), new JsonFormat().printToString(msg));
                    // 保存mongodb数据库中
//                    convertKline(msg);
                    break;
                }
                case DepthType: {
                    simpMessagingTemplate.convertAndSend("/topic/depth/"+msg.getSymbol(), new JsonFormat().printToString(msg));
                    break;
                }
                case TradeType: {
                    simpMessagingTemplate.convertAndSend("/topic/trade/" + msg.getSymbol(), msg.toString());
                    break;
                }
                case MarketType: {
                    simpMessagingTemplate.convertAndSend("/topic/market", new JsonFormat().printToString(msg));
                    break;
                }
            }
        }

//        private void convertKline(QuotationMessage.Message msg) {
//            KLine kLine = new KLine(msg.getKline().getType());
//            kLine.setSymbol(msg.getSymbol());
//            kLine.setOpenPrice(BigDecimal.valueOf(msg.getKline().getOpen()));
//            kLine.setHighestPrice(BigDecimal.valueOf(msg.getKline().getHigh()));
//            kLine.setLowestPrice(BigDecimal.valueOf(msg.getKline().getLow()));
//            kLine.setClosePrice(BigDecimal.valueOf(msg.getKline().getClose()));
//            kLine.setTime(msg.getKline().getId() * 1000);
//            kLine.setCount((int) msg.getKline().getCount());
//            kLine.setVolume(BigDecimal.valueOf(msg.getKline().getAmount()));
//            kLine.setTurnover(BigDecimal.valueOf(msg.getKline().getVol()));
//            saveAndUpdateKline(kLine);
//        }

//        private void saveAndUpdateKline(KLine kLine) {
//            Query query = new Query();
//            query.addCriteria(Criteria.where("time").is(kLine.getTime()));
//            Update update = new Update();
//            update.set("symbol", kLine.getSymbol());
//            update.set("openPrice", kLine.getOpenPrice());
//            update.set("highestPrice", kLine.getHighestPrice());
//            update.set("lowestPrice", kLine.getLowestPrice());
//            update.set("closePrice", kLine.getClosePrice());
//            update.set("time", kLine.getTime());
//            update.set("period", kLine.getPeriod());
//            update.set("count", kLine.getCount());
//            update.set("volume", kLine.getVolume());
//            update.set("turnover", kLine.getTurnover());
//            mongoTemplate.upsert(query, update, "exchange_kline_" + kLine.getSymbol() + "_" + kLine.getPeriod());
//
//        }
    }
}
