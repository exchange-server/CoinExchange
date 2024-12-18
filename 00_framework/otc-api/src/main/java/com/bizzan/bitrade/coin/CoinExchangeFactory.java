package com.bizzan.bitrade.coin;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;

@Slf4j
public class CoinExchangeFactory {
    @Setter
    private HashMap<String, BigDecimal> coins;

    public CoinExchangeFactory() {
        coins = new HashMap<>();
    }

    public HashMap<String, BigDecimal> getCoins() {
        return coins;
    }

    /**
     * 获取币种价格
     *
     * @param symbol
     * @return
     */
    public BigDecimal get(String symbol) {
        return coins.get(symbol);
    }

    public void set(String symbol, BigDecimal rate) {
        coins.put(symbol, rate);
    }
}
