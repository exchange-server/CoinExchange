package com.bizzan.bitrade.entity;

import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

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
import java.util.Date;

@Data
@Entity
@Table(name = "bussiness_cancel_apply")
@ToString
public class BusinessCancelApply {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @Enumerated(value = EnumType.ORDINAL)
    private CertifiedBusinessStatus status;
    private String depositRecordId;
    @CreationTimestamp
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date cancelApplyTime;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;
    /**
     * 退保原因
     */
    private String reason;
    /**
     * 审核失败理由
     */
    private String detail;
    @Transient
    private DepositRecord depositRecord;

    public BusinessCancelApply() {
    }
}
