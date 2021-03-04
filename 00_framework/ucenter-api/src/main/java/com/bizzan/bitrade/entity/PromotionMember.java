package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.PromotionLevel;
import com.bizzan.bitrade.constant.RealNameStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author Jammy
 * @date 2020年03月20日
 */
@Data
@Builder
public class PromotionMember {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String username;
    private PromotionLevel level;
    private RealNameStatus realNameStatus = RealNameStatus.NOT_CERTIFIED;
}
