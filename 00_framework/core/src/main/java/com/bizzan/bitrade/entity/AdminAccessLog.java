package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.AdminModule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * 后台用户访问操作日志
 *
 * @author Jammy
 * @date 2020年12月19日
 */
@Entity
@Data
@Table
public class AdminAccessLog {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private Long adminId;

    @Transient
    private String adminName;

    private String uri;

    @Enumerated(EnumType.ORDINAL)
    private AdminModule module;

    private String operation;

    private String accessIp;

    private String accessMethod;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date accessTime;

}
