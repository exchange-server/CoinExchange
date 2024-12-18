package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MiningOrderDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MiningOrderDetailDao extends BaseDao<MiningOrderDetail> {

    List<MiningOrderDetail> findAllByMemberId(Long memberId);

}
