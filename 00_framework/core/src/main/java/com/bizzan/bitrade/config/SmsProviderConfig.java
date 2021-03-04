package com.bizzan.bitrade.config;

import com.bizzan.bitrade.vendor.provider.support.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bizzan.bitrade.vendor.provider.SMSProvider;

@Configuration
public class SmsProviderConfig {

    @Value("${sms.gateway:}")
    private String gateway;
    @Value("${sms.username:}")
    private String username;
    @Value("${sms.password:}")
    private String password;
    @Value("${sms.sign:}")
    private String sign;
    @Value("${sms.internationalGateway:}")
    private String internationalGateway;
    @Value("${sms.internationalUsername:}")
    private String internationalUsername;
    @Value("${sms.internationalPassword:}")
    private String internationalPassword;
    @Value("${access.key.id:}")
    private String accessKey;
    @Value("${access.key.secret:}")
    private String accessSecret;

    //阿里云短信调用
    @Value("${aliyun.mail-sms.region}")
    private String ali_Region;
    @Value("${aliyun.mail-sms.access-key-id}")
    private String ali_accessKeyId;
    @Value("${aliyun.mail-sms.access-secret}")
    private String ali_accessSecret;
    @Value("${aliyun.mail-sms.sms-sign}")
    private String ali_smsSign;
    @Value("${aliyun.mail-sms.sms-template}")
    private String ali_smsTemplate;

    @Bean
    public SMSProvider getSMSProvider(@Value("${sms.driver:}") String driverName) {
    	//赛邮
        return new SaiyouSMSProvider(username, password, sign, gateway);

        //飞鸽
        //return new FeigeSMSProvider(sign,username,password);

        //阿里云短信调用
        //return new AliyunSMSprovider(ali_Region, ali_accessKeyId, ali_accessSecret, ali_smsSign, ali_smsTemplate);

        //第一短信
//    	return new DiyiSMSProvider(username, password, sign);
//        return new ChuangRuiSMSProvider(gateway, username, password, sign,accessKey,accessSecret);
//        if (StringUtils.isEmpty(driverName)) {
//            return new ChuangRuiSMSProvider(gateway, username, password, sign,accessKey,accessSecret);
//        }
//        if (driverName.equalsIgnoreCase(ChuangRuiSMSProvider.getName())) {
//            return new ChuangRuiSMSProvider(gateway, username, password, sign,accessKey,accessSecret);
//        } else if (driverName.equalsIgnoreCase(EmaySMSProvider.getName())) {
//            return new EmaySMSProvider(gateway, username, password);
//        }else if (driverName.equalsIgnoreCase(HuaXinSMSProvider.getName())) {
//            return new HuaXinSMSProvider(gateway, username, password,internationalGateway,internationalUsername,internationalPassword,sign);
//        }  else {
//            return null;
//        }
    }
}
