package com.xiaobin.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author xiaobin qq:944484545
 * @date 2020/4/6 12:57
 * @desc controller响应实体类
 */

@JsonSerialize(include =  JsonSerialize.Inclusion.NON_NULL)
@Getter
@ApiModel(value = "ResponseServer",description = "controller响应实体类")
public class ResponseServer<T> implements Serializable {
    /**
     *  200:成功响应
     *  500:错误响应
     */
    @ApiModelProperty(name = "code",value = "响应code")
    private Integer code;
    @ApiModelProperty(name = "message",value = "响应信息")
    private String message;
    @ApiModelProperty(name = "data",value = "响应数据")
    private T data;

    private ResponseServer(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    private ResponseServer(Integer code,T data){
        this.code = code;
        this.data = data;
    }

    private ResponseServer(Integer code,String message,T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public static <T> ResponseServer<T> ok(){
        return new ResponseServer<T>(200,"success");
    }
    public static <T> ResponseServer<T> ok(String message){
        return new ResponseServer<T>(200,message);
    }

    public static <T> ResponseServer<T> ok(T data){
        return new ResponseServer<T>(200,data);
    }

    public static <T> ResponseServer<T> ok(String message,T data){
        return new ResponseServer<T>(200,message,data);
    }

    public static <T> ResponseServer<T> error(){
        return new ResponseServer<T>(500,"error");
    }
    public static <T> ResponseServer<T> error(String message){
        return new ResponseServer<T>(500,message);
    }
    public static <T> ResponseServer<T> error(String message,T data){
        return new ResponseServer<T>(500,message,data);
    }

}