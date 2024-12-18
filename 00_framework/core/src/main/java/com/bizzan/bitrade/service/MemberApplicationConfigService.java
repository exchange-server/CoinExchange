package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.MemberApplicationConfigDao;
import com.bizzan.bitrade.entity.MemberApplicationConfig;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MemberApplicationConfigService extends TopBaseService<MemberApplicationConfig, MemberApplicationConfigDao> {

    @Override
    @Resource
    public void setDao(MemberApplicationConfigDao dao) {
        super.setDao(dao);
    }

    public MemberApplicationConfig get() {
        List<MemberApplicationConfig> list = dao.findAll();
        return list != null && list.size() > 0 ? list.get(0) : null;
    }
}
