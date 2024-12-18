package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.Activity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityDao extends BaseDao<Activity> {

    List<Activity> findAllByStep(int step);

    List<Activity> findAllByTypeAndStep(int type, int step);
}
