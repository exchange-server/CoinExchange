package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @description: MemberApiKey
 * @author QQ:247818019 E-mail:247818019@qq.com
 * @create: 2019/05/07 10:36
 */
@Entity
@Data
public class MemberApiKey {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id ;

    private Long memberId ;

    private String apiKey ;

    private String secretKey ;
    /**
     * 绑定ip 多个以逗号分割
     */
    private String bindIp ;

    private String apiName ;

    private String remark ;

    private Date expireTime;

    private Date createTime;
    @Transient
    private String code ;
    public MemberApiKey() {
    }

    public MemberApiKey(Long memberId, String apiKey, String secretKey, String bindIp, String apiName, String remark,
                        Date expireTime, Long id, Date createTime) {
        this.id = id;
        this.memberId = memberId;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.bindIp = bindIp;
        this.apiName = apiName;
        this.remark = remark;
        this.expireTime = expireTime;
        this.createTime = createTime;
    }

    public MemberApiKey(Long memberId, String apiKey, String bindIp, String apiName, String remark, Date expireTime,
                        Long id, Date createTime) {
        this.id = id;
        this.memberId = memberId;
        this.apiKey = apiKey;
        this.bindIp = bindIp;
        this.apiName = apiName;
        this.remark = remark;
        this.expireTime = expireTime;
        this.createTime = createTime;
    }

    public MemberApiKey(Long memberId, String bindIp, String apiName, String remark, Date createTime) {
        this.memberId = memberId;
        this.bindIp = bindIp;
        this.apiName = apiName;
        this.remark = remark;
        this.createTime = createTime;
    }
}
