package com.xiaobin.controller;

import com.xiaobin.constant.HuobiQuotationConstant;
import com.xiaobin.entity.dto.SymbolDto;
import com.xiaobin.entity.vo.MarketDataVo;
import com.xiaobin.entity.vo.MarketDepthDataVo;
import com.xiaobin.response.ResponseServer;
import com.xiaobin.service.MarketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 12:18
 * @desc 市场行情数据Controller
 */
@RestController
@RequestMapping("/v1/market")
@Api(tags = "市场行情数据Controller",value = "市场行情数据Controller")
public class MarketController {


    @Autowired
    private MarketService marketService;

    @GetMapping("/")
    @ApiOperation(value = "返回历史K线数据",notes = "返回历史K线数据")
    public ResponseServer markets(MarketDataVo marketDataVo){
        return ResponseServer.ok(marketService.getHistoryKlineData(marketDataVo));
    }

    @GetMapping("/symbol")
    @ApiOperation(value = "返回支持的交易对",notes = "返回支持的交易对")
    public ResponseServer symbol(){
        HuobiQuotationConstant.TradingPair[] tradingPairs = HuobiQuotationConstant.TradingPair.values();
        List<SymbolDto> symbolDtos = new ArrayList<>();
        for (HuobiQuotationConstant.TradingPair tradingPair : tradingPairs) {
            SymbolDto symbolDto = new SymbolDto();
            symbolDto.setSymbol(tradingPair.getTrade());
            symbolDto.setSymbolUp(tradingPair.getName());
            symbolDtos.add(symbolDto);
        }
        return ResponseServer.ok(symbolDtos);
    }

    @GetMapping("/depth")
    @ApiOperation(value = "指定交易对的当前市场深度数据",notes = "指定交易对的当前市场深度数据")
    public ResponseServer depths(MarketDepthDataVo marketDepthDataVo){
        return ResponseServer.ok(marketService.getMarketDepthData(marketDepthDataVo));
    }

    @GetMapping("/latest/trade")
    @ApiOperation(value = "指定交易对最新的一个交易记录",notes = "指定交易对最新的一个交易记录")
    public ResponseServer latestTrade(@RequestParam("symbol")String symbol){
        return ResponseServer.ok(marketService.getMarketTradeData(symbol));
    }

    @GetMapping("/trade")
    @ApiOperation(value = "返回指定交易对近期的所有交易记录",notes = "返回指定交易对近期的所有交易记录")
    public ResponseServer trades(@RequestParam("symbol")String symbol,@RequestParam("size")Integer size){
        return ResponseServer.ok(marketService.getMarketHistoryTradeData(symbol,size));
    }

}
