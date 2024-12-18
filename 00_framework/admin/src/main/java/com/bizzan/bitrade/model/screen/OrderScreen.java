package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.constant.AdvertiseType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderScreen extends OtcOrderTopScreen {
    private String orderSn;
    private BigDecimal minNumber;
    private BigDecimal maxNumber;
    private String memberName;//用户名和真名的关键字即可
    private String customerName;//用户名和真名的关键字即可
    private BigDecimal minMoney;
    private BigDecimal maxMoney;
    private AdvertiseType advertiseType;
}
