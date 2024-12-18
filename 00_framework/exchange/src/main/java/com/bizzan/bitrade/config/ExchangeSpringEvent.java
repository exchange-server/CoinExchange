package com.bizzan.bitrade.config;

import com.bizzan.bitrade.Trader.CoinTraderFactory;
import com.bizzan.bitrade.service.ExchangeOrderDetailService;
import com.bizzan.bitrade.service.ExchangeOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ExchangeSpringEvent implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    CoinTraderFactory coinTraderFactory;
    private Logger log = LoggerFactory.getLogger(ExchangeSpringEvent.class);
    @Resource
    private ExchangeOrderService exchangeOrderService;
    @Resource
    private ExchangeOrderDetailService exchangeOrderDetailService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("======程序启动成功======");
        //coinTraderFactory.getTraderMap();
        /*
        HashMap<String,CoinTrader> traders = coinTraderFactory.getTraderMap();
        traders.forEach((symbol,trader) ->{
            List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(symbol);
            List<ExchangeOrder> tradingOrders = new ArrayList<>();
            orders.forEach(order -> {
                BigDecimal tradedAmount = BigDecimal.ZERO;
                BigDecimal turnover = BigDecimal.ZERO;
                List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(order.getOrderId());
                order.setDetail(details);
                for(ExchangeOrderDetail trade:details){
                    tradedAmount = tradedAmount.add(trade.getAmount());
                    turnover = turnover.add(trade.getAmount().multiply(trade.getPrice()));
                }
                order.setTradedAmount(tradedAmount);
                order.setTurnover(turnover);
                if(!order.isCompleted()){
                    tradingOrders.add(order);
                }
            });
            try {
				trader.trade(tradingOrders);
			} catch (ParseException e) {
				e.printStackTrace();
				log.info("异常：Exchange Spring Event：trader.trade(tradingOrders);");
			}
        });
        */
    }

}
