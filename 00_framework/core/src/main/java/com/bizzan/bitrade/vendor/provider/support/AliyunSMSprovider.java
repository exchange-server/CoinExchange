package com.bizzan.bitrade.vendor.provider.support;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.bizzan.bitrade.dto.SmsDTO;
import com.bizzan.bitrade.service.SmsService;
import com.bizzan.bitrade.util.HttpSend;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.FormSubmitEvent;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AliyunSMSprovider implements SMSProvider {
    private String ali_region; // Region
    private String ali_accessKeyId; // accessKeyId
    private String ali_accessSecret; // accessSecret
    private String ali_smsSign; // smsSign
    private String ali_smsTemplate; // smsTemplate

    @Autowired
    private SmsService smsService;

    public AliyunSMSprovider(String ali_region, String ali_accessKeyId, String ali_accessSecret, String ali_smsSign, String ali_smsTemplate) {
        this.ali_region = ali_region;
        this.ali_accessKeyId = ali_accessKeyId;
        this.ali_accessSecret = ali_accessSecret;
        this.ali_smsSign = ali_smsSign;
        this.ali_smsTemplate = ali_smsTemplate;
    }

    public static String getName() {
        return "aliyun";
    }

    @Override
    public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
        SmsDTO smsDTO = smsService.getByStatus();
        if("aliyun".equals(smsDTO.getSmsName())){
            return sendMessage(mobile,content,smsDTO);
        }
        return null;
    }

    @Override
    public MessageResult sendMessageByTempId(String mobile, String content, String templateId) throws Exception {
        return null;
    }

    public MessageResult sendMessage(String mobile, String content,SmsDTO smsDTO) throws Exception{
        DefaultProfile profile = DefaultProfile.getProfile(ali_region, ali_accessKeyId, ali_accessSecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", ali_region);
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", ali_smsSign);
        request.putQueryParameter("TemplateCode", ali_smsTemplate);

        JSONObject templateParasm = new JSONObject();
        templateParasm.put("code", content);
        request.putQueryParameter("TemplateParam", templateParasm.toJSONString());


        log.info("阿里云短信=={},{}", mobile, templateParasm.toJSONString());

        try {
            CommonResponse response = client.getCommonResponse(request);
            String returnStr = response.getData();
            return parseResult(returnStr);
        } catch (ClientException e) {

            MessageResult mr = new MessageResult(500, "系统错误");
            e.printStackTrace();
            return mr;
        }
    }

    private MessageResult parseResult(String result) {
        // 返回参数形式：
//        {
//                "Message":"OK",
//                "RequestId":"2184201F-BFB3-446B-B1F2-C746B7BF0657",
//                "BizId":"197703245997295588^0",
//                "Code":"OK"
//        }
        JSONObject jsonObject = JSONObject.parseObject(result);

        MessageResult mr = new MessageResult(500, "系统错误");
        if(jsonObject.getString("Message").equals("OK")) {
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
        log.info("阿里云短信不支持自定义文本");
        MessageResult mr = new MessageResult(500, "阿里云短信不支持自定义文本");
        return mr;
    }
}
