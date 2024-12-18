package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.LegalWalletWithdraw;
import com.bizzan.bitrade.entity.LegalWalletWithdrawModel;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.QLegalWalletWithdraw;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.LegalWalletWithdrawService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.BigDecimalUtils;
import com.bizzan.bitrade.util.BindingResultUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.querydsl.core.types.dsl.BooleanExpression;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * 提现
 */
@RestController
@RequestMapping("legal-wallet-withdraw")
public class LegalWalletWithdrawController extends BaseController {
    @Resource
    private LegalWalletWithdrawService legalWalletWithdrawService;
    @Resource
    private MemberService memberService;
    @Resource
    private CoinService coinService;
    @Resource
    private MemberWalletService walletService;

    @GetMapping()
    public MessageResult page(
            PageModel pageModel,
            @RequestParam(value = "state", required = false) WithdrawStatus status,
            @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        BooleanExpression eq = QLegalWalletWithdraw.legalWalletWithdraw.member.id.eq(user.getId());
        if (status != null) {
            eq.and(QLegalWalletWithdraw.legalWalletWithdraw.status.eq(status));
        }
        Page<LegalWalletWithdraw> page = legalWalletWithdrawService.findAll(eq, pageModel);
        return success(page);
    }

    @PostMapping()
    public MessageResult post(
            LegalWalletWithdrawModel model,
            BindingResult bindingResult,
            @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        MessageResult result = BindingResultUtil.validate(bindingResult);
        if (result != null) {
            return result;
        }
        // 合法币种
        Coin coin = coinService.findByUnit(model.getUnit());
        Assert.notNull(coin, "validate coin name!");
        Assert.isTrue(coin.getHasLegal(), "validate coin name!");
        //用户提现的币种钱包
        MemberWallet wallet = walletService.findOneByCoinNameAndMemberId(coin.getName(), user.getId());
        Assert.notNull(wallet, "wallet null!");
        Assert.isTrue(BigDecimalUtils.compare(wallet.getBalance(), model.getAmount()), "insufficient balance!");
        //提现人
        Member member = memberService.findOne(user.getId());
        Assert.notNull(member, "validate login user!");
        //创建 提现
        LegalWalletWithdraw legalWalletWithdraw = new LegalWalletWithdraw();
        legalWalletWithdraw.setMember(member);
        legalWalletWithdraw.setCoin(coin);
        legalWalletWithdraw.setAmount(model.getAmount());
        legalWalletWithdraw.setPayMode(model.getPayMode());
        legalWalletWithdraw.setStatus(WithdrawStatus.PROCESSING);
        legalWalletWithdraw.setRemark(model.getRemark());
        //提现操作
        legalWalletWithdrawService.withdraw(wallet, legalWalletWithdraw);
        return success();
    }

    @GetMapping("{id}")
    public MessageResult detail(
            @PathVariable Long id,
            @SessionAttribute(SESSION_MEMBER) AuthMember user) {
        LegalWalletWithdraw one = legalWalletWithdrawService.findDetailWeb(id, user.getId());
        Assert.notNull(one, "validate id!");
        return success(one);
    }

}
