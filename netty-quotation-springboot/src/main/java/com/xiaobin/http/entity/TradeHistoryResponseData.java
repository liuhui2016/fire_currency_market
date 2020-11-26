package com.xiaobin.http.entity;

import lombok.Data;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/13 0:31
 * @desc
 */
@Data
public class TradeHistoryResponseData {
    private String ch;
    private String status;
    private Long ts;
    private TradeTick[] data;
}

