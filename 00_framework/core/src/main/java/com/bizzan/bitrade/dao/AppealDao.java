package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.AppealStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Appeal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Jammy
 * @date 2020年01月23日
 */
public interface AppealDao extends BaseDao<Appeal> {

    @Query("select count(a.id) as complainantNum from Appeal a where a.initiatorId = :memberId")
    Long getBusinessAppealInitiatorIdStatistics(@Param("memberId") Long memberId);

    @Query("select count(a.id) as defendantNum from Appeal a where a.associateId = :memberId")
    Long getBusinessAppealAssociateIdStatistics(@Param("memberId") Long memberId);

    long countAllByStatus(AppealStatus status);
}
