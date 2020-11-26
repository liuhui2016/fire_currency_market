package com.xiaobin.http.service;

import com.xiaobin.http.entity.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 12:09
 * @desc 行情数据Http请求
 */
public interface MarketDataService {

    /**
     * 此接口返回历史K线数据。
     * @param symbol btcusdt, ethbtc..
     * @param period 返回数据时间粒度 1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1mon, 1week, 1year
     * @param size  返回 K 线数据条数 [1, 2000]
     */
    @GET("/market/history/kline")
    public Call<MarketResponseData<MarketData>> getHistoryKlineData(@Query("symbol")String symbol, @Query("period")String period, @Query("size")Integer size);

    /**
     * 此接口返回指定交易对的当前市场深度数据。
     * @param symbol 交易对btcusdt, ethbtc..
     * @param depth 返回深度的数量	5，10，20
     * @param type  深度的价格聚合度	step0，step1，step2，step3，step4，step5
     * @return
     */
    @GET("/market/depth")
    public Call<DepthResponseData> getMarketDepthData(@Query("symbol")String symbol,@Query("depth")Integer depth,@Query("type")String type);


    /**
     * 此接口返回指定交易对最新的一个交易记录。
     * @param symbol 交易对
     * @return
     */
    @GET("/market/trade")
    public Call<TradeResponseData> getMarketTradeData(@Query("symbol")String symbol);

    /**
     * 此接口返回指定交易对近期的所有交易记录。
     * @param symbol 交易对
     * @param size 返回的交易记录数量，最大值2000
     * @return
     */
    @GET("/market/history/trade")
    public Call<TradeHistoryResponseData> getMarkethistoryTradeData(@Query("symbol")String symbol, @Query("size")Integer size);

}
