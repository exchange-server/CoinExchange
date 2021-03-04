package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.MemberWeightUpperDao;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWeightUpper;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sulinxin
 */
@Service
public class MemberWeightUpperService extends BaseService {

    @Autowired
    private MemberWeightUpperDao memberWeightUpperDao;
    @Autowired
    private MemberService memberService;

    public MemberWeightUpper findMemberWeightUpperByMemberId(Long memberId) {
        Criteria<MemberWeightUpper> specification = new Criteria<MemberWeightUpper>();
        specification.add(Restrictions.eq("memberId", memberId, false));
        return memberWeightUpperDao.findOne(specification);
    }

    public List<MemberWeightUpper> findAllByUpperIds(String uppers) {
        String[] idss = uppers.split(",");
        List<Long> ids = new ArrayList<>();
        for(String id:idss){
            ids.add(Long.parseLong(id));
        }
        return memberWeightUpperDao.findAllByUpperIds(ids);
    }

    public MemberWeightUpper saveMemberWeightUpper(Member member) {
        MemberWeightUpper memberWeightUpper = this.findMemberWeightUpperByMemberId(member.getId());
        if(memberWeightUpper!=null){
            return memberWeightUpper;
        }
        memberWeightUpper = new MemberWeightUpper();
        //找上级 如果有上级
        if(member.getInviterId()!=null){
            Member inviter = memberService.findOne(member.getInviterId());
            //有上级
            MemberWeightUpper upper = this.saveMemberWeightUpper(inviter);
            memberWeightUpper.setFirstMemberId(upper.getFirstMemberId());
            memberWeightUpper.setRate(0);
            memberWeightUpper.setMemberId(member.getId());
            String uppers = upper.getUpper();
            if(uppers==null || "".equals(uppers.trim())){
                uppers = upper.getMemberId().toString();
            }else {
                uppers = uppers+","+upper.getMemberId();
            }
            memberWeightUpper.setUpper(uppers);
        }else {
            //最上级
            if("1".equals(member.getSuperPartner())){
                memberWeightUpper.setRate(100);
            }else {
                memberWeightUpper.setRate(0);
            }
            memberWeightUpper.setFirstMemberId(member.getId());
            memberWeightUpper.setMemberId(member.getId());
            memberWeightUpper.setUpper(null);

        }
        memberWeightUpper = memberWeightUpperDao.save(memberWeightUpper);
        return memberWeightUpper;
    }
}
