package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.HotTransferRecordDao;
import com.bizzan.bitrade.entity.HotTransferRecord;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class HotTransferRecordService extends TopBaseService<HotTransferRecord, HotTransferRecordDao> {

    @Override
    @Resource
    public void setDao(HotTransferRecordDao dao) {
        super.setDao(dao);
    }
}
