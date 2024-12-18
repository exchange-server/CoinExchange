package com.bizzan.bitrade.model.screen;

import com.bizzan.bitrade.constant.AdvertiseType;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.OrderStatus;
import lombok.Data;

@Data
public class AppealScreen {
    private AdvertiseType advertiseType;
    private String complainant;//申诉者
    private String negotiant;//交易者
    private BooleanEnum success;
    private String unit;
    private OrderStatus status;
    private Boolean auditing = false;
}
