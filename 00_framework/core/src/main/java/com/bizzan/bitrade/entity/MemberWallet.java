package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import java.math.BigDecimal;

/**
 * @author Jammy
 * @description 会员钱包
 * @date 2019/1/2 15:28
 */
@Entity
@Data
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"memberId", "coin_id"})})
public class MemberWallet {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;
    /**
     * 可用余额
     */
    @Column(columnDefinition = "decimal(26,16) comment '可用余额'")
    private BigDecimal balance;
    /**
     * 冻结余额
     */
    @Column(columnDefinition = "decimal(26,16) comment '冻结余额'")
    private BigDecimal frozenBalance;

    /**
     * 待释放总量
     */
    @Column(columnDefinition = "decimal(18,8) comment '待释放总量'")
    private BigDecimal toReleased = BigDecimal.ZERO;

    /**
     * 充值地址
     */
    private String address;


    @JsonIgnore
    @Version
    private int version;

    /**
     * 钱包是否锁定，0否，1是。锁定后
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "int default 0 comment '钱包不是锁定'")
    private BooleanEnum isLock = BooleanEnum.IS_FALSE;

    /**
     * EOS、XRP等币种需要填写Memo时生成
     */
    @Transient
    private String memo;
}
