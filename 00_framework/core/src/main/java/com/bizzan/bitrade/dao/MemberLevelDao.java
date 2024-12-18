package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MemberLevel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Jammy
 * @description 会员等级Dao
 * @date 2019/12/26 17:24
 */
public interface MemberLevelDao extends BaseDao<MemberLevel> {

    MemberLevel findOneByIsDefault(Boolean isDefault);

    @Query("update MemberLevel set isDefault = false  where isDefault = true ")
    @Modifying
    int updateDefault();
}
