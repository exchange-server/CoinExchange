package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.ExchangeOrderDetailRepository;
import com.bizzan.bitrade.dao.ExchangeOrderRepository;
import com.bizzan.bitrade.dao.OrderDetailAggregationRepository;
import com.bizzan.bitrade.entity.ExchangeOrderDetail;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ExchangeOrderDetailService {
    @Resource
    private ExchangeOrderDetailRepository orderDetailRepository;

    @Resource
    private ExchangeOrderRepository exchangeOrderRepository;

    @Resource
    private MemberService memberService;

    @Resource
    private OrderDetailAggregationRepository orderDetailAggregationRepository;


    /**
     * 查询某订单的成交详情
     *
     * @param orderId
     * @return
     */
    public List<ExchangeOrderDetail> findAllByOrderId(String orderId) {
        return orderDetailRepository.findAllByOrderId(orderId);
    }

    public ExchangeOrderDetail save(ExchangeOrderDetail detail) {
        return orderDetailRepository.save(detail);
    }
}
