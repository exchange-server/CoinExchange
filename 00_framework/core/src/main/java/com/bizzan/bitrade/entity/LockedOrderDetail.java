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
public class LockedOrderDetail {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    
    @NotNull
    private Long memberId;
    
    /**
     * 锁仓ID
     */
    private Long lockedOrderId;
    
    /**
     * 锁仓产出
     */
    @Column(columnDefinition = "decimal(18,8) comment '矿机当期产出'")
    private BigDecimal output = BigDecimal.ZERO;

    /**
     * 锁仓产出币种
     */
    private String releaseUnit;
    
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
