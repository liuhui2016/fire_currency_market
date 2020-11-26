package com.xiaobin.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 12:35
 * @desc 行情数据VO
 */
@Data
@ApiModel(value = "接收市场深度数据VO",description = "MarketDepthDataVo")
public class MarketDepthDataVo {

    @ApiModelProperty(value = "交易对",name = "symbol")
    private String symbol;
    @ApiModelProperty(value = "深度的数量",name = "depth")
    private Integer depth;
    @ApiModelProperty(value = "深度的聚合度",name = "type")
    private String type;
}
