package com.bizzan.bitrade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bizzan.bitrade.dao.MemberLevelDao;
import com.bizzan.bitrade.entity.MemberLevel;
import com.bizzan.bitrade.service.Base.BaseService;

import java.util.List;

/**
 * @author Jammy
 * @description
 * @date 2019/12/26 17:26
 */
@Service
public class MemberLevelService extends BaseService {
    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public List<MemberLevel> findAll(){
        return memberLevelDao.findAll();
    }

    /**
     * @author Jammy
     * @description id查询一个
     * @date 2019/12/27 10:54
     */
    public MemberLevel findOne(Long id){
        return  memberLevelDao.findOne(id);
    }

    /**
     * @author Jammy
     * @description 查询默认会员的等级
     * @date 2019/12/26 17:58
     */
    public MemberLevel findDefault() {
        return memberLevelDao.findOneByIsDefault(true);
    }

    /**
     * @author Jammy
     * @description 更新状态为false 不包括
     * @date 2019/12/27 11:02
     */
    public int updateDefault() {
       return memberLevelDao.updateDefault();
    }

    public MemberLevel save(MemberLevel memberLevel) {
        return memberLevelDao.save(memberLevel);
    }
}
