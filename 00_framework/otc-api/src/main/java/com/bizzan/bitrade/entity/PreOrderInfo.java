package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.constant.BooleanEnum;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Jammy
 * @date 2020年01月16日
 */
@Builder
@Data
public class PreOrderInfo {
    private String username;
    private BooleanEnum emailVerified;
    private BooleanEnum phoneVerified;
    private BooleanEnum idCardVerified;
    private int transactions;
    private long otcCoinId;
    private String unit;
    private BigDecimal price;
    private BigDecimal number;
    private String payMode;
    private BigDecimal minLimit;
    private BigDecimal maxLimit;
    private int timeLimit;
    private String country;
    private AdvertiseType advertiseType;
    private String remark;

    //最大可交易数量
    private BigDecimal maxTradableAmount;
}
