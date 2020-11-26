package com.xiaobin.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 12:35
 * @desc 行情数据VO
 */
@Data
@ApiModel(value = "接收行情数据VO",description = "MarketDataVo")
public class MarketDataVo {

    @ApiModelProperty(value = "交易对",name = "symbol")
    private String symbol;
    @ApiModelProperty(value = "时间周期",name = "period")
    private String period;
    @ApiModelProperty(value = "size",name = "size")
    private Integer size;
}
