package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.CtcAcceptor;
import com.bizzan.bitrade.entity.Member;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CtcAcceptorDao extends BaseDao<CtcAcceptor> {
    List<CtcAcceptor> findAllByStatus(int status);

    List<CtcAcceptor> findAllByMember(Member member);
}
