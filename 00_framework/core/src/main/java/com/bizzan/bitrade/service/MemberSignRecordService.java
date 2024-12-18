package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.MemberSignRecordDao;
import com.bizzan.bitrade.entity.MemberSignRecord;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @Description:
 * @date 2019/5/410:19
 */
@Service
public class MemberSignRecordService extends TopBaseService<MemberSignRecord, MemberSignRecordDao> {
    @Override
    @Resource
    public void setDao(MemberSignRecordDao dao) {
        super.setDao(dao);
    }
}
