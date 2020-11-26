package com.xiaobin.http;

import cn.hutool.json.JSONUtil;
import com.xiaobin.http.utils.ApiServiceGenerator;
import lombok.Data;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;

import java.io.IOException;

/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 0:03
 * @desc retrofit http请求
 */
public class RetrofitDemo  {


    public static void main(String[] args) throws IOException {
        DemoService demoService = ApiServiceGenerator.generatorService(DemoService.class);
        Call<Result> responseBodyCall = demoService.listRepos();
        Response<Result> execute = responseBodyCall.execute();

        if(execute.isSuccessful()){
            Result body = execute.body();
            System.out.println(JSONUtil.toJsonStr(body));
        }
    }


}


/**
 * @author xiaobin qq:944484545
 * @date 2020/7/12 0:18
 * @desc
 */
interface DemoService {
    @GET("/shakespeare/v2/notes/68c735e29f1b/book")
    Call<Result> listRepos();
}

@Data
class Result {
    private Integer notebook_id;
    private String notebook_name;
    private Boolean liked_by_user;
}

