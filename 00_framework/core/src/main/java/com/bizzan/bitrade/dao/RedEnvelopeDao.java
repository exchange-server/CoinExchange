package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.RedEnvelope;

import java.util.List;

public interface RedEnvelopeDao extends BaseDao<RedEnvelope> {

    RedEnvelope findByEnvelopeNo(String envelopeNo);

    List<RedEnvelope> findAllByMemberId(Long memberId);

    List<RedEnvelope> findAllByState(int state);
}
