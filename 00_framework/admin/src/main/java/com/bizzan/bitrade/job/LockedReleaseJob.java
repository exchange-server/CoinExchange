package com.bizzan.bitrade.job;

import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class LockedReleaseJob {

    @Autowired
    private SMSProvider smsProvider;

    private static Logger logger = LoggerFactory.getLogger(LockedReleaseJob.class);

    @Autowired
    private LockedOrderDetailService lockedOrderDetailService;

    @Autowired
    private LockedOrderService lockedOrderService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private MemberTransactionService memberTransactionService;

    @Autowired
    private MemberService memberService;

    private void increaseToRelease(){

    }
    /**
     * 日处理：每天晚上11点30释放锁仓
     */
    @Scheduled(cron = "0 30 23 * * *")
   // @Transactional(rollbackFor = Exception.class)
    public void release() {
        List<LockedOrder> list = lockedOrderService.findAllByLockedStatus(1);

        Date currentDate = DateUtil.getCurrentDate();
        for(LockedOrder item : list) {
            if(this.checkNuccessary(item)) {
                this.doRelease(item);
            }
        }
    }

    private boolean checkNuccessary(LockedOrder item){
        if (item.getLockedStatus() != 1) { // 不是释放中的状态
            return false;
        }
        // 日周期，直接释放
        // 周周期，计算是否是释放周期
        long days = DateUtil.diffDays(item.getCreateTime(), DateUtil.getCurrentDate());
        if(days == 0) {
            return false;
        }
        if (item.getPeriod() == 1) {
            if (days % 7 != 0) return false;
        }
        // 月周期，计算是否是释放周期(30天释放一次)
        if (item.getPeriod() == 2) {
            if (days % 30 != 0) return false;
        }
        // 年周期，计算是否是释放周期（365天释放一次）
        if (item.getPeriod() == 3) {
            if (days % 365 != 0) return false;
        }
        return true;
    }

    @Transactional(propagation= Propagation.REQUIRES_NEW)
    void doRelease(LockedOrder item){

        Member member = memberService.findOne(item.getMemberId());
        // 获取当前钱包
        MemberWallet userWallet = memberWalletService.findByCoinUnitAndMemberId(item.getReleaseUnit(), item.getMemberId());

        LockedOrderDetail lod = new LockedOrderDetail();
        BigDecimal rAmount = item.getCurrentReleaseamount(); // 等额释放
        if (item.getReleaseType() == 1) { // 等比释放
            rAmount = item.getTotalLocked().subtract(item.getTotalRelease()).multiply(item.getReleaseCurrentpercent()); // 剩余锁仓数量 x 释放比例
        }
        if (item.getLockedDays() - item.getReleasedDays() == 1) { // 最后一期释放
            rAmount = item.getTotalLocked().subtract(item.getTotalRelease()); // 释放最后没有释放的
        }
        // 保存释放详情
        lod.setOutput(rAmount);
        lod.setReleaseUnit(item.getReleaseUnit());
        lod.setMemberId(item.getMemberId());
        lod.setLockedOrderId(item.getId());
        lod.setCreateTime(DateUtil.getCurrentDate());
        lockedOrderDetailService.save(lod);

        // 扣除用户钱包表中的资产
        if(userWallet == null) {
            logger.info("=======>userWallet is null");
        }
        if(rAmount == null) {
            logger.info("=======>rAmount is null");
        }
        memberWalletService.decreaseToRelease(userWallet.getId(), rAmount);
        // 增加余额资产
        memberWalletService.increaseBalance(userWallet.getId(), rAmount);
        // 增加资产变更记录
        MemberTransaction memberTransaction1 = new MemberTransaction();
        memberTransaction1.setFee(BigDecimal.ZERO);
        memberTransaction1.setAmount(rAmount);
        memberTransaction1.setMemberId(item.getMemberId());
        memberTransaction1.setSymbol(item.getReleaseUnit());
        memberTransaction1.setType(TransactionType.ACTIVITY_BUY);
        memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction1.setRealFee("0");
        memberTransaction1.setDiscountFee("0");
        memberTransactionService.save(memberTransaction1);
        // 更新主表
        item.setTotalRelease(item.getTotalRelease().add(rAmount));
        item.setReleasedDays(item.getReleasedDays() + 1);
        if (item.getLockedDays() - item.getReleasedDays() == 0) { // 最后一期
            item.setLockedStatus(2); // 已结束
        }
        lockedOrderService.save(item);
    }
}
