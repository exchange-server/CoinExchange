package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.InitPlateDao;
import com.bizzan.bitrade.entity.InitPlate;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class InitPlateService extends BaseService {

    @Resource
    private InitPlateDao initPlateDao;

    public InitPlate findInitPlateBySymbol(String symbol) {
        return initPlateDao.findInitPlateBySymbol(symbol);
    }

    public InitPlate save(InitPlate initPlate) {
        return initPlateDao.save(initPlate);
    }

    public InitPlate saveAndFlush(InitPlate initPlate) {
        return initPlateDao.saveAndFlush(initPlate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(long id) {
        initPlateDao.delete(id);
    }

    public Page<InitPlate> findAllByPage(Criteria<InitPlate> specification, PageRequest pageRequest) {
        return initPlateDao.findAll(specification, pageRequest);
    }

    public InitPlate findByInitPlateId(long id) {
        return initPlateDao.findOne(id);
    }
}
