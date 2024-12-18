package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.dao.base.BaseDao;
import com.bizzan.bitrade.entity.SysRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Jammy
 * @date 2020年12月18日
 */
public interface SysRoleDao extends BaseDao<SysRole> {

    @Modifying
    @Query("update SysRole s set s.description=?1,s.role=?2 where s.id=?3")
    int updateSysRole(String description, String role, Long id);

    @Query("SELECT new SysRole(s.id,s.role,s.description) FROM SysRole s")
    List<SysRole> findAllSysRole();

}
