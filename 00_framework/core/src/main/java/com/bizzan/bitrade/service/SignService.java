package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.SignStatus;
import com.bizzan.bitrade.dao.SignDao;
import com.bizzan.bitrade.entity.Sign;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @Description:
 * @date 2019/5/311:11
 */
@Service
public class SignService extends TopBaseService<Sign, SignDao> {


    @Override
    @Resource
    public void setDao(SignDao dao) {
        super.setDao(dao);
    }

    public Sign fetchUnderway() {
        return dao.findByStatus(SignStatus.UNDERWAY);
    }

    /**
     * 提前关闭
     *
     * @param sign 提前关闭
     */
    public void earlyClosing(Sign sign) {
        sign.setStatus(SignStatus.FINISH);
        dao.save(sign);
    }

}
