package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.WebsiteInformationDao;
import com.bizzan.bitrade.entity.QWebsiteInformation;
import com.bizzan.bitrade.entity.WebsiteInformation;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @description
 * @date 2019/1/26 10:18
 */
@Service
public class WebsiteInformationService extends BaseService {
    @Resource
    private WebsiteInformationDao websiteInformationDao;

    @Transactional(readOnly = true)
    public WebsiteInformation fetchOne() {
        QWebsiteInformation qEntity = QWebsiteInformation.websiteInformation;
        return queryFactory.selectFrom(qEntity).fetchOne();
    }

    public WebsiteInformation save(WebsiteInformation websiteInformation) {
        return websiteInformationDao.save(websiteInformation);
    }

}
