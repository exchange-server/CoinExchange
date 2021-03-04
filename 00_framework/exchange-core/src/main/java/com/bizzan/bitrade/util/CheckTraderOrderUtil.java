package com.bizzan.bitrade.util;

import com.bizzan.bitrade.entity.ExchangeOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @description: CheckTraderOrderUtil
 * @author QQ:247818019 E-mail:247818019@qq.com
 * @create: 2019/04/28 09:27
 */
@Slf4j
public class CheckTraderOrderUtil {

    /**
     * 查找撮合交易器中订单是否存在
     * @param order
     * @return
     */
    public static boolean isExchangeOrderExist(ExchangeOrder order, RestTemplate restTemplate) {
        try {
            String serviceName = "SERVICE-EXCHANGE-TRADE";
            String url = "http://" + serviceName + "/monitor/order?symbol=" + order.getSymbol() + "&orderId=" + order.getOrderId() + "&direction=" + order.getDirection() + "&type=" + order.getType();
            ResponseEntity<ExchangeOrder> result = restTemplate.getForEntity(url, ExchangeOrder.class);
            log.info("查找交易撮合器结果result={}",result);
            if (result.getStatusCode().value() == 200) {
                ExchangeOrder remoteOrder = result.getBody();
                if (remoteOrder == null) {
                    return false;
                }else {
                    return true;
                }
            }else {
                return false;
            }
        } catch (Exception e) {
            log.info("查找撮合器ERROR={}", e);
            return false;
        }
    }


    public static boolean isWaitingOrderExist(ExchangeOrder order, RestTemplate restTemplate) {
        try {
            String serviceName = "BITRADE-MARKET";
            String url = "http://" + serviceName + "/market/find/waiting?symbol=" + order.getSymbol() + "&orderId=" + order.getOrderId() + "&direction=" + order.getDirection() ;
            ResponseEntity<ExchangeOrder> result = restTemplate.getForEntity(url, ExchangeOrder.class);
            log.info("查找止盈止损撮合器结果result={}",result);
            if (result.getStatusCode().value() == 200) {
                ExchangeOrder remoteOrder = result.getBody();
                if (remoteOrder == null) {
                    return false;
                }else {
                    return true;
                }
            }else {
                return false;
            }
        } catch (Exception e) {
            log.info("查找撮合器ERROR={}", e);
            return false;
        }
    }

}
