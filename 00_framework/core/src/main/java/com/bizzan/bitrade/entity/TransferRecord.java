package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jammy
 * @date 2020年02月27日
 */
@Entity
@Data
public class TransferRecord {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private BigDecimal amount;

    private Long memberId;
    @JoinColumn(name = "coin_id")
    @ManyToOne
    private Coin coin;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @Column(columnDefinition = "decimal(18,8) comment '手续费'")
    private BigDecimal fee;
    //接口返回的订单号
    private String orderSn;
    //转账地址
    private String address;
    private String remark;
}
