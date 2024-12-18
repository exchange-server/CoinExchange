package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.PromotionCardDao;
import com.bizzan.bitrade.dao.PromotionCardOrderDao;
import com.bizzan.bitrade.entity.PromotionCard;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PromotionCardService extends BaseService {
    @Resource
    private PromotionCardDao promotionCardDao;

    @Resource
    private PromotionCardOrderDao promotionCardOrderDao;

    public PromotionCard findPromotionCardByCardNo(String cardNo) {
        return promotionCardDao.findByCardNo(cardNo);
    }

    public List<PromotionCard> findAllByMemberId(Long memberId) {
        return promotionCardDao.findAllByMemberId(memberId);
    }

    public PromotionCard findOne(Long id) {
        return promotionCardDao.findOne(id);
    }

    public PromotionCard save(PromotionCard card) {
        return promotionCardDao.save(card);
    }

    public PromotionCard saveAndFlush(PromotionCard card) {
        return promotionCardDao.saveAndFlush(card);
    }

    public PromotionCard findById(Long id) {
        return promotionCardDao.findOne(id);
    }

    public Page<PromotionCard> findAll(Predicate predicate, Pageable pageable) {
        return promotionCardDao.findAll(predicate, pageable);
    }

    public List<PromotionCard> findAllByMemberIdAndIsFree(long memberId, int isFree) {
        return promotionCardDao.findAllByMemberIdAndIsFree(memberId, isFree);
    }
}
