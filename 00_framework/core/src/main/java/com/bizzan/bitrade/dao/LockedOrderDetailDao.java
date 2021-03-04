package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.LockedOrderDetail;
import com.bizzan.bitrade.entity.MiningOrderDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LockedOrderDetailDao extends BaseDao<LockedOrderDetail> {
    List<LockedOrderDetail> findAllByMemberId(Long memberId);
}
