package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 系统权限
 *
 * @author Jammy
 * @date 2020年12月18日
 */
@Entity
@Data
@Table(name = "admin_permission")
public class SysPermission {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotBlank(message = "权限名不能为空")
    @NotNull(message = "权限名不能为空")
    private String title;

    private String description;

    /**
     * 为0表示是菜单
     */
    private Long parentId = 0L;

    private Integer sort = 0;

    @NotBlank(message = "权限名不能为空")
    @NotNull(message = "权限名不能为空")
    private String name;

    @ManyToMany(cascade = CascadeType.PERSIST, mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SysRole> roles;
}
