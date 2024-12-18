package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.BusinessAuthDeposit;

import java.util.List;

/**
 * @author Shaoxianjun
 * @date 2019/5/5
 */
public interface BusinessAuthDepositDao extends BaseDao<BusinessAuthDeposit> {
    public List<BusinessAuthDeposit> findAllByStatus(CommonStatus status);
}
