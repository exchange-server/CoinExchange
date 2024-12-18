package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.SysPermission;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Jammy
 * @date 2020年12月18日
 */
public interface SysPermissionDao extends BaseDao<SysPermission> {

    @Modifying
    @Query(value = "delete from admin_role_permission where rule_id = ?1", nativeQuery = true)
    int deletePermission(long permissionId);
}
