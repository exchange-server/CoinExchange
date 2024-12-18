package com.bizzan.bitrade.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Jammy
 * @date 2020年03月07日
 */
@Data
@Builder
public class TransferAddressInfo {
    private List<Map<String, Object>> info;
    private BigDecimal balance;//可用余额
}
