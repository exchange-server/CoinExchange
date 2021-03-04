package com.bizzan.bitrade.controller.activity;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.service.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.vendor.provider.SMSProvider;
import com.sparkframework.security.Encrypt;

@RestController
@RequestMapping("activity/activity")
public class ActivityController extends BaseAdminController {
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private ActivityOrderService activityOrderService;
	
	@Autowired
	private MemberWalletService memberWalletService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
    private LocaleMessageSourceService messageSource;

	@Autowired
	private MiningOrderService miningOrderService;

	@Autowired
	private LockedOrderService lockedOrderService;

    @Autowired
    private MemberTransactionService memberTransactionService;

	@Autowired
	private CoinService coinService;
	
    @Autowired
    private SMSProvider smsProvider;
    
	@Value("${spark.system.md5.key}")
    private String md5Key;
	
	/**
	 * 分页查询
	 * @param pageModel
	 * @return
	 */
	@RequiresPermissions("activity:activity:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.ACTIVITY, operation = "分页查看活动列表Activity")
    public MessageResult activityList(PageModel pageModel) {
		if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("createTime");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<Activity> all = activityService.findAll(null, pageModel.getPageable());
        return success(all);
    }
	@RequiresPermissions("activity:activity:locked-activity")
	@PostMapping("locked-activity")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "查看锁仓活动列表")
	public MessageResult lockedActivityList() {
		List<Activity> all = activityService.findByTypeAndStep(6, 1); // 查询锁仓活动并且在进行中的
		return success(all);
	}

