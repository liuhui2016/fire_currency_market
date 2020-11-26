package com.xiaobin.constant;

import cn.hutool.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

/**
 *  ok常量
 */
public interface OkQuotationConstant {

    public static final String passphrase = "Qq944484545";
    public static final String apikey = "39ac74f8-1bf3-461d-9d56-0f994d259e66";
    public static final String secretKey = "52EA7EAE8B08C914DEF729950BBA4485";

    static String loginOkWs(){
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000 + 28800);
        String str = timeStamp + "GET/users/self/verify";
        String hash = "";
        try {
            Mac sha256_HMAC =  Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            hash = Base64.encodeBase64String(sha256_HMAC.doFinal(str.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("op","login");
        jsonObject.put("args", Arrays.asList(apikey,passphrase,timeStamp,hash));
        return jsonObject.toString();
    }
}
