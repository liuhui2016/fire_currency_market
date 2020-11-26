package com.xiaobin.http.utils;

import com.xiaobin.constant.HuobiQuotationConstant;
import com.xiaobin.exception.HuobiHttpRequestException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 0:15
 * @desc 生成retrofit对象
 */
public class ApiServiceGenerator {

    public static <T> T generatorService(Class<T> clazz){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HuobiQuotationConstant.HTTP_BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    public static <T> T getCallResponseBody(Call<T> call){
        try {
            Response<T> execute = call.execute();
            if(execute.isSuccessful()){
                return execute.body();
            }else{
                // 抛出异常
                throw new HuobiHttpRequestException("request error url ===>>> "+call.request().url().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
