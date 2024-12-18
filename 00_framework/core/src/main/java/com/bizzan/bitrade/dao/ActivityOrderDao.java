package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.ActivityOrder;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ActivityOrderDao extends BaseDao<ActivityOrder> {

    List<ActivityOrder> getAllByActivityIdEquals(Long activityId);

    List<ActivityOrder> getAllByMemberIdAndActivityIdEquals(Long memberId, Long activityId);

}
