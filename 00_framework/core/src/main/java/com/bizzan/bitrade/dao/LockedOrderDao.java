package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.LockedOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LockedOrderDao extends BaseDao<LockedOrder> {
    List<LockedOrder> findAllByMemberId(Long memberId);

    List<LockedOrder> findAllByMemberIdAndActivityId(Long memberId, Long activityId);

    List<LockedOrder> findAllByActivityId(Long activityId);

    List<LockedOrder> findAllByLockedStatus(int lockedStatus);
}
