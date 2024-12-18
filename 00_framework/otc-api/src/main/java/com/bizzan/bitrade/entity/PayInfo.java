package com.bizzan.bitrade.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author Jammy
 * @date 2020年01月20日
 */
@Builder
@Data
public class PayInfo {
    private String realName;
    private Alipay alipay;
    private WechatPay wechatPay;
    private BankInfo bankInfo;
}
