package com.xiaobin.http.entity;

import lombok.Data;

import java.util.List;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 12:48
 * @desc
 */
@Data
public class MarketResponseData<T> {

    private String ch;
    private String status;
    private Long ts;
    private List<T> data;
}
