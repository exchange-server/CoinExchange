package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MiningOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MiningOrderDao extends BaseDao<MiningOrder> {

    List<MiningOrder> findAllByMemberId(Long memberId);

    List<MiningOrder> findAllByMemberIdAndActivityId(Long memberId, Long activityId);

    List<MiningOrder> findAllByActivityId(Long activityId);

    List<MiningOrder> findAllByMiningStatus(int miningStatus);
}
