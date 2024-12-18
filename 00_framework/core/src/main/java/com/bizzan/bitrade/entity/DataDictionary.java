package com.bizzan.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author Jammy
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1214:12
 */
@Data
@Entity
public class DataDictionary {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String bond;

    private String value;

    /**
     * 注释
     */
    private String comment;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationTime;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public DataDictionary() {
    }

    public DataDictionary(String bond, String value, String comment) {
        this.bond = bond;
        this.value = value;
        this.comment = comment;
    }
}
