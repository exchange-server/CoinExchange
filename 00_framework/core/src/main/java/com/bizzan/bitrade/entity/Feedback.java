package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * 意见反馈
 *
 * @author Jammy
 * @date 2020年03月19日
 */
@Entity
@Data
public class Feedback {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String remark;
    /**
     * 反馈者
     */
    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne
    private Member member;
}
