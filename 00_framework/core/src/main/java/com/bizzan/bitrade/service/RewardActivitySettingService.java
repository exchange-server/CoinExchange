package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.ActivityRewardType;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.dao.RewardActivitySettingDao;
import com.bizzan.bitrade.entity.RewardActivitySetting;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @date 2020年03月08日
 */
@Service
public class RewardActivitySettingService extends TopBaseService<RewardActivitySetting, RewardActivitySettingDao> {

    @Override
    @Resource
    public void setDao(RewardActivitySettingDao dao) {
        this.dao = dao;
    }


    public RewardActivitySetting findByType(ActivityRewardType type) {
        return dao.findByStatusAndType(BooleanEnum.IS_TRUE, type);
    }

    @Override
    public RewardActivitySetting save(RewardActivitySetting rewardActivitySetting) {
        return dao.save(rewardActivitySetting);
    }

   /* public List<RewardActivitySetting> page(Predicate predicate){
        Pageable pageable = new PageRequest()
        Iterable<RewardActivitySetting> iterable = rewardActivitySettingDao.findAll(predicate, QRewardActivitySetting.rewardActivitySetting.updateTime.desc());
        return (List<RewardActivitySetting>) iterable ;
    }*/


}
