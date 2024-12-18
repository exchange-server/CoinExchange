package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.MemberPromotion;
import com.bizzan.bitrade.vo.MemberPromotionStasticVO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Jammy
 * @date 2020年03月08日
 */
public interface MemberPromotionDao extends BaseDao<MemberPromotion> {
    @Query(value = "select *, count(*) as count from member_promotion where level = :level and create_time > :startDate and create_time < :endDate group by (inviter_id) order by count desc limit :limitNum", nativeQuery = true)
    List<MemberPromotionStasticVO> getInviteGroupByTypeAndDate(@Param("level") int level, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("limitNum") int limitNum);
}
