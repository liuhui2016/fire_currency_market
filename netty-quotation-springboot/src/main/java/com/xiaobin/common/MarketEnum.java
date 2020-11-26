package com.xiaobin.common;

import com.xiaobin.constant.HuobiQuotationConstant;
import com.xiaobin.handler.huobi.quotation.HuobiQuotationClient;
import com.xiaobin.handler.ok.OkQuotationClient;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/4 20:23
 * @desc
 */
public enum MarketEnum {
    // 火币网
    Huobi(0, HuobiQuotationConstant.WEBSOCKET_BASE_URL, HuobiQuotationClient.class),
    // ok网
    OK(1,"wss://okcoincomreal.bafang.com:8443/ws/v3", OkQuotationClient.class);
//    OK(1,"wss://real.okcoin.com:8443/ws/v3", OkQuotationClient.class);

    private Integer code;
    private String url;
    private Class clazz;

    MarketEnum(Integer code, String url,Class<? extends AbstractNettyClient> clazz) {
        this.code = code;
        this.url = url;
        this.clazz = clazz;
    }


    public static String getUrlByCode(Integer code){
        for (MarketEnum marketEnum : MarketEnum.values()) {
            if(marketEnum.code == code){
                return marketEnum.url;
            }
        }
        return "";
    }
    public static Class getClassByCode(Integer code){
        for (MarketEnum marketEnum : MarketEnum.values()) {
            if(marketEnum.code == code){
                return marketEnum.clazz;
            }
        }
        return null;
    }
}
