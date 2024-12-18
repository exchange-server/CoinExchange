package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.WalletTransRecordDao;
import com.bizzan.bitrade.entity.WalletTransRecord;
import com.bizzan.bitrade.service.Base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class WalletTransRecordService extends BaseService {

    @Resource
    private WalletTransRecordDao walletTransRecordDao;

    /**
     * 保存划转记录
     *
     * @param record
     * @return
     */
    public WalletTransRecord save(WalletTransRecord record) {
        return walletTransRecordDao.saveAndFlush(record);
    }

}
