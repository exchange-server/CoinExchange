package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.DividendStartRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Jammy
 * @date 2020年03月22日
 */
public interface DividendStartRecordDao extends BaseDao<DividendStartRecord> {

    @Query("select a from DividendStartRecord a where ((a.start<:start and a.end>=:start) or (a.start<:en and a.end>=:en) " +
            " or (a.start>=:start and a.end<:en ) or (a.start<=:start and a.end>:en)) and a.unit=:unit")
    List<DividendStartRecord> findAllByTimeAndUnit(@Param("start") long start, @Param("en") long end, @Param("unit") String unit);
}
