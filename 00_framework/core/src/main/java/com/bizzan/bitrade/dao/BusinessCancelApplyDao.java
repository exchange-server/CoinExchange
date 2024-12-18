package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.BusinessCancelApply;
import com.bizzan.bitrade.entity.Member;

import java.util.List;

/**
 * @author jiangtao
 * @date 2019/5/17
 */
public interface BusinessCancelApplyDao extends BaseDao<BusinessCancelApply> {

    List<BusinessCancelApply> findByMemberAndStatusOrderByIdDesc(Member member, CertifiedBusinessStatus status);

    List<BusinessCancelApply> findByMemberOrderByIdDesc(Member member);

    long countAllByStatus(CertifiedBusinessStatus status);
}
