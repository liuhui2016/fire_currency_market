package com.xiaobin.controller;

import com.xiaobin.response.ResponseServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/6 12:39
 * @desc
 */
@Api(tags = "swagger测试",value = "测试控制类")
@RestController
public class SwaggerController {


    @ApiOperation(value = "swagger调用测试",notes = "swagger notes",response = ResponseServer.class,httpMethod = "GET")
    @GetMapping("/swagger")
    public ResponseServer swagger(){
        return ResponseServer.ok("swagger调用测试");
    }
}
