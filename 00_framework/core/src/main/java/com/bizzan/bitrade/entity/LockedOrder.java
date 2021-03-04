package com.bizzan.bitrade.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Entity
@Data
public class LockedOrder {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 活动图片
     */
    private String image;
    
    /**
     * 活动名称
     */
    private String title;
    
    /**
     * 领取人ID
     */
    @NotNull
    private Long memberId;
    
    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 锁仓释放周期（0：日，1：周，2：月，3：年
     */
    private int period = 0;
    
    /**
     * 锁仓状态（0：待释放，1：释放中，2：已结束）
     */
    private int lockedStatus = 0;
    
    /**
     * 持续周期（此处变量名称Days并非一定是天，也可能是周、月、年）
     */
    private int lockedDays;

    /**
     * 已释放周期（此处变量名称Days并非一定是天，也可能是周、月、年）
     */
    private int releasedDays;
    
    /**
     * 释放币种
     */
    private String releaseUnit;
    
    /**
     * 总锁仓
     */
    @Column(columnDefinition = "decimal(18,8) comment '总锁仓'")
    private BigDecimal totalLocked = BigDecimal.ZERO;
    
    /**
     * 锁仓释放：释放类型（0：等额释放，1：等比释放）
     */
    private int releaseType = 0;
    
    /**
     * 锁仓释放：原始释放比例
     */
    private BigDecimal releasePercent = BigDecimal.ZERO;
    
    /**
     * 锁仓释放：当前释放比例
     */
    private BigDecimal releaseCurrentpercent = BigDecimal.ZERO;
    
    /**
     * 锁仓释放：释放倍数
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal releaseTimes = BigDecimal.ZERO;
    
    /**
     * 原始周期释放总量
     */
    @Column(columnDefinition = "decimal(18,8) comment '原始周期释放数量'")
    private BigDecimal originReleaseamount = BigDecimal.ZERO;
    
    /**
     * 当前周期释放（因邀请好友购买锁仓会增加产出）
     */
    @Column(columnDefinition = "decimal(18,8) comment '当前周期释放数量'")
    private BigDecimal currentReleaseamount = BigDecimal.ZERO;
    
    /**
     * 总释放（已经释放量）
     */
    @Column(columnDefinition = "decimal(18,8) comment '总释放'")
    private BigDecimal totalRelease = BigDecimal.ZERO;


    /**
     * 邀请好友（且购买锁仓）产能增加百分比（为0则不增加）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal lockedInvite = BigDecimal.ZERO;
    
    /**
     * 产能增加上限百分比（为0则无上限）
     */
    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal lockedInvitelimit = BigDecimal.ZERO;
    
    /**
     * 结束日期
     */
    @Column(columnDefinition = "varchar(30) default '2000-01-01 01:00:00'  comment '结束时间'")
    @NotNull
    private Date endTime;
    
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
