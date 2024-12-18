package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.PromotionCardOrder;

import java.util.List;

public interface PromotionCardOrderDao extends BaseDao<PromotionCardOrder> {
    List<PromotionCardOrder> findAllByCardIdAndMemberId(Long cardId, Long memberId);

    List<PromotionCardOrder> findAllByCardId(Long cardId);


    List<PromotionCardOrder> findAllByMemberIdAndIsFree(long memberId, int isFree);
}
