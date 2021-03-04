package com.bizzan.bitrade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.MemberSignRecordDao;
import com.bizzan.bitrade.entity.MemberSignRecord;
import com.bizzan.bitrade.service.Base.TopBaseService;

/**
 * @author Jammy
 * @Description:
 * @date 2019/5/410:19
 */
@Service
public class MemberSignRecordService extends TopBaseService<MemberSignRecord, MemberSignRecordDao> {
    @Override
    @Autowired
    public void setDao(MemberSignRecordDao dao) {
        super.setDao(dao);
    }
}
