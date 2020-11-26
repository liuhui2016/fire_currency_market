package com.xiaobin.service;

import com.xiaobin.entity.vo.MarketDataVo;
import com.xiaobin.entity.vo.MarketDepthDataVo;
import com.xiaobin.http.entity.*;
import com.xiaobin.http.service.MarketDataService;
import com.xiaobin.http.utils.ApiServiceGenerator;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.util.Optional;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 12:28
 * @desc
 */
@Service
public class MarketService implements InitializingBean {

    private static MarketDataService marketDataService;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.marketDataService = ApiServiceGenerator.generatorService(MarketDataService.class);
    }

    public MarketResponseData<MarketData> getHistoryKlineData(MarketDataVo marketDataVo){
        Call<MarketResponseData<MarketData>> historyKlineData = marketDataService.getHistoryKlineData(marketDataVo.getSymbol(), marketDataVo.getPeriod(), marketDataVo.getSize());
        return handleCallData(historyKlineData);
    }


    public DepthResponseData getMarketDepthData(MarketDepthDataVo marketDepthDataVo){
        Call<DepthResponseData> marketDepthData = marketDataService.getMarketDepthData(marketDepthDataVo.getSymbol(), marketDepthDataVo.getDepth(), marketDepthDataVo.getType());
        return handleCallData(marketDepthData);
    }

    public TradeResponseData getMarketTradeData(String symbol){
        return handleCallData(marketDataService.getMarketTradeData(symbol));
    }

    public TradeHistoryResponseData getMarketHistoryTradeData(String symbol,Integer size){
        return handleCallData(marketDataService.getMarkethistoryTradeData(symbol,size));
    }



    private <T> T handleCallData(Call<T> t){
        return Optional.ofNullable(ApiServiceGenerator.getCallResponseBody(t)).orElse(null);
    }


}
