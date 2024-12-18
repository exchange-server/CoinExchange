package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.RewardRecordType;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.RewardRecord;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Jammy
 * @date 2020年03月08日
 */
public interface RewardRecordDao extends BaseDao<RewardRecord> {
    List<RewardRecord> findAllByMemberAndType(Member member, RewardRecordType type);

    @Query(value = "select coin_id , sum(amount) from reward_record where member_id = :memberId and type = :type group by coin_id", nativeQuery = true)
    List<Object[]> getAllPromotionReward(@Param("memberId") long memberId, @Param("type") int type);
}
