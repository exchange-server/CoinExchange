package com.bizzan.bitrade.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.bizzan.bitrade.constant.AnnouncementClassification;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Jammy
 * @description 公告
 * @date 2019/3/5 14:59
 */
@Entity
@Data
@Table
public class Announcement {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @NotNull(message = "标题不能为空")
    private String title;

    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String content;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Excel(name = "分类", orderNum = "1", width = 20)
    @NotNull(message = "分类不能为空")
    private AnnouncementClassification announcementClassification;

    //是否显示
    private Boolean isShow;

    //语言
    private String lang;

    //是否置顶
    private String isTop;

    @Column(nullable = true)
    private String imgUrl;

    private int sort = 0;
}
