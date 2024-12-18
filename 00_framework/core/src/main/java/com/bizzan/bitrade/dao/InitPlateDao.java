package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.InitPlate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InitPlateDao extends BaseDao<InitPlate> {

    @Query(value = "select * from init_plate where symbol=:symbol", nativeQuery = true)
    InitPlate findInitPlateBySymbol(@Param("symbol") String symbol);
}
