package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.QuickExchangeDao;
import com.bizzan.bitrade.entity.QuickExchange;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QuickExchangeService extends BaseService {
    @Resource
    private QuickExchangeDao quickExchangeDao;

    public List<QuickExchange> findAllByMemberId(Long memberId) {
        return quickExchangeDao.findAllByMemberId(memberId);
    }

    public QuickExchange save(QuickExchange qe) {
        return quickExchangeDao.save(qe);
    }
}
