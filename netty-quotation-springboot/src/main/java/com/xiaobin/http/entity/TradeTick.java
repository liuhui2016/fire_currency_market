package com.xiaobin.http.entity;

import lombok.Data;

@Data
public class TradeTick{
    private Long id;
    private Long ts;
    private TradeData[] data;
}