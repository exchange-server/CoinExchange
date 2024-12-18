package com.bizzan.bitrade.handler;

import com.bizzan.bitrade.entity.CoinThumb;
import com.bizzan.bitrade.entity.ExchangeTrade;
import com.bizzan.bitrade.entity.KLine;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoMarketHandler implements MarketHandler {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void handleTrade(String symbol, ExchangeTrade exchangeTrade, CoinThumb thumb) {
        mongoTemplate.insert(exchangeTrade, "exchange_trade_" + symbol);
    }

    @Override
    public void handleKLine(String symbol, KLine kLine) {
        mongoTemplate.insert(kLine, "exchange_kline_" + symbol + "_" + kLine.getPeriod());
    }
}
