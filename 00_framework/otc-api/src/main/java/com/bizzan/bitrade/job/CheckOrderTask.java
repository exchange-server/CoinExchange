package com.bizzan.bitrade.job;

import com.bizzan.bitrade.entity.Order;
import com.bizzan.bitrade.service.OrderService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jammy
 * @date 2020年01月22日
 */
@Component
@Slf4j
public class CheckOrderTask {
    @Resource
    private OrderService orderService;

    @Scheduled(fixedRate = 60000)
    public void checkExpireOrder() {
        log.info("=========开始检查过期订单===========");
        List<Order> list = orderService.checkExpiredOrder();
        list.stream().forEach(x -> {
                    try {
                        orderService.cancelOrderTask(x);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.warn("订单编号{}:自动取消失败", x.getOrderSn());
                    }
                }
        );
        log.info("=========检查过期订单结束===========");
    }
}
