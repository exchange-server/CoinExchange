package com.bizzan.bitrade;

import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.service.MarketService;
import org.junit.runner.RunWith;

import javax.annotation.Resource;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MarketApplication.class)
public class MarketAppTest {
    @Resource
    private MarketService marketService;
    @Resource
    private ExchangeOrderService exchangeOrderService;

//    @Test
//    public void contextLoads() throws Exception {
//        String json = "{\"amount\":1,\"buyOrderId\":\"E152111410306554\",\"direction\":\"SELL\",\"price\":29.3,\"sellOrderId\":\"E152111428290792\",\"time\":1521114282916}";
//        ExchangeTrade trade = JSON.parseObject(json, ExchangeTrade.class);
//        System.out.println(trade.getBuyOrderId());
//        exchangeOrderService.processExchangeTrade(trade,false);
//    }

}
