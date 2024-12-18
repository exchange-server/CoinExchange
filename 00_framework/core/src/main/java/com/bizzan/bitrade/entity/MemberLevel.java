package com.bizzan.bitrade.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Jammy
 * @description 会员等级实体
 * @date 2019/12/26 17:12
 */
@Data
@Entity
@Table(name = "member_level")
public class MemberLevel {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @NotBlank(message = "会员等级名称不得为空")
    private String name;
    @NotNull(message = "默认不得为null")
    private Boolean isDefault;
    private String remark;

}
