package com.bizzan.bitrade.vendor.provider.support;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.dto.SmsDTO;
import com.bizzan.bitrade.moduyun.SmsSingleSender;
import com.bizzan.bitrade.moduyun.SmsSingleSenderResult;
import com.bizzan.bitrade.service.SmsService;
import com.bizzan.bitrade.util.HttpSend;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SaiyouSMSProvider implements SMSProvider {
    private String username; // appID
    private String password; // signature
    private String sign; // sign
    private String gateway; // gateway

    @Autowired
    private SmsService smsService;

    public SaiyouSMSProvider(String username, String password, String sign, String gateway) {
        this.username = username;
        this.password = password;
        this.sign = sign;
        this.gateway = gateway;
    }

    public static String getName() {
        return "saiyou";
    }

    @Override
    public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
        SmsDTO smsDTO = smsService.getByStatus();
        if("saiyou".equals(smsDTO.getSmsName())){
            return sendMessage(mobile,content,smsDTO);
        }
        return null;
    }

    @Override
    public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
        return null;
    }

    public MessageResult sendMessage(String mobile, String content,SmsDTO smsDTO) throws Exception{

//        Map<String, String> params = new HashMap<String, String>();
//        params.put("appid", username);
//        params.put("to", mobile);
//        String smsContent = "【" + this.sign + "】您的验证码为" + content + "，十分钟内有效，如非本人操作，请忽略。";
//        params.put("content", smsContent);
//        params.put("signature", password);
//        log.info("云邮短信====", params.toString());
//        String returnStr= HttpSend.post(gateway, params);
//        log.info("result = {}", returnStr);
//        return parseResult(returnStr);
        SmsSingleSender singleSender = new SmsSingleSender(username, password);
        SmsSingleSenderResult singleSenderResult;
        String smsContent = "【" + this.sign + "】您的验证码为" + content + "，十分钟内有效，如非本人操作，请忽略。";
        singleSenderResult = singleSender.send(0, "86", mobile, smsContent, "", "");

        MessageResult mr = new MessageResult(500, "系统错误");
        if(singleSenderResult.result == 0) {
            mr.setCode(0);
            mr.setMessage("短信发送成功！");
        }else{
            mr.setCode(1);
            mr.setMessage("短信发送失败，请联系平台处理！");
        }
        return mr;
    }

    private MessageResult parseResult(String result) {
        // 返回参数形式：
        // {
        //    "status":"success"
        //    "send_id":"093c0a7df143c087d6cba9cdf0cf3738"
        //    "fee":1,
        //    "sms_credits":14197
        //}
        JSONObject jsonObject = JSONObject.parseObject(result);

        MessageResult mr = new MessageResult(500, "系统错误");
        if(jsonObject.getString("status").equals("success")) {
            mr.setCode(0);
            mr.setMessage("短信发送成功！");
        }else{
            mr.setCode(1);
            mr.setMessage("短信发送失败，请联系平台处理！");
        }
        return mr;
    }

    /**
     * 转换返回值类型为UTF-8格式.
     * @param is
     * @return
     */
    public String convertStreamToString(InputStream is) {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size = 0;

        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb1.toString();
    }

    @Override
    public MessageResult sendCustomMessage(String mobile, String content) throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", username);
        params.put("to", mobile);
        String smsContent = "【" + this.sign + "】" + content;
        params.put("content", smsContent);
        params.put("signature", password);
        log.info("云邮短信====", params.toString());
        String returnStr= HttpSend.post(gateway, params);
        log.info("result = {}", returnStr);
        return parseResult(returnStr);
    }
}
