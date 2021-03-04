package com.bizzan.bitrade.constant;

import com.bizzan.bitrade.core.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum ContractRewardRecordType implements BaseEnum {
    /**
     * 推广
     */
    OPEN("开仓返佣"),
    /**
     * 活动
     */
    CLOSE("平仓返佣"),
    LEVEL( "平级奖励"),
    PLATFORM( "平台手续费收入");
    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }

}
