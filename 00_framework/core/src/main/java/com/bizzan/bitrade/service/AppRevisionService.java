package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.Platform;
import com.bizzan.bitrade.dao.AppRevisionDao;
import com.bizzan.bitrade.entity.AppRevision;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/2416:19
 */
@Service
public class AppRevisionService extends TopBaseService<AppRevision, AppRevisionDao> {

    @Override
    @Resource
    public void setDao(AppRevisionDao dao) {
        super.setDao(dao);
    }

    public AppRevision findRecentVersion(Platform p) {
        return dao.findAppRevisionByPlatformOrderByIdDesc(p);
    }
}
