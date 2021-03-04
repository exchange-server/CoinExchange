package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.QuickExchangeDao;
import com.bizzan.bitrade.entity.PromotionCardOrder;
import com.bizzan.bitrade.entity.QuickExchange;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuickExchangeService extends BaseService {
    @Autowired
    private QuickExchangeDao quickExchangeDao;

    public List<QuickExchange> findAllByMemberId(Long memberId) {
        return quickExchangeDao.findAllByMemberId(memberId);
    }

    public QuickExchange save(QuickExchange qe) {
        return quickExchangeDao.save(qe);
    }
}
