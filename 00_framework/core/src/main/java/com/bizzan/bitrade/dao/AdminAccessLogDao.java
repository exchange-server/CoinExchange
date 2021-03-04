package com.bizzan.bitrade.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.AdminAccessLog;

import java.util.List;

/**
 * @author Jammy
 * @date 2020年12月19日
 */
public interface AdminAccessLogDao extends BaseDao<AdminAccessLog> {

}
