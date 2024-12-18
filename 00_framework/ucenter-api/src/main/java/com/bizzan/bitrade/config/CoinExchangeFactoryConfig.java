package com.bizzan.bitrade.config;

import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.system.CoinExchangeFactory;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class CoinExchangeFactoryConfig {
    @Resource
    private CoinService coinService;

    @Bean
    public CoinExchangeFactory createCoinExchangeFactory() {
        List<Coin> coinList = coinService.findAll();
        CoinExchangeFactory factory = new CoinExchangeFactory();
        coinList.forEach(coin ->
                factory.set(coin.getUnit(), new BigDecimal(coin.getUsdRate()), new BigDecimal(coin.getCnyRate()))
        );
        return factory;
    }
}
