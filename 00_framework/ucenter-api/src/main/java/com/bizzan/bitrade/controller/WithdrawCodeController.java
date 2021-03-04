package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.constant.*;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.service.*;
import com.bizzan.bitrade.util.Md5;
import com.bizzan.bitrade.util.MessageResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;
import static com.bizzan.bitrade.util.BigDecimalUtils.compare;
import static com.bizzan.bitrade.util.BigDecimalUtils.sub;
import static com.bizzan.bitrade.util.MessageResult.error;
import static org.springframework.util.Assert.*;

/**
 * @author Jammy
 * @date 2020年01月26日
 */
@RestController
@Slf4j
@RequestMapping(value = "/withdrawcode", method = RequestMethod.POST)
public class WithdrawCodeController {
    @Autowired
    private MemberAddressService memberAddressService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MemberService memberService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private WithdrawCodeRecordService withdrawApplyService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private LocaleMessageSourceService sourceService;
    @Autowired
    private MemberTransactionService memberTransactionService ;

    /**
     * 支持提现的地址
     * @return
     */
    @RequestMapping("support/coin")
    public MessageResult queryWithdraw() {
        List<Coin> list = coinService.findAllCanWithDraw();
        List<String> list1 = new ArrayList<>();
        list.stream().forEach(x -> list1.add(x.getUnit()));
        MessageResult result = MessageResult.success();
        result.setData(list1);
        return result;
    }

    /**
     * 提现币种详细信息
     * @param user
     * @return
     */
    @RequestMapping("support/coin/info")
    public MessageResult queryWithdrawCoin(@SessionAttribute(SESSION_MEMBER) AuthMember user) {
        List<Coin> list = coinService.findAllCanWithDraw();
        List<MemberWallet> list1 = memberWalletService.findAllByMemberId(user.getId());
        long id = user.getId();
        List<WithdrawWalletInfo> list2 = list1.stream().filter(x -> list.contains(x.getCoin())).map(x ->
                WithdrawWalletInfo.builder()
                        .balance(x.getBalance())
                        .withdrawScale(x.getCoin().getWithdrawScale())
                        .maxTxFee(x.getCoin().getMaxTxFee())
                        .minTxFee(x.getCoin().getMinTxFee())
                        .minAmount(x.getCoin().getMinWithdrawAmount())
                        .maxAmount(x.getCoin().getMaxWithdrawAmount())
                        .name(x.getCoin().getName())
                        .nameCn(x.getCoin().getNameCn())
                        .threshold(x.getCoin().getWithdrawThreshold())
                        .unit(x.getCoin().getUnit())
                        .accountType(x.getCoin().getAccountType())
                        .canAutoWithdraw(x.getCoin().getCanAutoWithdraw())
                        .addresses(memberAddressService.queryAddress(id, x.getCoin().getName())).build()
        ).collect(Collectors.toList());
        MessageResult result = MessageResult.success();
        result.setData(list2);
        return result;
    }

