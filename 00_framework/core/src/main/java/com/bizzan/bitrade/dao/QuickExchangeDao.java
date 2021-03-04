package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.PromotionCard;
import com.bizzan.bitrade.entity.QuickExchange;

import java.util.List;

public interface QuickExchangeDao extends BaseDao<QuickExchange> {
    List<QuickExchange> findAllByMemberId(Long memberId);
}
