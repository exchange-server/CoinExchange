package com.bizzan.bitrade.vendor.provider.support;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sulinxin
 */
@Slf4j
@AllArgsConstructor
public class FeigeSMSProvider implements SMSProvider {


    private String sign;
    private String username;
    private String password;
    /**
     * 发送单条短信
     *
     * @param mobile  手机号
     * @param content 短信内容
     * @return
     * @throws Exception
     */
    @Override
    public MessageResult sendSingleMessage(String mobile, String content) throws Exception {

            CloseableHttpClient client = null;
            CloseableHttpResponse response = null;
            try {
                List<BasicNameValuePair> formparams = new ArrayList<>();
                formparams.add(new BasicNameValuePair("Account",username));
                formparams.add(new BasicNameValuePair("Pwd",password));
                formparams.add(new BasicNameValuePair("Content",content));
                formparams.add(new BasicNameValuePair("Mobile",mobile));
                formparams.add(new BasicNameValuePair("SignId","ǩ��id"));

                HttpPost httpPost = new HttpPost("http://api.feige.ee/SmsService/Send");
                httpPost.setEntity(new UrlEncodedFormEntity(formparams,"UTF-8"));
                client = HttpClients.createDefault();
                response = client.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
                System.out.println(result);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        return null;
    }

    /**
     * 发送单条短信
     *
     * @param mobile     手机号
     * @param content    短信内容
     * @param templateId
     * @return
     * @throws Exception
     */
    @Override
    public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
        return null;
    }

    /**
     * 发送自定义短信
     *
     * @param mobile
     * @param content
     * @return
     * @throws Exception
     */
    @Override
    public MessageResult sendCustomMessage(String mobile, String content) throws Exception {
        return null;
    }

    /**
     * 发送验证码短信
     *
     * @param mobile     手机号
     * @param verifyCode 验证码
     * @return
     * @throws Exception
     */
    @Override
    public MessageResult sendVerifyMessage(String mobile, String verifyCode) throws Exception {
        String content = formatVerifyCode(verifyCode);
        return sendSingleMessage(mobile, content);
    }

    /**
     * 获取验证码信息格式
     *
     * @param code
     * @return
     */
    @Override
    public String formatVerifyCode(String code) {
        return String.format("【%s】验证码：%s，10分钟内有效，请勿告诉他人。", sign, code);
    }

    /**
     * 发送国际短信
     *
     * @param content
     * @param phone
     * @return
     */
    @Override
    public MessageResult sendInternationalMessage(String content, String phone) throws IOException, DocumentException {
        CloseableHttpClient client = null;
        CloseableHttpResponse response = null;
        List<BasicNameValuePair> formparams = new ArrayList<>();
        formparams.add(new BasicNameValuePair("Account",username));
        formparams.add(new BasicNameValuePair("Pwd",password));
        formparams.add(new BasicNameValuePair("Content",content));
        formparams.add(new BasicNameValuePair("Mobile",phone));
        formparams.add(new BasicNameValuePair("SignId","50328"));
        formparams.add(new BasicNameValuePair("TemplateId","55715"));

        HttpPost httpPost = new HttpPost("http://api.feige.ee/SmsService/Inter");
        httpPost.setEntity(new UrlEncodedFormEntity(formparams,"UTF-8"));
        client = HttpClients.createDefault();
        response = client.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject jsonObject = JSONObject.parseObject(result);
        log.info("phone:{},code:{}",phone,jsonObject);
        Integer code = jsonObject.getInteger("Code");
        String message = jsonObject.getString("Message");
        MessageResult messageResult = MessageResult.success();
        if (code != 0) {
            messageResult.setCode(500);
        }
        messageResult.setMessage(message);
        return messageResult;
    }


}
