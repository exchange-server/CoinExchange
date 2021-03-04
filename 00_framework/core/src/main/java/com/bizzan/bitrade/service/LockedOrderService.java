package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.LockedOrderDao;
import com.bizzan.bitrade.dao.MiningOrderDao;
import com.bizzan.bitrade.entity.LockedOrder;
import com.bizzan.bitrade.entity.MiningOrder;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LockedOrderService  extends BaseService {
    @Autowired
    private LockedOrderDao lockedOrderDao;

    public LockedOrder findOne(Long id) {
        return lockedOrderDao.findOne(id);
    }

    public LockedOrder save(LockedOrder lockedOrder) {
        return lockedOrderDao.save(lockedOrder);
    }

    public LockedOrder saveAndFlush(LockedOrder lockedOrder) {
        return lockedOrderDao.saveAndFlush(lockedOrder);
    }

    public Page<LockedOrder> findAll(Predicate predicate, Pageable pageable){
        return lockedOrderDao.findAll(predicate, pageable);
    }

    public List<LockedOrder> findAllByMemberId(Long memberId){
        return lockedOrderDao.findAllByMemberId(memberId);
    }

    public List<LockedOrder> findAllByActivityId(Long activityId){
        return lockedOrderDao.findAllByActivityId(activityId);
    }

    public List<LockedOrder> findAllByMemberIdAndActivityId(Long memberId, Long activityId){
        return lockedOrderDao.findAllByMemberIdAndActivityId(memberId, activityId);
    }

    public List<LockedOrder> findAllByLockedStatus(int lockedStatus){
        return lockedOrderDao.findAllByLockedStatus(lockedStatus);
    }

    public Page<LockedOrder> findAllByMemberIdPage(Long memberId, int pageNo, int pageSize){
        Sort orders = Criteria.sortStatic("createTime.desc");
        //分页参数
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, orders);
        //查询条件
        Criteria<LockedOrder> specification = new Criteria<LockedOrder>();
        specification.add(Restrictions.eq("memberId", memberId, false));

        return lockedOrderDao.findAll(specification, pageRequest);
    }
}
