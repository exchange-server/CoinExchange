package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.util.MessageResult;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Shaoxianjun
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1816:54
 */
@RestController
@RequestMapping("exchange-coin")
public class ExchangeCoinController extends BaseController {
    @Resource
    private ExchangeCoinService service;

    //获取基币
    @RequestMapping("base-symbol")
    public MessageResult baseSymbol() {
        List<String> baseSymbol = service.getBaseSymbol();
        if (baseSymbol != null && baseSymbol.size() > 0) {
            return success(baseSymbol);
        }
        return error("baseSymbol null");

    }

}
