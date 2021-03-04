package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.LockedOrderDetailDao;
import com.bizzan.bitrade.dao.MiningOrderDetailDao;
import com.bizzan.bitrade.entity.LockedOrderDetail;
import com.bizzan.bitrade.entity.MiningOrderDetail;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LockedOrderDetailService {
    @Autowired
    private LockedOrderDetailDao lockedOrderDetailDao;

    public LockedOrderDetail findOne(Long id) {
        return lockedOrderDetailDao.findOne(id);
    }

    public LockedOrderDetail save(LockedOrderDetail lockedOrderDetail) {
        return lockedOrderDetailDao.save(lockedOrderDetail);
    }

    public LockedOrderDetail saveAndFlush(LockedOrderDetail lockedOrderDetail) {
        return lockedOrderDetailDao.saveAndFlush(lockedOrderDetail);
    }

    public Page<LockedOrderDetail> findAll(Predicate predicate, Pageable pageable){
        return lockedOrderDetailDao.findAll(predicate, pageable);
    }

    public Page<LockedOrderDetail> findAllByMemberId(Long memberId, int pageNo, int pageSize) {
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<LockedOrderDetail> specification = new Criteria<LockedOrderDetail>();
        specification.add(Restrictions.eq("memberId", memberId, false));

        return lockedOrderDetailDao.findAll(specification, pageRequest);
    }

    public Page<LockedOrderDetail> findAllByMiningOrderId(Long lockedOrderId, int pageNo, int pageSize) {
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<LockedOrderDetail> specification = new Criteria<LockedOrderDetail>();
        specification.add(Restrictions.eq("lockedOrderId", lockedOrderId, false));

        return lockedOrderDetailDao.findAll(specification, pageRequest);
    }
}
