package com.xiaobin.http.entity;

import lombok.Data;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 12:21
 * @desc
 */
@Data
public class MarketData {

    /**
     * 调整为新加坡时间的时间戳，单位秒，并以此作为此K线柱的id
     */
    private Long id;

    /**
     * 以基础币种计量的交易量
     */
    private Double amount;

    /**
     * 交易次数
     */
    private Integer count;

    /**
     * 本阶段开盘价
     */
    private Double open;

    /**
     * 本阶段收盘价
     */
    private Double close;

    /**
     * 本阶段最低价
     */
    private Double low;

    /**
     * 本阶段最高价
     */
    private Double high;

    /**
     * 以报价币种计量的交易量
     */
    private Double vol;

}
