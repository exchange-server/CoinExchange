package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.AdminDao;
import com.bizzan.bitrade.dao.DepartmentDao;
import com.bizzan.bitrade.entity.Admin;
import com.bizzan.bitrade.entity.Department;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Jammy
 * @date 2020年12月19日
 */
@Service
public class DepartmentService extends BaseService {
    @Autowired
    private LocaleMessageSourceService msService;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private AdminDao adminDao;


    /**
     * 添加或更新部门
     *
     * @param department
     * @return
     */
    public Department save(Department department) {
        return departmentDao.save(department);
    }

    public Department findOne(Long departmentId) {
        return departmentDao.findOne(departmentId);
    }


    public Department getDepartmentDetail(Long departmentId) {
        Department department = departmentDao.findOne(departmentId);
        Assert.notNull(department, msService.getMessage("DEPARTMENT_DOES_NOT_EXIST"));
        return department;
    }


    public Page<Department> findAll(Predicate predicate, Pageable pageable) {
        return departmentDao.findAll(predicate, pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    public MessageResult deletes(Long id) {
        Department department = departmentDao.findOne(id);
        List<Admin> list = adminDao.findAllByDepartment(department);
        if (list != null && list.size() > 0) {
            MessageResult result = MessageResult.error(msService.getMessage("DELETE_ALL_USERS_IN_THIS_DEPARTMENT"));
            return result;
        }
        departmentDao.delete(id);
        return MessageResult.success(msService.getMessage("SUCCESS"));
    }
}
