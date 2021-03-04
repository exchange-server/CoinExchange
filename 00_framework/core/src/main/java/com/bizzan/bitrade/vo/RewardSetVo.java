package com.bizzan.bitrade.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RewardSetVo implements Serializable {
    private String name;
    private String realName;
    private String id;
    private String rate;
    private boolean canUpdate;
    private List<RewardSetVo> children;
    private ItemStyle itemStyle;

    public void setCanUpdate(boolean canUpdate){
        this.canUpdate=canUpdate;
        if(canUpdate){
            ItemStyle itemStyle = new ItemStyle();
            itemStyle.setBorderColor("rgba(49, 205, 34, 1)");
            this.itemStyle=itemStyle;
        }else {
            ItemStyle itemStyle = new ItemStyle();
            this.itemStyle=itemStyle;
        }
    }
}
