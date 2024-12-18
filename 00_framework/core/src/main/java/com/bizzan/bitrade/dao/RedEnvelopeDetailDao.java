package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.RedEnvelopeDetail;

import java.util.List;

public interface RedEnvelopeDetailDao extends BaseDao<RedEnvelopeDetail> {

    List<RedEnvelopeDetail> findAllByEnvelopeIdAndMemberId(Long envelopeId, Long memberId);

    List<RedEnvelopeDetail> findAllByEnvelopeId(Long envelopeId);
}
