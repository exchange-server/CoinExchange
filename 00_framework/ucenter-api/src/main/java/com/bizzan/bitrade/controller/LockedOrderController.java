package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.entity.LockedOrder;
import com.bizzan.bitrade.entity.LockedOrderDetail;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.LockedOrderDetailService;
import com.bizzan.bitrade.service.LockedOrderService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

@RestController
@RequestMapping("lockedorder")
public class LockedOrderController extends BaseController {
    @Resource
    private LockedOrderService lockedOrderService;

    @Resource
    private LockedOrderDetailService lockedOrderDetailService;

    /**
     * 我的矿机列表
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("my-locked")
    public MessageResult page(@SessionAttribute(SESSION_MEMBER) AuthMember member, @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Assert.notNull(member, "The login timeout!");
        MessageResult mr = new MessageResult();
        Page<LockedOrder> all = lockedOrderService.findAllByMemberIdPage(member.getId(), pageNo, pageSize);
        long currentTime = DateUtil.getCurrentDate().getTime();
        for (int i = 0; i < all.getContent().size(); i++) {
            // 已超时
            if (currentTime > all.getContent().get(i).getEndTime().getTime()) {
                all.getContent().get(i).setLockedStatus(2); // 已结束
            }
            // 超出天数
            if (all.getContent().get(i).getReleasedDays() >= all.getContent().get(i).getLockedDays()) {
                all.getContent().get(i).setLockedStatus(2); // 已结束
            }
        }
        mr.setCode(0);
        mr.setData(all);
        return mr;
    }

    /**
     * 获取指定矿机详情
     *
     * @param member
     * @param miningId
     * @return
     */
    @RequestMapping("my-locked-detail")
    public MessageResult miningDetail(@SessionAttribute(SESSION_MEMBER) AuthMember member, Long miningId) {
        Assert.notNull(member, "The login timeout!");
        Assert.notNull(miningId, "锁仓不存在!");
        LockedOrder mo = lockedOrderService.findOne(miningId);
        if (mo != null) {
            if (mo.getMemberId().longValue() != member.getId()) {
                return error("非法访问");
            }
            long currentTime = DateUtil.getCurrentDate().getTime();
            if (currentTime > mo.getEndTime().getTime()) {
                mo.setLockedStatus(2);
            }
            if (mo.getReleasedDays() >= mo.getLockedDays()) {
                mo.setLockedStatus(2);
            }
            return success(mo);
        } else {
            return error("锁仓不存在");
        }
    }

    /**
     * 矿机产出明细
     *
     * @param member
     * @param miningId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("mining-detail")
    public MessageResult miningDetail(@SessionAttribute(SESSION_MEMBER) AuthMember member,
                                      @RequestParam(value = "miningId", defaultValue = "1") Long miningId,
                                      @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Assert.notNull(member, "The login timeout!");
        Assert.notNull(miningId, "锁仓不存在!");
        LockedOrder mining = lockedOrderService.findOne(miningId);
        Assert.notNull(mining, "锁仓不存在!");
        if (mining.getMemberId().longValue() != member.getId()) {
            return error("非法访问");
        }
        Page<LockedOrderDetail> all = lockedOrderDetailService.findAllByMiningOrderId(miningId, pageNo, pageSize);
        return success(all);
    }
}
