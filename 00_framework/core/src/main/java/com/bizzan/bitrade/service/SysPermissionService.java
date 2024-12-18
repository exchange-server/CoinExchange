package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.SysPermissionDao;
import com.bizzan.bitrade.entity.SysPermission;
import com.bizzan.bitrade.service.Base.BaseService;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jammy
 * @date 2020年12月20日
 */
@Service
public class SysPermissionService extends BaseService<SysPermission> {

    @Resource
    private SysPermissionDao sysPermissionDao;

    @Resource
    public void setDao(SysPermissionDao dao) {
        super.setDao(dao);
    }

    public SysPermission findOne(Long id) {
        return sysPermissionDao.findOne(id);
    }

    @Override
    public List<SysPermission> findAll() {
        return sysPermissionDao.findAll();
    }

    public SysPermission save(SysPermission sysPermission) {
        return sysPermissionDao.save(sysPermission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletes(Long[] ids) {
        for (long id : ids) {
            sysPermissionDao.deletePermission(id);
            sysPermissionDao.delete(id);
        }
    }

    public Page<SysPermission> findAll(Predicate predicate, Pageable pageable) {
        return sysPermissionDao.findAll(predicate, pageable);
    }
}
