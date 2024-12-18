package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.DataDictionaryDao;
import com.bizzan.bitrade.entity.DataDictionary;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1214:19
 */
@Service
public class DataDictionaryService extends TopBaseService<DataDictionary, DataDictionaryDao> {
    @Resource
    DataDictionaryDao dataDictionaryDao;

    @Override
    @Resource
    public void setDao(DataDictionaryDao dao) {
        super.setDao(dao);
    }

    public DataDictionary findByBond(String bond) {
        return dataDictionaryDao.findByBond(bond);
    }

}
