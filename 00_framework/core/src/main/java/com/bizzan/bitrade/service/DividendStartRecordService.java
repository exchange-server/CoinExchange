package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.DividendStartRecordDao;
import com.bizzan.bitrade.entity.DividendStartRecord;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jammy
 * @date 2020年03月22日
 */
@Service
public class DividendStartRecordService extends TopBaseService<DividendStartRecord, DividendStartRecordDao> {

    @Override
    @Resource
    public void setDao(DividendStartRecordDao dao) {
        super.setDao(dao);
    }

    public List<DividendStartRecord> matchRecord(long start, long end, String unit) {
        return dao.findAllByTimeAndUnit(start, end, unit);
    }

    @Override
    public DividendStartRecord save(DividendStartRecord dividendStartRecord) {
        return dao.save(dividendStartRecord);
    }


}
