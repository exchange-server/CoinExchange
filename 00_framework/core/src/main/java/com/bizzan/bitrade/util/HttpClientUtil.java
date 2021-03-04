package com.bizzan.bitrade.util;


import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author zhang yingxin
 * @date 2019/4/18
 */
@Slf4j
public class HttpClientUtil {
    public static String post(JSONObject jsonObject,String url,String tokenKey,String tokenValue) throws IOException {
        HttpPost httpPost=new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader(tokenKey,tokenValue);
        StringEntity entity = new StringEntity(jsonObject.toString(), Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");
        // 发送Json格式的数据请求
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        HttpClient httpClient=new DefaultHttpClient();
        HttpResponse response = httpClient.execute(httpPost);
        String result="";
        if(response != null){
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null){
                result= EntityUtils.toString(resEntity,"utf-8");
            }
        }
        return result;
    }

    public static String get(String url,String tokenKey,String tokenValue){
        log.info("apiUrl:"+url);
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
            if(tokenKey!=null&&tokenValue!=null){
                httpGet.setHeader(tokenKey,tokenValue);
            }
            HttpResponse response = httpClient.execute(httpGet);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result= EntityUtils.toString(resEntity,"utf-8");
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }
}

