package com.bizzan.bitrade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @Description: 持币分红表
 * @author: GuoShuai
 * @date: create in 16:13 2019/6/30
 * @Modified:
 */
@Entity
@Data
@Table(name = "member_bonus")
public class MemberBonusDTO {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    /**
     * 用户ID
     */
    private Long memberId;

    /**
     * 持币时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String haveTime;

    /**
     * 到账时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String arriveTime;

    /**
     * 当天总手续费
     */
    @Column(columnDefinition = "decimal(18,8) comment '当天总手续费'")
    private BigDecimal total;

    /**
     * 分红数量
     */
    @Column(columnDefinition = "decimal(18,8) comment '分红数量'")
    private BigDecimal memBouns;

    /**
     * 币种ID
     */
    private String coinId;

}
