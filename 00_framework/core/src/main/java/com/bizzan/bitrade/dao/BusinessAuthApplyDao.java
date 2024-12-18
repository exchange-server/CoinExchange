package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.BusinessAuthApply;
import com.bizzan.bitrade.entity.Member;

import java.util.List;

/**
 * @author Shaoxianjun
 * @date 2019/5/7
 */
public interface BusinessAuthApplyDao extends BaseDao<BusinessAuthApply> {

    List<BusinessAuthApply> findByMemberOrderByIdDesc(Member member);

    List<BusinessAuthApply> findByMemberAndCertifiedBusinessStatusOrderByIdDesc(Member member, CertifiedBusinessStatus certifiedBusinessStatus);

    long countAllByCertifiedBusinessStatus(CertifiedBusinessStatus status);

}
