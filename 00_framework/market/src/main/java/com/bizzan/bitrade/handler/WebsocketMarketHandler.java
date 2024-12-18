package com.bizzan.bitrade.handler;

import com.bizzan.bitrade.entity.CoinThumb;
import com.bizzan.bitrade.entity.ExchangeTrade;
import com.bizzan.bitrade.entity.KLine;
import com.bizzan.bitrade.job.ExchangePushJob;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebsocketMarketHandler implements MarketHandler {
    @Resource
    private SimpMessagingTemplate messagingTemplate;
    @Resource
    private ExchangePushJob pushJob;

    /**
     * 推送币种简化信息
     *
     * @param symbol
     * @param exchangeTrade
     * @param thumb
     */
    @Override
    public void handleTrade(String symbol, ExchangeTrade exchangeTrade, CoinThumb thumb) {
        //推送缩略行情
        pushJob.addThumb(symbol, thumb);
    }

    @Override
    public void handleKLine(String symbol, KLine kLine) {
        //推送K线数据
        messagingTemplate.convertAndSend("/topic/market/kline/" + symbol, kLine);
    }
}
