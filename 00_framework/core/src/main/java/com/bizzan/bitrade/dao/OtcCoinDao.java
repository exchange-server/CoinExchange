package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.OtcCoin;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Jammy
 * @date 2020年01月12日
 */
public interface OtcCoinDao extends BaseDao<OtcCoin> {

    OtcCoin findOtcCoinByUnitAndStatus(String unit, CommonStatus status);

    List<OtcCoin> findAllByStatus(CommonStatus status);

    OtcCoin findOtcCoinByUnit(String unit);

    @Query("select distinct a.unit from OtcCoin a where a.status = 0")
    List<String> findAllUnits();

}
