package com.bizzan.bitrade.event;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.PromotionRewardType;
import com.bizzan.bitrade.constant.RewardRecordType;
import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.Order;
import com.bizzan.bitrade.entity.RewardPromotionSetting;
import com.bizzan.bitrade.entity.RewardRecord;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.RewardPromotionSettingService;
import com.bizzan.bitrade.service.RewardRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;

import static com.bizzan.bitrade.util.BigDecimalUtils.add;
import static com.bizzan.bitrade.util.BigDecimalUtils.getRate;
import static com.bizzan.bitrade.util.BigDecimalUtils.mulRound;

/**
 * @author Shaoxianjun
 * @date 2020年01月22日
 */
@Service
public class OrderEvent {
    @Resource
    private MemberDao memberDao;
    @Resource
    private MemberWalletService memberWalletService;
    @Resource
    private RewardRecordService rewardRecordService;
    @Resource
    private RewardPromotionSettingService rewardPromotionSettingService;

    public void onOrderCompleted(Order order) {
        Member member = memberDao.findOne(order.getMemberId());
        member.setTransactions(member.getTransactions() + 1);
        Member member1 = memberDao.findOne(order.getCustomerId());
        member1.setTransactions(member1.getTransactions() + 1);
        RewardPromotionSetting rewardPromotionSetting = rewardPromotionSettingService.findByType(PromotionRewardType.TRANSACTION);
        if (rewardPromotionSetting != null) {
            Member[] array = {member, member1};
            Arrays.stream(array).forEach(
                    x -> {
                        if (x.getTransactions() == 1 && x.getInviterId() != null) {
                            Member member2 = memberDao.findOne(x.getInviterId());
                            MemberWallet memberWallet1 = memberWalletService.findByCoinAndMember(rewardPromotionSetting.getCoin(), member2);
                            BigDecimal amount1 = mulRound(order.getNumber(), getRate(JSONObject.parseObject(rewardPromotionSetting.getInfo()).getBigDecimal("one")));
                            memberWallet1.setBalance(add(memberWallet1.getBalance(), amount1));
                            memberWalletService.save(memberWallet1);
                            RewardRecord rewardRecord1 = new RewardRecord();
                            rewardRecord1.setAmount(amount1);
                            rewardRecord1.setCoin(rewardPromotionSetting.getCoin());
                            rewardRecord1.setMember(member2);
                            rewardRecord1.setRemark(rewardPromotionSetting.getType().getCnName());
                            rewardRecord1.setType(RewardRecordType.PROMOTION);
                            rewardRecordService.save(rewardRecord1);
                            if (member2.getInviterId() != null) {
                                Member member3 = memberDao.findOne(member2.getInviterId());
                                MemberWallet memberWallet2 = memberWalletService.findByCoinAndMember(rewardPromotionSetting.getCoin(), member3);
                                BigDecimal amount2 = mulRound(order.getNumber(), getRate(JSONObject.parseObject(rewardPromotionSetting.getInfo()).getBigDecimal("two")));
                                memberWallet2.setBalance(add(memberWallet2.getBalance(), amount2));
                                RewardRecord rewardRecord2 = new RewardRecord();
                                rewardRecord2.setAmount(amount2);
                                rewardRecord2.setCoin(rewardPromotionSetting.getCoin());
                                rewardRecord2.setMember(member3);
                                rewardRecord2.setRemark(rewardPromotionSetting.getType().getCnName());
                                rewardRecord2.setType(RewardRecordType.PROMOTION);
                                rewardRecordService.save(rewardRecord2);
                            }
                        }
                    }
            );
        }
    }
}
