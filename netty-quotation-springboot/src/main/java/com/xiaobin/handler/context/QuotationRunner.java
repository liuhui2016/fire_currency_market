package com.xiaobin.handler.context;

import com.xiaobin.common.AbstractNettyClient;
import com.xiaobin.common.MarketEnum;
import com.xiaobin.task.WebsocketMessageHandlerTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class QuotationRunner implements ApplicationRunner {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Value("${netty.websocket.client.code}")
    private Integer code;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String websocketUrl = MarketEnum.getUrlByCode(code);
        Class clazz = MarketEnum.getClassByCode(code);
        Constructor constructor = clazz.getConstructor(String.class);
        AbstractNettyClient abstractNettyClient = (AbstractNettyClient)constructor.newInstance(websocketUrl);
        executorService.execute(()->{
            abstractNettyClient.clientConnect();
        });
        executorService.execute(new WebsocketMessageHandlerTask());
        // 启动交易对队列take任务
        abstractNettyClient.startTradingTask();
        // 启动超时检测任务
        abstractNettyClient.startCheckTimeTask();
        // 30秒一次重新订阅
        abstractNettyClient.reSubTask();

    }
}