	/**
	 * 添加活动信息
	 * @param activity
	 * @return
	 */
	@RequiresPermissions("activity:activity:add")
    @PostMapping("add")
    @AccessLog(module = AdminModule.ACTIVITY, operation = "新增活动信息Activity")
    public MessageResult ExchangeCoinList(
            @Valid Activity activity) {
		activity.setCreateTime(DateUtil.getCurrentDate());
		activity = activityService.save(activity);
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), activity);
    }
	
	/**
	 * 修改活动进度数值
	 * @param id
	 * @param progress
	 * @return
	 */
	@RequiresPermissions("activity:activity:modify-progress")
    @PostMapping("modify-progress")
    @AccessLog(module = AdminModule.ACTIVITY, operation = "修改活动进度Activity")
    public MessageResult alterActivity(
            @RequestParam("id") Long id,
            @RequestParam("progress") Integer progress) {
		notNull(id, "validate id!");
		
		Activity result = activityService.findOne(id);
		notNull(result, "validate activity!");
		
		if(result.getProgress() > progress.intValue()) {
			return error("新进度数值小于当前数值");
		}
		result.setProgress(progress);
		
		activityService.save(result);
		
		return success(messageSource.getMessage("SUCCESS"));
	}
	
	/**
	 * 修改活动冻结总资产
	 * @param id
	 * @param freezeAmount
	 * @return
	 */
	@RequiresPermissions("activity:activity:modify-freezeamount")
    @PostMapping("modify-freezeamount")
    @AccessLog(module = AdminModule.ACTIVITY, operation = "修改活动冻结总资产Activity")
    public MessageResult alterActivityFreezeAmount(
            @RequestParam("id") Long id,
            @RequestParam("freezeAmount") BigDecimal freezeAmount) {
		notNull(id, "validate id!");
		
		Activity result = activityService.findOne(id);
		notNull(result, "validate activity!");
		
		if(result.getFreezeAmount().compareTo(freezeAmount) > 0) {
			return error("新冻结资产数量小于当前数值");
		}
		result.setFreezeAmount(freezeAmount);
		
		activityService.save(result);
		
		return success(messageSource.getMessage("SUCCESS"));
	}
	
	/**
	 * 修改活动成交总数
	 * @param id
	 * @param tradedAmount
	 * @return
	 */
	@RequiresPermissions("activity:activity:modify-tradedamount")
    @PostMapping("modify-tradedamount")
    @AccessLog(module = AdminModule.ACTIVITY, operation = "修改活动成交总数Activity")
    public MessageResult alterActivityTradedAmount(
            @RequestParam("id") Long id,
            @RequestParam("tradedAmount") BigDecimal tradedAmount) {
		notNull(id, "validate id!");
		
		Activity result = activityService.findOne(id);
		notNull(result, "validate activity!");
		
		if(result.getTradedAmount().compareTo(tradedAmount) > 0) {
			return error("新冻结资产数量小于当前数值");
		}
		result.setTradedAmount(tradedAmount);
		
		activityService.save(result);
		
		return success(messageSource.getMessage("SUCCESS"));
	}
	
	//Modify
	@RequiresPermissions("activity:activity:modify")
    @PostMapping("modify")
    @AccessLog(module = AdminModule.ACTIVITY, operation = "修改活动信息Activity")
    public MessageResult alterActivity(
            @RequestParam("id") Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "detail", required = false) String detail,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "step", required = false) Integer step,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "totalSupply", required = false) BigDecimal totalSupply,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "priceScale", required = false) Integer priceScale,
            @RequestParam(value = "unit", required = false) String unit,
            @RequestParam(value = "acceptUnit", required = false) String acceptUnit,
            @RequestParam(value = "amountScale", required = false) Integer amountScale,
            @RequestParam(value = "maxLimitAmout", required = false) BigDecimal maxLimitAmout,
            @RequestParam(value = "minLimitAmout", required = false) BigDecimal minLimitAmout,
            @RequestParam(value = "limitTimes", required = false) Integer limitTimes,
            @RequestParam(value = "settings", required = false) String settings,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "smallImageUrl", required = false) String smallImageUrl,
            @RequestParam(value = "bannerImageUrl", required = false) String bannerImageUrl,
            @RequestParam(value = "noticeLink", required = false) String noticeLink,
            @RequestParam(value = "activityLink", required = false) String activityLink,
            @RequestParam(value = "leveloneCount", required = false) Integer leveloneCount,
            @RequestParam(value = "holdLimit", required = false) BigDecimal holdLimit,
            @RequestParam(value = "holdUnit", required = false) String holdUnit,
            @RequestParam(value = "miningDays", required = false) Integer miningDays,
            @RequestParam(value = "miningDaysprofit", required = false) BigDecimal miningDaysprofit,
            @RequestParam(value = "miningUnit", required = false) String miningUnit,
            @RequestParam(value = "miningInvite", required = false) BigDecimal miningInvite,
            @RequestParam(value = "miningInvitelimit", required = false) BigDecimal miningInvitelimit,
            @RequestParam(value = "miningPeriod", required = false) Integer miningPeriod,
			@RequestParam(value = "lockedUnit", required = false) String lockedUnit,
			@RequestParam(value = "lockedPeriod", required = false) Integer lockedPeriod,
			@RequestParam(value = "lockedDays", required = false) Integer lockedDays,
			@RequestParam(value = "releaseType", required = false) Integer releaseType,
			@RequestParam(value = "releasePercent", required = false) BigDecimal releasePercent,
			@RequestParam(value = "lockedFee", required = false) BigDecimal lockedFee,
			@RequestParam(value = "releaseAmount", required = false) BigDecimal releaseAmount,
			@RequestParam(value = "releaseTimes", required = false) BigDecimal releaseTimes,

            @RequestParam(value = "password") String password,
            @SessionAttribute(SysConstant.SESSION_ADMIN) Admin admin) {
		password = Encrypt.MD5(password + md5Key);
        Assert.isTrue(password.equals(admin.getPassword()), messageSource.getMessage("WRONG_PASSWORD"));
        
        Activity result = activityService.findOne(id);
        
        notNull(result, "validate activity!");
        
        if(title != null) result.setTitle(title);
        if(detail != null) result.setDetail(detail);
        if(status != null) result.setStatus(status == 0 ? BooleanEnum.IS_FALSE : BooleanEnum.IS_TRUE);
        if(step != null) result.setStep(step);
        if(type != null) result.setType(type);
        if(startTime != null) result.setStartTime(startTime);
        if(endTime != null) result.setEndTime(endTime);
        if(totalSupply != null) result.setTotalSupply(totalSupply);
        if(price != null) result.setPrice(price);
        if(priceScale != null) result.setPriceScale(priceScale);
        if(unit != null) result.setUnit(unit);
        if(acceptUnit != null) result.setAcceptUnit(acceptUnit);
        if(amountScale != null) result.setAmountScale(amountScale);
        if(maxLimitAmout != null) result.setMaxLimitAmout(maxLimitAmout);
        if(minLimitAmout != null) result.setMinLimitAmout(minLimitAmout);
        if(limitTimes != null) result.setLimitTimes(limitTimes);
        if(settings != null) result.setSettings(settings);
        if(content != null) result.setContent(content);
        if(smallImageUrl != null) result.setSmallImageUrl(smallImageUrl);
        if(bannerImageUrl != null) result.setBannerImageUrl(bannerImageUrl);
        if(noticeLink != null) result.setNoticeLink(noticeLink);
        if(activityLink != null) result.setActivityLink(activityLink);
        if(leveloneCount != null) result.setLeveloneCount(leveloneCount);
        if(holdLimit != null) result.setHoldLimit(holdLimit);
        if(holdUnit != null) result.setHoldUnit(holdUnit);
        if(miningDays != null) result.setMiningDays(miningDays);
        if(miningDaysprofit != null) result.setMiningDaysprofit(miningDaysprofit);
        if(miningUnit != null) result.setMiningUnit(miningUnit);
        if(miningInvite != null) result.setMiningInvite(miningInvite);
        if(miningInvitelimit != null) result.setMiningInvitelimit(miningInvitelimit);
        if(miningPeriod != null) result.setMiningPeriod(miningPeriod);
		if(lockedUnit != null) result.setLockedUnit(lockedUnit);
		if(lockedPeriod != null) result.setLockedPeriod(lockedPeriod);
		if(lockedDays != null) result.setLockedDays(lockedDays);
		if(releaseType != null) result.setReleaseType(releaseType);
		if(releasePercent != null) result.setReleasePercent(releasePercent);
		if(lockedFee != null) result.setLockedFee(lockedFee);
		if(releaseAmount != null) result.setReleaseAmount(releaseAmount);
		if(releaseTimes != null) result.setReleaseTimes(releaseTimes);

        activityService.saveAndFlush(result);
        return success(messageSource.getMessage("SUCCESS"));
	}
	
	@RequiresPermissions("activity:activity:detail")
    @GetMapping("{id}/detail")
    public MessageResult detail(
            @PathVariable Long id) {
        Activity activity = activityService.findById(id);
        Assert.notNull(activity, "validate id!");
        return success(activity);
    }
	
	@RequiresPermissions("activity:activity:orderlist")
    @GetMapping("{aid}/orderlist")
    public MessageResult orderList(
            @PathVariable Long aid) {
        List<ActivityOrder> activityOrderList = activityOrderService.findAllByActivityId(aid);
        Assert.notNull(activityOrderList, "validate id!");
        return success(activityOrderList);
    }
	
	/**
	 * 分发活动币
	 * @return
	 */
	@RequiresPermissions("activity:activity:distribute")
    @PostMapping("distribute")
    @AccessLog(module = AdminModule.ACTIVITY, operation = "分发活动币Activity")
	@Transactional(rollbackFor = Exception.class)
	public MessageResult distribute(@RequestParam("oid") Long oid) {
		ActivityOrder order = activityOrderService.findById(oid);
		if(order == null) {
			return error("订单不存在");
		}
		if(order.getState() != 1) {
			return error("订单状态错误（非待派发状态）！");
		}
		Activity activity = activityService.findById(order.getActivityId());
		if(activity == null) {
			return error("活动不存在");
		}
		// 1、2、3、4类型活动需要判断是否处于派发阶段
		if(activity.getType() == 1 || activity.getType() == 2 || activity.getType() == 3 || activity.getType() == 4) {
			// 活动是否结束
			if(activity.getStep() != 2) {
				return error("活动尚未结束");
			}
		}
		
		// type = 3（持仓瓜分）
		// 持仓瓜分活动详解：用户无需花费任何币就可以瓜分池内的数量的币，按照持仓比例进行瓜分
		if(activity.getType() == 3) {
			// 解冻接受币（acceptUnit）
			MemberWallet freezeWallet = memberWalletService.findByCoinUnitAndMemberId(activity.getAcceptUnit(), order.getMemberId());
			if(freezeWallet == null) {
				return error("冻结币钱包不存在");
			}
			memberWalletService.thawBalance(freezeWallet, order.getFreezeAmount());
			
			// 分发活动币（unit）(获取活动币钱包)
			MemberWallet distributeWallet = memberWalletService.findByCoinUnitAndMemberId(activity.getUnit(), order.getMemberId());

			if(distributeWallet == null) {
				return error("活动币钱包不存在");
			}
			// 分发数量 = 持仓 / 总持仓 * 活动供应量
			BigDecimal disAmount = order.getFreezeAmount().divide(activity.getFreezeAmount()).multiply(activity.getTotalSupply()).setScale(activity.getAmountScale(), BigDecimal.ROUND_HALF_DOWN);
			// 用户钱包增加资产
			memberWalletService.increaseBalance(distributeWallet.getId(), disAmount);

	        MemberTransaction memberTransaction = new MemberTransaction();
	        memberTransaction.setFee(BigDecimal.ZERO);
	        memberTransaction.setAmount(disAmount);
	        memberTransaction.setMemberId(distributeWallet.getMemberId());
	        memberTransaction.setSymbol(activity.getUnit());
	        memberTransaction.setType(TransactionType.ACTIVITY_BUY);
	        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
	        memberTransaction.setRealFee("0");
	        memberTransaction.setDiscountFee("0");
	        memberTransaction= memberTransactionService.save(memberTransaction);
	        
	        Member member = memberService.findOne(order.getMemberId());
			try {
 				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您参与活动兑换的" + disAmount + activity.getUnit() + "已到账！");
 			} catch (Exception e) {
 				return error(e.getMessage());
 			}
			
			// 更新订单状态
			order.setState(2);// 已成交
			order.setAmount(disAmount); //成交数量
			activityOrderService.saveAndFlush(order);
			
			return success("持仓瓜分活动分发完成，分发此单：" + disAmount);
		}
		
		// type = 4（自由认购）
		if(activity.getType() == 4) {
			// 扣除接受币成交
			MemberWallet freezeWallet = memberWalletService.findByCoinUnitAndMemberId(activity.getAcceptUnit(), order.getMemberId());
			if(freezeWallet == null) {
				return error("冻结币钱包不存在");
			}
			memberWalletService.decreaseFrozen(freezeWallet.getId(), order.getTurnover());
			
			MemberTransaction memberTransaction1 = new MemberTransaction();
			memberTransaction1.setFee(BigDecimal.ZERO);
			memberTransaction1.setAmount(order.getTurnover().negate());
			memberTransaction1.setMemberId(freezeWallet.getMemberId());
	        memberTransaction1.setSymbol(activity.getAcceptUnit());
	        memberTransaction1.setType(TransactionType.ACTIVITY_BUY);
	        memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
	        memberTransaction1.setRealFee("0");
	        memberTransaction1.setDiscountFee("0");
	        memberTransaction1 = memberTransactionService.save(memberTransaction1);
	        
			// 分发活动币
			BigDecimal disAmount = order.getAmount();
			MemberWallet distributeWallet = memberWalletService.findByCoinUnitAndMemberId(activity.getUnit(), order.getMemberId());
			if(distributeWallet == null) {
				return error("活动币钱包不存在");
			}
			memberWalletService.increaseBalance(distributeWallet.getId(), disAmount);
			
			MemberTransaction memberTransaction = new MemberTransaction();
	        memberTransaction.setFee(BigDecimal.ZERO);
	        memberTransaction.setAmount(disAmount);
	        memberTransaction.setMemberId(distributeWallet.getMemberId());
	        memberTransaction.setSymbol(activity.getUnit());
	        memberTransaction.setType(TransactionType.ACTIVITY_BUY);
	        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
	        memberTransaction.setRealFee("0");
	        memberTransaction.setDiscountFee("0");
	        memberTransaction= memberTransactionService.save(memberTransaction);
	        
			// 更新订单状态
			order.setState(2);//已成交
			activityOrderService.saveAndFlush(order);
			
			Member member = memberService.findOne(order.getMemberId());
			try {
 				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您参与活动兑换的" + disAmount + activity.getUnit() + "已到账！");
 			} catch (Exception e) {
 				return error(e.getMessage());
 			}
			
			return success("自由认购活动分发完成，分发此单：" + disAmount);
		}
		
		// 云矿机销售类型
		if(activity.getType() == 5) {
			// 扣除接受币成交
			MemberWallet freezeWallet = memberWalletService.findByCoinUnitAndMemberId(activity.getAcceptUnit(), order.getMemberId());
			if(freezeWallet == null) {
				return error("冻结币钱包不存在");
			}
			memberWalletService.decreaseFrozen(freezeWallet.getId(), order.getTurnover());
			
			MemberTransaction memberTransaction1 = new MemberTransaction();
			memberTransaction1.setFee(BigDecimal.ZERO);
			memberTransaction1.setAmount(order.getTurnover().negate());
			memberTransaction1.setMemberId(freezeWallet.getMemberId());
	        memberTransaction1.setSymbol(activity.getAcceptUnit());
	        memberTransaction1.setType(TransactionType.ACTIVITY_BUY);
	        memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
	        memberTransaction1.setRealFee("0");
	        memberTransaction1.setDiscountFee("0");
	        memberTransaction1 = memberTransactionService.save(memberTransaction1);
	        
	        // 更新订单状态
 			order.setState(2);//已成交
 			activityOrderService.saveAndFlush(order);
 			
 			// 生成矿机
 			for(int i = 0; i < order.getAmount().intValue(); i++) {
	 			Date currentDate = DateUtil.getCurrentDate();
	 			MiningOrder mo = new MiningOrder();
	 			mo.setActivityId(activity.getId());
	 			mo.setMemberId(order.getMemberId());
	 			mo.setMiningDays(activity.getMiningDays());
	 			mo.setMiningDaysprofit(activity.getMiningDaysprofit());
	 			mo.setMiningUnit(activity.getMiningUnit());
	 			mo.setCurrentDaysprofit(activity.getMiningDaysprofit());
	 			mo.setCreateTime(currentDate);
	 			mo.setEndTime(DateUtil.dateAddDay(currentDate, activity.getMiningDays()));
	 			mo.setImage(activity.getSmallImageUrl());
	 			mo.setTitle(activity.getTitle());
	 			mo.setMiningStatus(1); //挖矿状态（1：挖矿中）
	 			mo.setMiningedDays(0); //初始为0天
	 			mo.setTotalProfit(BigDecimal.ZERO);
	 			mo.setType(0); // 一般矿机
	 			mo.setMiningInvite(activity.getMiningInvite()); // 邀请
	 			mo.setMiningInvitelimit(activity.getMiningInvitelimit()); // 上限产能
	 			mo.setPeriod(activity.getMiningPeriod()); // 挖矿产出周期
	 			miningOrderService.save(mo);
 			}
 			Member member = memberService.findOne(order.getMemberId());
 			// 邀请是否能增加产能(邀请一人参与仅能为一台矿机增加产能）
 			if(activity.getMiningInvite().compareTo(BigDecimal.ZERO) > 0) {
 				if(member != null) {
 					if(member.getInviterId() != null) {
 						Member inviter = memberService.findOne(member.getInviterId());
 						List<MiningOrder> miningOrders = miningOrderService.findAllByMemberIdAndActivityId(inviter.getId(), activity.getId());
 						if(miningOrders.size() > 0) {
 							for(MiningOrder item : miningOrders) {
 								// 如果当前产能低于极限产能
 								if(item.getCurrentDaysprofit().subtract(item.getMiningDaysprofit()).divide(item.getMiningDaysprofit()).compareTo(activity.getMiningInvitelimit()) < 0) {
 									// 获取新产能
 									BigDecimal newMiningDaysprofit = item.getCurrentDaysprofit().add(item.getMiningDaysprofit().multiply(activity.getMiningInvite()));
 									// 如果新产能比极限产能高
 									if(newMiningDaysprofit.compareTo(item.getMiningDaysprofit().add(item.getMiningDaysprofit().multiply(activity.getMiningInvitelimit()))) > 0) {
 										newMiningDaysprofit = item.getMiningDaysprofit().add(item.getMiningDaysprofit().multiply(activity.getMiningInvitelimit()));
 									}
 									item.setCurrentDaysprofit(newMiningDaysprofit);
 									miningOrderService.save(item);
 									break;
 								}
 							}
 						}
 					}
 				}
 			}
 			
 			try {
 				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您购买的云矿机已部署成功！");
 			} catch (Exception e) {
 				return error(e.getMessage());
			}
			return success("矿机部署成功");
		}

		// 云矿机销售类型
		if(activity.getType() == 6) {
			if(activity.getReleaseTimes().compareTo(BigDecimal.ZERO) <=0  || activity.getReleaseTimes() == null) {
				return error("释放倍数不能为0！");
			}
			// 扣除接受币成交
			MemberWallet freezeWallet = memberWalletService.findByCoinUnitAndMemberId(activity.getAcceptUnit(), order.getMemberId());
			if(freezeWallet == null) {
				return error("冻结币钱包不存在");
			}
			// 扣除了锁仓数量和门槛费
			memberWalletService.decreaseFrozen(freezeWallet.getId(), order.getTurnover());

			MemberTransaction memberTransaction1 = new MemberTransaction();
			memberTransaction1.setFee(BigDecimal.ZERO);
			memberTransaction1.setAmount(order.getTurnover().negate());
			memberTransaction1.setMemberId(freezeWallet.getMemberId());
			memberTransaction1.setSymbol(activity.getAcceptUnit());
			memberTransaction1.setType(TransactionType.ACTIVITY_BUY);
			memberTransaction1.setCreateTime(DateUtil.getCurrentDate());
			memberTransaction1.setRealFee("0");
			memberTransaction1.setDiscountFee("0");
			memberTransaction1 = memberTransactionService.save(memberTransaction1);

			// ToRelease 增加待释放余额( = 用户参与数量 * 释放倍数）
			memberWalletService.increaseToRelease(freezeWallet.getId(), order.getAmount().multiply(activity.getReleaseTimes()));

			// 更新订单状态
			order.setState(2);//已成交
			activityOrderService.saveAndFlush(order);

			// 生成锁仓订单
			Date currentDate = DateUtil.getCurrentDate();
			LockedOrder lo = new LockedOrder();
			lo.setActivityId(activity.getId());
			lo.setMemberId(order.getMemberId());
			lo.setLockedDays(activity.getLockedDays());
			lo.setReleasedDays(0);
			lo.setReleaseUnit(activity.getLockedUnit());
			lo.setReleaseType(activity.getReleaseType());
			lo.setPeriod(activity.getLockedPeriod());
			lo.setLockedStatus(1); // 锁仓状态：释放中
			lo.setReleasePercent(activity.getReleasePercent());
			lo.setReleaseCurrentpercent(activity.getReleasePercent());
			lo.setImage(activity.getSmallImageUrl());
			lo.setTitle(activity.getTitle());
			lo.setTotalLocked(order.getAmount().multiply(activity.getReleaseTimes()));
			lo.setReleaseTimes(activity.getReleaseTimes());
			lo.setOriginReleaseamount(order.getAmount().multiply(activity.getReleaseTimes()).divide(BigDecimal.valueOf(activity.getLockedDays()), 8, BigDecimal.ROUND_HALF_DOWN));
			lo.setCurrentReleaseamount(order.getAmount().multiply(activity.getReleaseTimes()).divide(BigDecimal.valueOf(activity.getLockedDays()), 8, BigDecimal.ROUND_HALF_DOWN));
			lo.setTotalRelease(BigDecimal.ZERO);
			lo.setLockedInvite(activity.getMiningInvite());
			lo.setLockedInvitelimit(activity.getMiningInvitelimit());

			// 以日为周期的释放
			if(activity.getLockedPeriod() == 0) lo.setEndTime(DateUtil.dateAddDay(currentDate, activity.getLockedDays()));
			// 以周为周期的释放
			if(activity.getLockedPeriod() == 1) lo.setEndTime(DateUtil.dateAddDay(currentDate, activity.getLockedDays() * 7));
			// 以月为周期的释放
			if(activity.getLockedPeriod() == 2) lo.setEndTime(DateUtil.dateAddMonth(currentDate, activity.getLockedDays()));
			// 以年为周期的释放
			if(activity.getLockedPeriod() == 3) lo.setEndTime(DateUtil.dateAddYear(currentDate, activity.getLockedDays()));

			lockedOrderService.save(lo);

			Member member = memberService.findOne(order.getMemberId());
			// 邀请是否能增加产能(邀请一人参与仅能为一台矿机增加产能）
			if(activity.getMiningInvite().compareTo(BigDecimal.ZERO) > 0) {
				if(member != null) {
					if(member.getInviterId() != null) {
						Member inviter = memberService.findOne(member.getInviterId());
						List<LockedOrder> lockedOrders = lockedOrderService.findAllByMemberIdAndActivityId(inviter.getId(), activity.getId());
						if(lockedOrders.size() > 0) {
							for(LockedOrder item : lockedOrders) {
								// 如果当前产能低于极限产能
								if(item.getCurrentReleaseamount().subtract(item.getOriginReleaseamount()).divide(item.getOriginReleaseamount()).compareTo(activity.getMiningInvitelimit()) < 0) {
									// 获取新产能
									BigDecimal newReleaseAmount = item.getCurrentReleaseamount().add(item.getCurrentReleaseamount().multiply(activity.getMiningInvite()));
									// 如果新产能比极限产能高
									if(newReleaseAmount.compareTo(item.getOriginReleaseamount().add(item.getOriginReleaseamount().multiply(activity.getMiningInvitelimit()))) > 0) {
										newReleaseAmount = item.getOriginReleaseamount().add(item.getOriginReleaseamount().multiply(activity.getMiningInvitelimit()));
									}
									item.setCurrentReleaseamount(newReleaseAmount);
									lockedOrderService.save(item);
									break;
								}
							}
						}
					}
				}
			}

			try {
				smsProvider.sendCustomMessage(member.getMobilePhone(), "尊敬的用户，您参与的锁仓活动已通过审核！");
			} catch (Exception e) {
				return error(e.getMessage());
			}
			return success("审核成功");
		}

		if(activity.getType() == 6) {

		}
		return error("未知活动类型");
	}

	/**
	 * 管理员手工锁仓用户资产
	 * @param pageModel
	 * @return
	 */
	@RequiresPermissions("activity:activity:lock-member-coin")
	@PostMapping("lock-member-coin")
	@AccessLog(module = AdminModule.ACTIVITY, operation = "分页查看活动列表Activity")
	public MessageResult lockMemberCoin(@RequestParam("memberId") Long memberId,
										@RequestParam("activityId") Long activityId,
										@RequestParam("unit") String unit,
										@RequestParam("amount") BigDecimal amount) {
		// 检查用户是否存在
		Member member = memberService.findOne(memberId);
		org.springframework.util.Assert.notNull(member, "用户!");

		// 检查活动是否存在
		Activity activity = activityService.findOne(activityId);
		org.springframework.util.Assert.notNull(activity, "此活动不存在!");
		// 检查活动是否在进行中
		if(activity.getType() != 6) {
			return MessageResult.error("活动不是锁仓活动！");
		}
		// 检查活动是否在进行中
		if(activity.getStep() != 1) {
			return MessageResult.error("活动不在进行中状态！");
		}

		// 最低起兑量/最低锁仓量
		if(activity.getMinLimitAmout().compareTo(BigDecimal.ZERO) > 0) {
			if(activity.getMinLimitAmout().compareTo(amount) > 0) {
				return MessageResult.error("不能低于最低起兑量！");
			}
		}
		if(activity.getMaxLimitAmout().compareTo(BigDecimal.ZERO) > 0 || activity.getLimitTimes() > 0) {
			// 最高兑换量/最高锁仓量(先获取已下单的量)
			List<ActivityOrder> orderDetailList = activityOrderService.findAllByActivityIdAndMemberId(member.getId(), activityId);
			BigDecimal alreadyAttendAmount = BigDecimal.ZERO;
			int alreadyAttendTimes = 0;
			if(orderDetailList != null) {
				alreadyAttendTimes = orderDetailList.size();
				for(int i = 0; i < orderDetailList.size(); i++) {
					if(activity.getType() == 3) {
						alreadyAttendAmount = alreadyAttendAmount.add(orderDetailList.get(i).getFreezeAmount());
					}else {
						alreadyAttendAmount = alreadyAttendAmount.add(orderDetailList.get(i).getAmount());
					}
				}
			}
			// 最高限购量
			if(activity.getMaxLimitAmout().compareTo(BigDecimal.ZERO) > 0) {
				if(alreadyAttendAmount.add(amount).compareTo(activity.getMaxLimitAmout()) > 0) {
					return MessageResult.error("不能超过最高兑换量！");
				}
			}
			// 个人限购次数
			if(activity.getLimitTimes() > 0) {
				if(activity.getLimitTimes() < alreadyAttendTimes + 1) {
					return MessageResult.error("超过限购次数！");
				}
			}
		}

		// 检查持仓要求
		if(activity.getHoldLimit().compareTo(BigDecimal.ZERO) > 0 && activity.getHoldUnit() != null && activity.getHoldUnit() != "") {
			MemberWallet holdCoinWallet = memberWalletService.findByCoinUnitAndMemberId(activity.getHoldUnit(), member.getId());
			if (holdCoinWallet == null) {
				return MessageResult.error("持仓要求钱包不存在！");
			}
			if(holdCoinWallet.getIsLock().equals(BooleanEnum.IS_TRUE)){
				return MessageResult.error("持仓要求钱包已锁定！");
			}
			if(holdCoinWallet.getBalance().compareTo(activity.getHoldLimit()) < 0) {
				return MessageResult.error("您的" + activity.getHoldUnit() +"持仓数量不满足条件！");
			}
		}

		// 查询币种是否存在
		Coin coin;
		coin = coinService.findByUnit(activity.getAcceptUnit());
		if (coin == null) {
			return MessageResult.error("币种不存在");
		}

		// 检查钱包是否可用
		MemberWallet acceptCoinWallet = memberWalletService.findByCoinUnitAndMemberId(activity.getAcceptUnit(), member.getId());
		if (acceptCoinWallet == null || acceptCoinWallet == null) {
			return MessageResult.error("用户钱包不存在");
		}
		if(acceptCoinWallet.getIsLock().equals(BooleanEnum.IS_TRUE)){
			return MessageResult.error("钱包已锁定");
		}

		// 检查余额是否充足
		BigDecimal totalAcceptCoinAmount = BigDecimal.ZERO;
		totalAcceptCoinAmount = amount.add(activity.getLockedFee()).setScale(activity.getAmountScale(), BigDecimal.ROUND_HALF_DOWN);

		if(acceptCoinWallet.getBalance().compareTo(totalAcceptCoinAmount) < 0){
			return MessageResult.error("用户余额不足");
		}

		ActivityOrder activityOrder = new ActivityOrder();
		activityOrder.setActivityId(activityId);
		activityOrder.setAmount(amount); // 实际锁仓数量
		activityOrder.setFreezeAmount(totalAcceptCoinAmount); // 这里冻结的资产包含了用户实际锁仓数量和门槛费
		activityOrder.setBaseSymbol(activity.getAcceptUnit());
		activityOrder.setCoinSymbol(activity.getUnit());
		activityOrder.setCreateTime(DateUtil.getCurrentDate());
		activityOrder.setMemberId(member.getId());
		activityOrder.setPrice(activity.getPrice());
		activityOrder.setState(1); //未成交
		activityOrder.setTurnover(totalAcceptCoinAmount);//作为资产冻结或扣除资产的标准，锁仓活动中，此项目作为参与数量
		activityOrder.setType(activity.getType());

		MessageResult mr = activityOrderService.saveActivityOrder(member.getId(), activityOrder);

		if (mr.getCode() != 0) {
			return MessageResult.error(500, "活动参与失败:" + mr.getMessage());
		}else {
			return MessageResult.success("锁仓提交成功，请到活动管理同意锁仓！");
		}
	}
}
