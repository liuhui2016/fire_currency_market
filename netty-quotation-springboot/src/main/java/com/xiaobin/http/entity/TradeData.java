package com.xiaobin.http.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TradeData {
    private Double id;
    private Long ts;

    private Long tradeId;
    private Double amount;
    private Double price;
    private String direction;

    @JsonProperty("tradeId")
    public Long getTradeId(){
        return this.tradeId;
    }

    @JsonProperty("trade-id")
    public void setTradeId(Long tradeId){
        this.tradeId = tradeId;
    }
}
