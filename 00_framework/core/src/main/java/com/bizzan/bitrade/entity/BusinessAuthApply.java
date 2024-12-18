package com.bizzan.bitrade.entity;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商家认证申请信息
 *
 * @author Shaoxianjun
 * @date 2019/5/5
 */
@Entity
@Data
@Table
@ToString
public class BusinessAuthApply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    /**
     * 认证商家状态
     */
    @Enumerated(EnumType.ORDINAL)
    private CertifiedBusinessStatus certifiedBusinessStatus;

    /**
     * 认证失败的原因
     */
    private String detail;
    /**
     * 申请时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 审核时间
     */
    private Date auditingTime;

    @Column(columnDefinition = "TEXT")
    private String authInfo;

    @ManyToOne
    @JoinColumn(name = "business_auth_deposit_id")
    private BusinessAuthDeposit businessAuthDeposit;

    private String depositRecordId;

    /**
     * 保证金数额
     */
    private BigDecimal amount;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Transient
    private JSONObject info;
}
