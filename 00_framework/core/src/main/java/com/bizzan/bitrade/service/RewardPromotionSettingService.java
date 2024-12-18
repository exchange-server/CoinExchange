package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PromotionRewardType;
import com.bizzan.bitrade.dao.RewardPromotionSettingDao;
import com.bizzan.bitrade.entity.RewardPromotionSetting;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @date 2020年03月08日
 */
@Service
public class RewardPromotionSettingService extends TopBaseService<RewardPromotionSetting, RewardPromotionSettingDao> {

    @Override
    @Resource
    public void setDao(RewardPromotionSettingDao dao) {
        super.setDao(dao);
    }

    public RewardPromotionSetting findByType(PromotionRewardType type) {
        return dao.findByStatusAndType(BooleanEnum.IS_TRUE, type);
    }

    @Override
    public RewardPromotionSetting save(RewardPromotionSetting setting) {
        return dao.save(setting);
    }

    public void deletes(long[] ids) {
        for (long id : ids) {
            delete(id);
        }
    }

}
