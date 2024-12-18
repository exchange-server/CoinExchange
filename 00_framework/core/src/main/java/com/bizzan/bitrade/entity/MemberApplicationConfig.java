package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.BooleanEnum;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "member_application_config")
public class MemberApplicationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 0/1(关闭/开启)
     */
    private BooleanEnum withdrawCoinOn = BooleanEnum.IS_TRUE;

    private BooleanEnum rechargeCoinOn = BooleanEnum.IS_TRUE;

    private BooleanEnum promotionOn = BooleanEnum.IS_TRUE;

    private BooleanEnum transactionOn = BooleanEnum.IS_TRUE;

}
