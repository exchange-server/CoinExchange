package com.bizzan.bitrade.entity;

import lombok.Data;

import javax.persistence.Id;

@Data
public class CountryVo {
    /**
     * 中文名称
     */
    @Id
    private String zhName;

    /**
     * 英文名称
     */
    private String enName;

    /**
     * 区号
     */
    private String areaCode;
    /**
     * 语言
     */
    private String language;

    /**
     * 当地货币缩写
     */
    private String localCurrency;
    /***
     * 多语言翻译
     */
    private String translation;

    private int sort;
}
