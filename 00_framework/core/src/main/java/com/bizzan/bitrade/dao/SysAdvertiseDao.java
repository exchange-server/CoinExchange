package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.SysAdvertiseLocation;
import com.bizzan.bitrade.entity.SysAdvertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Jammy
 * @description
 * @date 2019/1/6 16:44
 */
public interface SysAdvertiseDao extends JpaRepository<SysAdvertise, String>, JpaSpecificationExecutor<SysAdvertise>, QueryDslPredicateExecutor<SysAdvertise> {
    List<SysAdvertise> findAllByStatusAndSysAdvertiseLocationOrderBySortAsc(CommonStatus status, SysAdvertiseLocation sysAdvertiseLocation);

    List<SysAdvertise> findAllByStatusAndSysAdvertiseLocationAndLangOrderBySortAsc(CommonStatus status, SysAdvertiseLocation sysAdvertiseLocation, String lang);

    @Query("select max(s.sort) from SysAdvertise s")
    int findMaxSort();

    //获得所有广告并通过sort排序,筛选条件 状态：上线，广告位置和sort
    @Query(value = "SELECT * FROM sys_advertise s WHERE s.sort>=:sort AND s.status=0 AND s.sys_advertise_location=:cate ORDER BY sort ASC ", nativeQuery = true)
    List<SysAdvertise> querySysAdvertise(@Param("sort") int sort, @Param("cate") int cate);

}
