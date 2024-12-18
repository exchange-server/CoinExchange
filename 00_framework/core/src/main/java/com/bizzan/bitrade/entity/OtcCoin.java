package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CommonStatus;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * OTC币种
 *
 * @author Jammy
 * @date 2020年01月09日
 */
@Entity
@Data
public class OtcCoin {
    @Excel(name = "otc货币编号", orderNum = "1", width = 20)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Excel(name = "otc货币名称", orderNum = "1", width = 20)
    @NotBlank(message = "币名称不得为空")
    private String name;

    /**
     * 中文
     */
    @Excel(name = "otc货币单位中文名称", orderNum = "1", width = 20)
    @NotBlank(message = "中文名称不得为空")
    private String nameCn;

    /**
     * 缩写
     */
    @Excel(name = "otc货币单位", orderNum = "1", width = 20)
    @NotBlank(message = "单位不得为空")
    private String unit;

    /**
     * 状态
     */
    @Enumerated(EnumType.ORDINAL)
    private CommonStatus status = CommonStatus.NORMAL;

    /**
     * 交易手续费率
     */
    @Column(columnDefinition = "decimal(18,6) comment '交易手续费率'")
    private BigDecimal jyRate;

    @Column(columnDefinition = "decimal(18,8) comment '卖出广告最低发布数量'")
    private BigDecimal sellMinAmount;

    @Column(columnDefinition = "decimal(18,8) comment '买入广告最低发布数量'")
    private BigDecimal buyMinAmount;

    @Excel(name = "otc货币单位", orderNum = "1", width = 20)
    private int sort;

    /**
     * 是否是平台币
     */
    @Enumerated(EnumType.ORDINAL)
    private BooleanEnum isPlatformCoin = BooleanEnum.IS_FALSE;
}
