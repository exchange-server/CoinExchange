package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.bizzan.bitrade.dao.AdvertiseDao;
import com.bizzan.bitrade.dao.AppealDao;
import com.bizzan.bitrade.dao.BusinessCancelApplyDao;
import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.dao.OrderDao;
import com.bizzan.bitrade.entity.BusinessCancelApply;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BusinessCancelApplyService extends TopBaseService<BusinessCancelApply, BusinessCancelApplyDao> {

    @Resource
    private OrderDao orderDao;
    @Resource
    private AppealDao appealDao;
    @Resource
    private AdvertiseDao advertiseDao;
    @Resource
    private MemberDao memberDao;

    @Override
    @Resource
    public void setDao(BusinessCancelApplyDao dao) {
        super.setDao(dao);
    }

    public List<BusinessCancelApply> findByMemberAndStaus(Member member, CertifiedBusinessStatus status) {
        return dao.findByMemberAndStatusOrderByIdDesc(member, status);
    }

    public List<BusinessCancelApply> findByMember(Member member) {
        return dao.findByMemberOrderByIdDesc(member);
    }

    public Map<String, Object> getBusinessOrderStatistics(Long memberId) {
        return orderDao.getBusinessStatistics(memberId);
    }

    public Map<String, Object> getBusinessAppealStatistics(Long memberId) {
        Map<String, Object> map = new HashedMap();
        Long complainantNum = appealDao.getBusinessAppealInitiatorIdStatistics(memberId);
        Long defendantNum = appealDao.getBusinessAppealAssociateIdStatistics(memberId);
        map.put("defendantNum", defendantNum);
        map.put("complainantNum", complainantNum);
        return map;
    }

    public Long getAdvertiserNum(Long memberId) {
        Member member = memberDao.findOne(memberId);
        return advertiseDao.getAdvertiseNum(member);
    }

    public long countAuditing() {
        return dao.countAllByStatus(CertifiedBusinessStatus.CANCEL_AUTH);
    }


}
