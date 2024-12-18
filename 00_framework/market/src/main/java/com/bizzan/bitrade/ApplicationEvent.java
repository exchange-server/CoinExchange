package com.bizzan.bitrade;

import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.processor.CoinProcessor;
import com.bizzan.bitrade.processor.CoinProcessorFactory;
import com.bizzan.bitrade.service.ExchangeCoinService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ApplicationEvent implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    ExchangeCoinService coinService;
    @Resource
    private CoinProcessorFactory coinProcessorFactory;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("====应用初始化完成，开启CoinProcessor====");
        List<ExchangeCoin> coins = coinService.findAllEnabled();
        coins.forEach(coin -> {
            CoinProcessor processor = coinProcessorFactory.getProcessor(coin.getSymbol());
            processor.initializeThumb();
            processor.initializeUsdRate();
            processor.setIsHalt(false);
        });
    }
}
