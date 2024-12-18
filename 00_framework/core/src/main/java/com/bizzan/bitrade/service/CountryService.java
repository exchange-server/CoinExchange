package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.CountryDao;
import com.bizzan.bitrade.entity.Country;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jammy
 * @date 2020年02月10日
 */
@Service
public class CountryService {
    @Resource
    private CountryDao countryDao;

    public List<Country> getAllCountry() {
        return countryDao.findAllOrderBySort();
    }

    public Country findOne(String zhName) {
        return countryDao.findByZhName(zhName);
    }

}
