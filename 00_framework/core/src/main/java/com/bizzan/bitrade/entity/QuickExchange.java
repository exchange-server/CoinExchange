package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
public class QuickExchange {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull
    private Long memberId; // 兑换人

    private String fromUnit; // 源币种

    private String toUnit; // 目标币种

    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal rate; // 兑换比例

    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal amount; // 源兑换数量

    @Column(columnDefinition = "decimal(24,8) DEFAULT 0 ")
    private BigDecimal exAmount; // 目标兑换数量

    // 状态（0：未成交， 1：已成交， 2：用户取消， 3：管理员撤回）
    private int status = 0;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
