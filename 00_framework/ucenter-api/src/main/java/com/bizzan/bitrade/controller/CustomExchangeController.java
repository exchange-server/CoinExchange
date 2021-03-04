package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.entity.CtcOrder;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberTransaction;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.MemberTransactionService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.math.BigDecimal;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * 动态兑换币种（前端CExchange）
 */
@RestController
@RequestMapping("cexchange")
public class CustomExchangeController extends BaseController {

    //@Value("${cexchange.rate.usdt:}")
    private BigDecimal usdtRate;

    //@Value("${cexchange.rate.btc:}")
    private BigDecimal btcRate;

    //@Value("${cexchange.rate.eth:}")
    private BigDecimal ethRate;

    //@Value("${cexchange.rate.nextusdt:}")
    private BigDecimal nextUsdtRate;

    //@Value("${cexchange.rate.nextbtc:}")
    private BigDecimal nextBtcRate;

    //@Value("${cexchange.rate.nexteth:}")
    private BigDecimal nextEthRate;

    //@Value("${cexchange.rate.totalsupply:}")
    private BigDecimal totalSupply;

    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberTransactionService transactionService;

    @RequestMapping("get-exchange-rate")
    public MessageResult getRate() {
        JSONObject retObj = new JSONObject();
        retObj.put("usdtRate", usdtRate);
        retObj.put("btcRate", btcRate);
        retObj.put("ethRate", ethRate);
        retObj.put("nextUsdtRate", nextUsdtRate);
        retObj.put("nextBtcRate", nextBtcRate);
        retObj.put("nextEthRate", nextEthRate);
        retObj.put("totalSupply", totalSupply);
        return success(retObj);
    }

    @RequestMapping("do-exchange")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult doExchagne(@SessionAttribute(SESSION_MEMBER) AuthMember member, String baseUnit, BigDecimal amount, BigDecimal price) {
        String targetUnit = "BZB";

        BigDecimal needAmount = BigDecimal.ZERO;
//        if(baseUnit.equals("BTC")) needAmount = amount.divide(btcRate, BigDecimal.ROUND_HALF_DOWN).setScale(8);
//        if(baseUnit.equals("USDT")) needAmount = amount.divide(usdtRate, BigDecimal.ROUND_HALF_DOWN).setScale(4);
//        if(baseUnit.equals("ETH")) needAmount = amount.divide(ethRate, BigDecimal.ROUND_HALF_DOWN).setScale(8);
        needAmount = amount.multiply(price);
        // 检查余额是否足够
        MemberWallet needMW = walletService.findByCoinUnitAndMemberId(baseUnit, member.getId());
        if(needMW.getBalance().compareTo(needAmount) < 0) {
            return error("Balance is not enough");
        }
        // 执行兑换
        MemberWallet targetMW = walletService.findByCoinUnitAndMemberId(targetUnit, member.getId());
        // 增加目标余额
        walletService.increaseBalance(targetMW.getId(), amount);

        // 减少基币余额
        walletService.decreaseFrozen(needMW.getId(), needAmount);

        // 增加资金变更记录（增加）
        MemberTransaction memberTransaction = new MemberTransaction();
        memberTransaction.setFee(BigDecimal.ZERO);
        memberTransaction.setAmount(amount);
        memberTransaction.setMemberId(member.getId());
        memberTransaction.setSymbol(targetUnit);
        memberTransaction.setType(TransactionType.ACTIVITY_BUY);
        memberTransaction.setCreateTime(DateUtil.getCurrentDate());
        memberTransaction.setRealFee("0");
        memberTransaction.setDiscountFee("0");
        memberTransaction= transactionService.save(memberTransaction);

        // 增加资金变更记录（减少记录）
        MemberTransaction memberTransactionOut = new MemberTransaction();
        memberTransactionOut.setFee(BigDecimal.ZERO);
        memberTransactionOut.setAmount(needAmount);
        memberTransactionOut.setMemberId(member.getId());
        memberTransactionOut.setSymbol(baseUnit);
        memberTransactionOut.setType(TransactionType.ACTIVITY_BUY);
        memberTransactionOut.setCreateTime(DateUtil.getCurrentDate());
        memberTransactionOut.setRealFee("0");
        memberTransactionOut.setDiscountFee("0");
        memberTransactionOut= transactionService.save(memberTransactionOut);

        return success("Exchange successful");
    }
}
