package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.coin.CoinExchangeFactory;
import com.bizzan.bitrade.service.OtcCoinService;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.bizzan.bitrade.util.MessageResult.success;

/**
 * @author Jammy
 * @date 2020年01月06日
 */
@RestController
@Slf4j
@RequestMapping(value = "/coin")
public class OtcCoinController {

    @Resource
    private OtcCoinService coinService;
    @Resource
    private CoinExchangeFactory coins;

    /**
     * 取得正常的币种
     *
     * @return
     */
    @RequestMapping(value = "all")
    public MessageResult allCoin() throws Exception {
        List<Map<String, String>> list = coinService.getAllNormalCoin();
        list.stream().forEachOrdered(x -> {
            if (coins.get(x.get("unit")) != null) {
                x.put("marketPrice", coins.get(x.get("unit")).toString());
            }
        });
        MessageResult result = success();
        result.setData(list);
        return result;
    }
}
