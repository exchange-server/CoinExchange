package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.entity.Alipay;
import com.bizzan.bitrade.entity.BankInfo;
import com.bizzan.bitrade.entity.WechatPay;

import lombok.Builder;
import lombok.Data;

/**
 * @author Jammy
 * @date 2020年01月16日
 */
@Builder
@Data
public class MemberAccount {
    private String realName;
    private BooleanEnum bankVerified;
    private BooleanEnum aliVerified;
    private BooleanEnum wechatVerified;
    private BankInfo bankInfo;
    private Alipay alipay;
    private WechatPay wechatPay;
}
