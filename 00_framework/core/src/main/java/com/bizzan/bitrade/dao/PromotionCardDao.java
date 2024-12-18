package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.PromotionCard;

import java.util.List;

public interface PromotionCardDao extends BaseDao<PromotionCard> {

    PromotionCard findByCardNo(String cardNo);

    List<PromotionCard> findAllByMemberId(Long memberId);

    List<PromotionCard> findAllByMemberIdAndIsFree(long memberId, int isFree);
}
