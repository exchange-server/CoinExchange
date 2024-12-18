package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ExchangeOrderDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExchangeOrderDetailRepository extends MongoRepository<ExchangeOrderDetail, String> {
    List<ExchangeOrderDetail> findAllByOrderId(String orderId);
}
