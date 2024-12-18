package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.ExchangeTrade;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExchangeTradeRepository extends MongoRepository<ExchangeTrade, String> {
}