    /**
     * 申请提币（添加验证码校验）
     * @param user
     * @param unit 币种单位
     * @param amount 提币数量
     * @param jyPassword  交易密码
     * @return
     * @throws Exception
     */
    @RequestMapping("apply/code")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdrawCode(@SessionAttribute(SESSION_MEMBER) AuthMember user, String unit, 
                                  BigDecimal amount, String jyPassword) throws Exception {
        
    	hasText(jyPassword, sourceService.getMessage("MISSING_JYPASSWORD"));
        hasText(unit, sourceService.getMessage("MISSING_COIN_TYPE"));
        Coin coin = coinService.findByUnit(unit);
        amount.setScale(coin.getWithdrawScale(),BigDecimal.ROUND_DOWN);
        notNull(coin, sourceService.getMessage("COIN_ILLEGAL"));

        isTrue(coin.getStatus().equals(CommonStatus.NORMAL) && coin.getCanWithdraw().equals(BooleanEnum.IS_TRUE), sourceService.getMessage("COIN_NOT_SUPPORT"));
        isTrue(compare(coin.getMaxWithdrawAmount(), amount), sourceService.getMessage("WITHDRAW_MAX") + coin.getMaxWithdrawAmount());
        isTrue(compare(amount, coin.getMinWithdrawAmount()), sourceService.getMessage("WITHDRAW_MIN") + coin.getMinWithdrawAmount());
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(coin, user.getId());
        isTrue(compare(memberWallet.getBalance(), amount), sourceService.getMessage("INSUFFICIENT_BALANCE"));
//        isTrue(memberAddressService.findByMemberIdAndAddress(user.getId(), address).size() > 0, sourceService.getMessage("WRONG_ADDRESS"));
        isTrue(memberWallet.getIsLock()==BooleanEnum.IS_FALSE,"钱包已锁定");
        Member member = memberService.findOne(user.getId());
        String mbPassword = member.getJyPassword();
        Assert.hasText(mbPassword, sourceService.getMessage("NO_SET_JYPASSWORD"));
        Assert.isTrue(Md5.md5Digest(jyPassword + member.getSalt()).toLowerCase().equals(mbPassword), sourceService.getMessage("ERROR_JYPASSWORD"));

        // 冻结用户资产
        MessageResult result = memberWalletService.freezeBalance(memberWallet, amount);
        if (result.getCode() != 0) {
            throw new InformationExpiredException("Information Expired");
        }
        // 生成提现码记录
        WithdrawCodeRecord withdrawApply = new WithdrawCodeRecord();
        withdrawApply.setCoin(coin);
        withdrawApply.setMemberId(user.getId());
        withdrawApply.setWithdrawAmount(amount);
        withdrawApply.setStatus(WithdrawStatus.PROCESSING);
        // 生成提现码(MD5)
        String withdrawCode = Md5.md5Digest(System.currentTimeMillis() + Math.random() + "");
        withdrawApply.setWithdrawCode(withdrawCode);

        if (withdrawApplyService.save(withdrawApply) != null) {
        	MessageResult mr = new MessageResult(0, "success");
        	mr.setData(withdrawApply);
            return mr;
        } else {
            throw new InformationExpiredException("Information Expired");
        }
    }
    
    /**
     * 提币码充值
     * @param user
     * @param withdrawCode 提币码
     * @param withdrawCode
     * @return
     * @throws Exception
     */
    @RequestMapping("apply/recharge")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult withdrawCodeRecharge(@SessionAttribute(SESSION_MEMBER) AuthMember user, String withdrawCode) throws Exception {
        
    	hasText(withdrawCode, sourceService.getMessage("MISSING_JYPASSWORD"));
        Member member = memberService.findOne(user.getId());
        
        WithdrawCodeRecord record = withdrawApplyService.findByWithdrawCode(withdrawCode);
        
        if(record != null) {
        	if(record.getStatus() != WithdrawStatus.PROCESSING) {
        		return MessageResult.error("该充值码已被使用或删除！");
        	}
        	withdrawApplyService.withdrawSuccess(record.getId(), user.getId());
        	return MessageResult.success("充值码充值成功！");
        }else {
        	return MessageResult.error("充值码不存在！");
        }
    }

    /**
     * 获取提币码信息
     * @param user
     * @param withdrawCode 提币码
     * @param withdrawCode
     * @return
     * @throws Exception
     */
    @RequestMapping("apply/info")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult getWithdrawCodeInfo(@SessionAttribute(SESSION_MEMBER) AuthMember user, String withdrawCode) throws Exception {

        hasText(withdrawCode, sourceService.getMessage("MISSING_JYPASSWORD"));
        Member member = memberService.findOne(user.getId());

        WithdrawCodeRecord record = withdrawApplyService.findByWithdrawCode(withdrawCode);

        if(record != null) {
            MessageResult ret = new MessageResult(0, "获取成功！");
            ret.setData(record);
            return ret;
        }else {
            return MessageResult.error("充值码不存在！");
        }
    }

    /**
     * 提币码记录
     * @param user
     * @param page 页码
     * @param pageSize 数量
     * @return
     */
    @GetMapping("record")
    public MessageResult pageWithdraw(@SessionAttribute(SESSION_MEMBER) AuthMember user, int page, int pageSize) {
        MessageResult mr = new MessageResult(0, "success");
        Page<WithdrawCodeRecord> records = withdrawApplyService.findAllByMemberId(user.getId(), page, pageSize);
        
        mr.setData(records);
        return mr;
    }

}
