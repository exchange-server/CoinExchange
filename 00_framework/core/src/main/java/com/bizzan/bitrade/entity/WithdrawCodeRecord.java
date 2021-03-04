package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.AuditStatus;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.security.auth.message.AuthStatus;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 提币申请(提币码)
 *
 * @author Jammy
 * @date 2020年01月29日
 */
@Entity
@Data
public class WithdrawCodeRecord {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 申请人id
     */
    private Long memberId;
    
    /**
     * 币种
     */
    @JoinColumn(name = "coin_id", nullable = false)
    @ManyToOne
    private Coin coin;
    
    /**
     * 提现总数量
     */
    @Column(columnDefinition = "decimal(18,8) comment '申请总数量'")
    private BigDecimal withdrawAmount;
    
    /**
     * 手续费
     */
    // @Column(columnDefinition = "decimal(18,8) comment '手续费'")
    // private BigDecimal fee;
    
    /**
     * 预计到账数量
     */
    // @Column(columnDefinition = "decimal(18,8) comment '预计到账数量'")
    // private BigDecimal arrivedAmount;
    
    /**
     * 申请提现时间
     */
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 充值时间(提现码被人充值)
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealTime;
    
    /**
     * 提现状态
     */
    @Enumerated(EnumType.ORDINAL)
    private WithdrawStatus status = WithdrawStatus.PROCESSING;

    /**
     * 提现码
     */
    private String withdrawCode;
    
    /**
     * 充值人id(recharge member id)
     */
    private Long rmemberId;

    private String remark;
}
