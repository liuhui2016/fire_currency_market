package com.xiaobin.constant;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/4 21:06
 * @desc　全局常量和变量
 */
@Slf4j
public class GlobalConstant {

    // websocket连接重试次数
    public static final Integer number = 3;

    // 订阅交易对处理队列
    public static LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<String>();

    // websocket 连接的 session
    public static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    // websocket 消息处理队列
    public static LinkedBlockingQueue<String> wsMsgQueue = new LinkedBlockingQueue<String>();

    // websocket 历史消息处理队列
    public static LinkedBlockingQueue<String> historyMsgQueue = new LinkedBlockingQueue<String>();

    // 已经订阅的交易对
    public static Set<String> set = new HashSet<>();

    // 交易对对应的symbol btcusdt -> BTC/USDT
    public static Map<String, String> symbolMap = new HashMap<>();

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
}
