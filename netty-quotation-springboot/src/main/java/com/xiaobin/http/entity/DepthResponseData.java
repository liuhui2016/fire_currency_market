package com.xiaobin.http.entity;

import lombok.Data;

import java.util.List;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 12:48
 * @desc
 */
@Data
public class DepthResponseData {

    private String ch;
    private String status;
    private Long ts;
    private DepthData tick;
}


@Data
class DepthData{

    private Double[][] bids;
    private Double[][] asks;
    private Long version;
    private Long ts;

}