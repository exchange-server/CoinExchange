package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.entity.ExchangeCoinPublishType;
import com.bizzan.bitrade.entity.ExchangeOrder;
import com.bizzan.bitrade.entity.ExchangeOrderDetail;
import com.bizzan.bitrade.entity.ExchangeOrderDirection;
import com.bizzan.bitrade.entity.ExchangeOrderStatus;
import com.bizzan.bitrade.entity.ExchangeOrderType;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.ExchangeOrderDetailService;
import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * 委托订单处理类
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private ExchangeOrderService orderService;
    @Resource
    private MemberWalletService walletService;
    @Resource
    private ExchangeCoinService exchangeCoinService;
    @Resource
    private CoinService coinService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private ExchangeOrderDetailService exchangeOrderDetailService;
    @Value("${exchange.max-cancel-times:-1}")
    private int maxCancelTimes;
    @Resource
    private LocaleMessageSourceService msService;
    @Resource
    private MemberService memberService;
    @Resource
    private RestTemplate restTemplate;
    @Resource
    private RedisTemplate redisTemplate;
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 添加委托订单
     *
     * @param authMember
     * @param direction
     * @param symbol
     * @param price
     * @param amount
     * @param type       usedisCount 暂时不用
     * @return
     */
    @RequestMapping("add")
    public MessageResult addOrder(@SessionAttribute(SESSION_MEMBER) AuthMember authMember,
                                  ExchangeOrderDirection direction, String symbol, BigDecimal price,
                                  BigDecimal amount, ExchangeOrderType type) {
//        int expireTime = SysConstant.USER_ADD_EXCHANGE_ORDER_TIME_LIMIT_EXPIRE_TIME;
//        ValueOperations valueOperations =  redisTemplate.opsForValue();
        if (direction == null || type == null) {
            return MessageResult.error(500, msService.getMessage("ILLEGAL_ARGUMENT"));
        }
        Member member = memberService.findOne(authMember.getId());

//        if(member.getMemberLevel()== MemberLevelEnum.GENERAL){
//            return MessageResult.error(500,msService.getMessage("REAL_NAME_AUTHENTICATION"));
//        }

        //是否被禁止交易
        if (member.getTransactionStatus().equals(BooleanEnum.IS_FALSE)) {
            return MessageResult.error(500, msService.getMessage("CANNOT_TRADE"));
        }
        ExchangeOrder order = new ExchangeOrder();
        //判断限价输入值是否小于零
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        //判断数量小于零
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        //根据交易对名称（symbol）获取交易对儿信息
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        if (exchangeCoin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if (exchangeCoin.getEnable() != 1 || exchangeCoin.getExchangeable() != 1) {
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }
        // 不允许卖
        if (exchangeCoin.getEnableSell() == BooleanEnum.IS_FALSE && direction == ExchangeOrderDirection.SELL) {
            return MessageResult.error(500, msService.getMessage("STOP_SELLING"));
        }

        // 不允许买
        if (exchangeCoin.getEnableBuy() == BooleanEnum.IS_FALSE && direction == ExchangeOrderDirection.BUY) {
            return MessageResult.error(500, msService.getMessage("STOP_BUYING"));
        }

        //获取基准币
        String baseCoin = exchangeCoin.getBaseSymbol();
        //获取交易币
        String exCoin = exchangeCoin.getCoinSymbol();
        Coin coin;
        //根据交易方向查询币种信息
        if (direction == ExchangeOrderDirection.SELL) {
            coin = coinService.findByUnit(exCoin);
        } else {
            coin = coinService.findByUnit(baseCoin);
        }
        if (coin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        //设置价格精度
        price = price.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
        //委托数量和精度控制
        if (direction == ExchangeOrderDirection.BUY && type == ExchangeOrderType.MARKET_PRICE) {
            amount = amount.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
            //最小成交额控制
            if (amount.compareTo(exchangeCoin.getMinTurnover()) < 0) {
                return MessageResult.error(500, msService.getMessage("LOWEST_TURNOVER") + exchangeCoin.getMinTurnover());
            }
        } else {
            amount = amount.setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_DOWN);
            //成交量范围控制
            if (exchangeCoin.getMaxVolume() != null && exchangeCoin.getMaxVolume().compareTo(BigDecimal.ZERO) != 0
                    && exchangeCoin.getMaxVolume().compareTo(amount) < 0) {
                return MessageResult.error(msService.getMessage("AMOUNT_OVER_SIZE") + " " + exchangeCoin.getMaxVolume());
            }
            if (exchangeCoin.getMinVolume() != null && exchangeCoin.getMinVolume().compareTo(BigDecimal.ZERO) != 0
                    && exchangeCoin.getMinVolume().compareTo(amount) > 0) {
                return MessageResult.error(msService.getMessage("AMOUNT_TOO_SMALL") + " " + exchangeCoin.getMinVolume());
            }
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        MemberWallet baseCoinWallet = walletService.findByCoinUnitAndMemberId(baseCoin, member.getId());
        MemberWallet exCoinWallet = walletService.findByCoinUnitAndMemberId(exCoin, member.getId());
        if (baseCoinWallet == null || exCoinWallet == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if (baseCoinWallet.getIsLock() == BooleanEnum.IS_TRUE || exCoinWallet.getIsLock() == BooleanEnum.IS_TRUE) {
            return MessageResult.error(500, msService.getMessage("WALLET_LOCKED"));
        }
        //如果有最低卖价限制，出价不能低于此价,且禁止市场价格卖
        if (direction == ExchangeOrderDirection.SELL && exchangeCoin.getMinSellPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMinSellPrice()) < 0) || type == ExchangeOrderType.MARKET_PRICE)) {
            return MessageResult.error(500, msService.getMessage("FLOOR_PRICE") + exchangeCoin.getMinSellPrice());
        }
        // 如果有最高买价限制，出价不能高于此价，且禁止市场价格买
        if (direction == ExchangeOrderDirection.BUY && exchangeCoin.getMaxBuyPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMaxBuyPrice()) > 0) || type == ExchangeOrderType.MARKET_PRICE)) {
            return MessageResult.error(500, msService.getMessage("PRICE_CEILING") + exchangeCoin.getMaxBuyPrice());
        }
        //查看是否启用市价买卖
        if (type == ExchangeOrderType.MARKET_PRICE) {
            if (exchangeCoin.getEnableMarketBuy() == BooleanEnum.IS_FALSE && direction == ExchangeOrderDirection.BUY) {
                return MessageResult.error(500, msService.getMessage("NO_MARKET_PRICE_BUY"));
            } else if (exchangeCoin.getEnableMarketSell() == BooleanEnum.IS_FALSE && direction == ExchangeOrderDirection.SELL) {
                return MessageResult.error(500, msService.getMessage("NO_MARKET_PRICE_SELL"));
            }
        }
        //限制委托数量
        if (exchangeCoin.getMaxTradingOrder() > 0 && orderService.findCurrentTradingCount(member.getId(), symbol, direction) >= exchangeCoin.getMaxTradingOrder()) {
            return MessageResult.error(500, msService.getMessage("MAXIMUM_QUANTITY") + exchangeCoin.getMaxTradingOrder());
        }

        // 抢购模式活动订单限制（用户无法在活动前下买单）
        long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
        // 抢购模式下，无法在活动开始前下买单，仅限于管理员下卖单
        if (exchangeCoin.getPublishType() == ExchangeCoinPublishType.QIANGGOU) {
            // 抢购模式订单
            try {
                if (currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                    if (direction == ExchangeOrderDirection.BUY) {
                        // 抢购未开始
                        if (currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                            return MessageResult.error(500, msService.getMessage("ACTIVITY_NOT_STARTED"));
                        }
                    } else {
                        // 此处2是管理员用户的ID
                        if (member.getId() != 2) {
                            return MessageResult.error(500, msService.getMessage("UNABLE_TO_PLACE_BUY_ORDER"));
                        }
                    }
                } else {
                    // 活动进行期间，无法下卖单 + 无法下市价单
                    if (currentTime < dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime()) {
                        if (direction == ExchangeOrderDirection.SELL) {
                            return MessageResult.error(500, msService.getMessage("UNABLE_TO_PLACE_SELL_ORDER"));
                        }
                        if (type == ExchangeOrderType.MARKET_PRICE) {
                            return MessageResult.error(500, msService.getMessage("ITS_NOT_MARKETABLE"));
                        }
                    } else {
                        // 清盘期间，无法下单
                        if (currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                            return MessageResult.error(500, msService.getMessage("WINDING_UP"));
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500, msService.getMessage("EXAPI_UNKNOWN_ERROR0"));
            }
        }
        // 分摊模式活动订单限制(开始前任何人无法下单)
        if (exchangeCoin.getPublishType() == ExchangeCoinPublishType.FENTAN) {
            try {

                if (currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                    // UNABLE_TO_PLACE_BUY_ORDER
                    return MessageResult.error(500, msService.getMessage("ACTIVITY_NOT_STARTED"));
                } else {
                    // 活动开始后且在结束前，无法下卖单 + 下单金额必须符合规定
                    if (currentTime < dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime()) {
                        if (direction == ExchangeOrderDirection.SELL) {
                            return MessageResult.error(500, msService.getMessage("ACTIVITY_STARTED_CANT_SELL"));
                        } else {
                            if (type == ExchangeOrderType.MARKET_PRICE) {
                                return MessageResult.error(500, msService.getMessage("ITS_NOT_MARKETABLE"));
                            } else {
                                if (price.compareTo(exchangeCoin.getPublishPrice()) != 0) {
                                    return MessageResult.error(500, msService.getMessage("ORDER_PRICE") + exchangeCoin.getPublishPrice());
                                }
                            }
                        }
                    } else {
                        // 清盘期间，普通用户无法下单，仅有管理员用户ID可下单
                        if (currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                            // 此处2和10001是管理员用户的ID
                            if (member.getId() != 2 && member.getId() != 10001) {
                                return MessageResult.error(500, msService.getMessage("WINDING_UP"));
                            } else {
                                if (price.compareTo(exchangeCoin.getPublishPrice()) != 0) {
                                    return MessageResult.error(500, msService.getMessage("ORDER_PRICE") + exchangeCoin.getPublishPrice());
                                }
                                if (direction == ExchangeOrderDirection.BUY) {
                                    return MessageResult.error(500, msService.getMessage("PERIOD_LIQUIDATION"));
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500, msService.getMessage("EXAPI_UNKNOWN_ERROR1"));
            }
        }
        order.setMemberId(member.getId());
        order.setSymbol(symbol);
        order.setBaseSymbol(baseCoin);
        order.setCoinSymbol(exCoin);
        order.setType(type);
        order.setDirection(direction);
        if (order.getType() == ExchangeOrderType.MARKET_PRICE) {
            order.setPrice(BigDecimal.ZERO);
        } else {
            order.setPrice(price);
        }
        order.setUseDiscount("0");
        //限价买入单时amount为用户设置的总成交额
        order.setAmount(amount);

        MessageResult mr = orderService.addOrder(member.getId(), order);
        if (mr.getCode() != 0) {
            return MessageResult.error(500, msService.getMessage("ORDER_FAILED") + mr.getMessage());
        }
        log.info(">>>>>>>>>>订单提交完成>>>>>>>>>>");
        // 发送消息至Exchange系统
        kafkaTemplate.send("exchange-order", JSON.toJSONString(order));
        MessageResult result = MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
        result.setData(order.getOrderId());
        return result;
    }


    /**
     * 行情机器人专用：添加委托订单
     *
     * @param uid
     * @param direction
     * @param symbol
     * @param price
     * @param amount
     * @param type      usedisCount 暂时不用
     * @return
     */
    @RequestMapping("mockaddydhdnskd")
    public MessageResult addOrderMock(Long uid, String sign,
                                      ExchangeOrderDirection direction, String symbol, BigDecimal price,
                                      BigDecimal amount, ExchangeOrderType type) {
        //int expireTime = SysConstant.USER_ADD_EXCHANGE_ORDER_TIME_LIMIT_EXPIRE_TIME;
        //ValueOperations valueOperations =  redisTemplate.opsForValue();
        if (direction == null || type == null) {
            return MessageResult.error(500, msService.getMessage("ILLEGAL_ARGUMENT"));
        }

//        Member member=memberService.findOne(uid);
//        if(member.getMemberLevel()== MemberLevelEnum.GENERAL){
//            return MessageResult.error(500,msService.getMessage("REAL_NAME_AUTHENTICATION"));
//        }

        if (uid != 1 && uid != 10001) {
            return MessageResult.error(500, msService.getMessage("ILLEGAL_ARGUMENT"));
        }
        if (!sign.equals("77585211314qazwsx")) {
            return MessageResult.error(500, msService.getMessage("ILLEGAL_ARGUMENT"));
        }

//        //是否被禁止交易
//        if(member.getTransactionStatus().equals(BooleanEnum.IS_FALSE)){
//            return MessageResult.error(500,msService.getMessage("CANNOT_TRADE"));
//        }

        ExchangeOrder order = new ExchangeOrder();
        //判断限价输入值是否小于零
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        //判断数量小于零
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        //根据交易对名称（symbol）获取交易对儿信息
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        if (exchangeCoin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if (exchangeCoin.getEnable() != 1 || exchangeCoin.getExchangeable() != 1) {
            return MessageResult.error(500, msService.getMessage("COIN_FORBIDDEN"));
        }

        //获取基准币
        String baseCoin = exchangeCoin.getBaseSymbol();
        //获取交易币
        String exCoin = exchangeCoin.getCoinSymbol();
        Coin coin;
        //根据交易方向查询币种信息
        if (direction == ExchangeOrderDirection.SELL) {
            coin = coinService.findByUnit(exCoin);
        } else {
            coin = coinService.findByUnit(baseCoin);
        }
        if (coin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        //设置价格精度
        price = price.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
        //委托数量和精度控制
        if (direction == ExchangeOrderDirection.BUY && type == ExchangeOrderType.MARKET_PRICE) {
            amount = amount.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
            //最小成交额控制
            if (amount.compareTo(exchangeCoin.getMinTurnover()) < 0) {
                return MessageResult.error(500, msService.getMessage("MINIMUM_TURNOVER") + exchangeCoin.getMinTurnover());
            }
        } else {
            amount = amount.setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_DOWN);
            //成交量范围控制
            if (exchangeCoin.getMaxVolume() != null && exchangeCoin.getMaxVolume().compareTo(BigDecimal.ZERO) != 0
                    && exchangeCoin.getMaxVolume().compareTo(amount) < 0) {
                return MessageResult.error(msService.getMessage("AMOUNT_OVER_SIZE") + " " + exchangeCoin.getMaxVolume());
            }
            if (exchangeCoin.getMinVolume() != null && exchangeCoin.getMinVolume().compareTo(BigDecimal.ZERO) != 0
                    && exchangeCoin.getMinVolume().compareTo(amount) > 0) {
                return MessageResult.error(msService.getMessage("AMOUNT_TOO_SMALL") + " " + exchangeCoin.getMinVolume());
            }
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }

//        MemberWallet baseCoinWallet = walletService.findByCoinUnitAndMemberId(baseCoin, member.getId());
//        MemberWallet exCoinWallet = walletService.findByCoinUnitAndMemberId(exCoin, member.getId());
//        if (baseCoinWallet == null || exCoinWallet == null) {
//            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
//        }
//        if (baseCoinWallet.getIsLock() == BooleanEnum.IS_TRUE || exCoinWallet.getIsLock() == BooleanEnum.IS_TRUE) {
//            return MessageResult.error(500, msService.getMessage("WALLET_LOCKED"));
//        }

        // 如果有最低卖价限制，出价不能低于此价,且禁止市场价格卖
        if (direction == ExchangeOrderDirection.SELL && exchangeCoin.getMinSellPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMinSellPrice()) < 0) || type == ExchangeOrderType.MARKET_PRICE)) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        // 如果有最高买价限制，出价不能高于此价，且禁止市场价格买
        if (direction == ExchangeOrderDirection.BUY && exchangeCoin.getMaxBuyPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMaxBuyPrice()) > 0) || type == ExchangeOrderType.MARKET_PRICE)) {
            return MessageResult.error(500, msService.getMessage("NO_PRICE_CEILING"));
        }
        //查看是否启用市价买卖
        if (type == ExchangeOrderType.MARKET_PRICE) {
            if (exchangeCoin.getEnableMarketBuy() == BooleanEnum.IS_FALSE && direction == ExchangeOrderDirection.BUY) {
                return MessageResult.error(500, msService.getMessage("NO_MARKET_PRICE_BUY"));
            } else if (exchangeCoin.getEnableMarketSell() == BooleanEnum.IS_FALSE && direction == ExchangeOrderDirection.SELL) {
                return MessageResult.error(500, msService.getMessage("NO_MARKET_PRICE_SELL"));
            }
        }

//        //限制委托数量
//        if (exchangeCoin.getMaxTradingOrder() > 0 && orderService.findCurrentTradingCount(uid, symbol, direction) >= exchangeCoin.getMaxTradingOrder()) {
//            return MessageResult.error(500, msService.getMessage("MAXIMUM_QUANTITY") + exchangeCoin.getMaxTradingOrder());
//        }

        // 抢购模式活动订单限制（用户无法在活动前下买单）
        long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
        // 抢购模式下，无法在活动开始前下买单，仅限于管理员下卖单
        if (exchangeCoin.getPublishType() == ExchangeCoinPublishType.QIANGGOU) {
            // 抢购模式订单
            try {
                if (currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                    if (direction == ExchangeOrderDirection.BUY) {
                        // 抢购未开始
                        if (currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                            return MessageResult.error(500, msService.getMessage("ACTIVITY_NOT_STARTED"));
                        }
                    } else {
                        // 此处2是管理员用户的ID
                        if (uid != 2 && uid != 1 && uid != 10001) {
                            return MessageResult.error(500, msService.getMessage("UNABLE_TO_PLACE_BUY_ORDER"));
                        }
                    }
                } else {
                    // 活动进行期间，无法下卖单 + 无法下市价单
                    if (currentTime < dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime()) {
                        if (direction == ExchangeOrderDirection.SELL) {
                            return MessageResult.error(500, msService.getMessage("UNABLE_TO_PLACE_SELL_ORDER"));
                        }
                        if (type == ExchangeOrderType.MARKET_PRICE) {
                            return MessageResult.error(500, msService.getMessage("ITS_NOT_MARKETABLE"));
                        }
                    } else {
                        // 清盘期间，无法下单
                        if (currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                            return MessageResult.error(500, msService.getMessage("WINDING_UP"));
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500, msService.getMessage("EXAPI_UNKNOWN_ERROR0"));
            }
        }
        // 分摊模式活动订单限制(开始前任何人无法下单)
        if (exchangeCoin.getPublishType() == ExchangeCoinPublishType.FENTAN) {
            try {

                if (currentTime < dateTimeFormat.parse(exchangeCoin.getStartTime()).getTime()) {
                    // UNABLE_TO_PLACE_BUY_ORDER
                    return MessageResult.error(500, msService.getMessage("ACTIVITY_NOT_STARTED"));
                } else {
                    // 活动开始后且在结束前，无法下卖单 + 下单金额必须符合规定
                    if (currentTime < dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime()) {
                        if (direction == ExchangeOrderDirection.SELL) {
                            return MessageResult.error(500, msService.getMessage("ACTIVITY_STARTED_CANT_SELL"));
                        } else {
                            if (type == ExchangeOrderType.MARKET_PRICE) {
                                return MessageResult.error(500, msService.getMessage("ITS_NOT_MARKETABLE"));
                            } else {
                                if (price.compareTo(exchangeCoin.getPublishPrice()) != 0) {
                                    return MessageResult.error(500, msService.getMessage("ORDER_PRICE") + exchangeCoin.getPublishPrice());
                                }
                            }
                        }
                    } else {
                        // 清盘期间，普通用户无法下单，仅有管理员用户ID可下单
                        if (currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                            // 此处2是超级管理员用户的ID
                            if (uid != 2 && uid != 1 && uid != 10001) {
                                return MessageResult.error(500, msService.getMessage("WINDING_UP"));
                            } else {
                                if (price.compareTo(exchangeCoin.getPublishPrice()) != 0) {
                                    return MessageResult.error(500, msService.getMessage("ORDER_PRICE") + exchangeCoin.getPublishPrice());
                                }
                                if (direction == ExchangeOrderDirection.BUY) {
                                    return MessageResult.error(500, msService.getMessage("PERIOD_LIQUIDATION"));
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500, msService.getMessage("EXAPI_UNKNOWN_ERROR1"));
            }
        }
        order.setMemberId(uid);
        order.setSymbol(symbol);
        order.setBaseSymbol(baseCoin);
        order.setCoinSymbol(exCoin);
        order.setType(type);
        order.setDirection(direction);
        if (order.getType() == ExchangeOrderType.MARKET_PRICE) {
            order.setPrice(BigDecimal.ZERO);
        } else {
            order.setPrice(price);
        }
        order.setUseDiscount("0");
        //限价买入单时amount为用户设置的总成交额
        order.setAmount(amount);

        MessageResult mr = orderService.addOrder(uid, order);
        if (mr.getCode() != 0) {
            return MessageResult.error(500, msService.getMessage("ORDER_FAILED") + mr.getMessage());
        }
        log.info(">>>>>>>>>>订单提交完成>>>>>>>>>>");
        // 发送消息至Exchange系统
        kafkaTemplate.send("exchange-order", JSON.toJSONString(order));
        MessageResult result = MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
        result.setData(order.getOrderId());
        return result;
    }

    /**
     * 历史委托
     */
    @RequestMapping("history")
    public Page<ExchangeOrder> historyOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member, String symbol, int pageNo, int pageSize) {
        Page<ExchangeOrder> page = orderService.findHistory(member.getId(), symbol, pageNo, pageSize);
        /*
        page.getContent().forEach(exchangeOrder -> {
            //获取交易成交详情
            exchangeOrder.setDetail(exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId()));
        });
        */
        return page;
    }

    /**
     * 个人中心历史委托
     */
    @RequestMapping("personal/history")
    public Page<ExchangeOrder> personalHistoryOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member,
                                                    @RequestParam(value = "symbol", required = false) String symbol,
                                                    @RequestParam(value = "type", required = false) ExchangeOrderType type,
                                                    @RequestParam(value = "status", required = false) ExchangeOrderStatus status,
                                                    @RequestParam(value = "startTime", required = false) String startTime,
                                                    @RequestParam(value = "endTime", required = false) String endTime,
                                                    @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction,
                                                    @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        Page<ExchangeOrder> page = orderService.findPersonalHistory(member.getId(), symbol, type, status, startTime, endTime, direction, pageNo, pageSize);
        /*
        page.getContent().forEach(exchangeOrder -> {
            //获取交易成交详情
            exchangeOrder.setDetail(exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId()));
        });
        */
        return page;
    }


    /**
     * 个人中心当前委托
     *
     * @param member
     * @param symbol
     * @param type
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("personal/current")
    public Page<ExchangeOrder> personalCurrentOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member,
                                                    @RequestParam(value = "symbol", required = false) String symbol,
                                                    @RequestParam(value = "type", required = false) ExchangeOrderType type,
                                                    @RequestParam(value = "startTime", required = false) String startTime,
                                                    @RequestParam(value = "endTime", required = false) String endTime,
                                                    @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction,
                                                    @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        Page<ExchangeOrder> page = orderService.findPersonalCurrent(member.getId(), symbol, type, startTime, endTime, direction, pageNo, pageSize);
        page.getContent().forEach(exchangeOrder -> {
            //获取交易成交详情
            BigDecimal tradedAmount = BigDecimal.ZERO;
            BigDecimal turnover = BigDecimal.ZERO;
            List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId());
            exchangeOrder.setDetail(details);
            for (ExchangeOrderDetail trade : details) {
                tradedAmount = tradedAmount.add(trade.getAmount());
                turnover = turnover.add(trade.getTurnover());
            }
            exchangeOrder.setTradedAmount(tradedAmount);
            exchangeOrder.setTurnover(turnover);
        });
        return page;
    }

    /**
     * 当前委托
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("current")
    public Page<ExchangeOrder> currentOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member, String symbol, int pageNo, int pageSize) {
        Page<ExchangeOrder> page = orderService.findCurrent(member.getId(), symbol, pageNo, pageSize);
        page.getContent().forEach(exchangeOrder -> {
            //获取交易成交详情
            BigDecimal tradedAmount = BigDecimal.ZERO;
            BigDecimal turnover = BigDecimal.ZERO;
            List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId());
            exchangeOrder.setDetail(details);
            for (ExchangeOrderDetail trade : details) {
                tradedAmount = tradedAmount.add(trade.getAmount());
                turnover = turnover.add(trade.getTurnover());
            }
            exchangeOrder.setTradedAmount(tradedAmount);
            exchangeOrder.setTurnover(turnover);
        });
        return page;
    }

    /**
     * 行情机器人专用：当前委托
     *
     * @param uid
     * @param sign
     * @return
     */
    @RequestMapping("mockcurrentydhdnskd")
    public Page<ExchangeOrder> currentOrderMock(Long uid, String sign, String symbol, int pageNo, int pageSize) {
        if (uid != 1 && uid != 10001) {
            return null;
        }
        if (!sign.equals("77585211314qazwsx")) {
            return null;
        }
        Page<ExchangeOrder> page = orderService.findCurrent(uid, symbol, pageNo, pageSize);

//        page.getContent().forEach(exchangeOrder -> {
//            //获取交易成交详情(机器人无需获取详情）
//
//            BigDecimal tradedAmount = BigDecimal.ZERO;
//            List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(exchangeOrder.getOrderId());
//            exchangeOrder.setDetail(details);
//            for (ExchangeOrderDetail trade : details) {
//                tradedAmount = tradedAmount.add(trade.getAmount());
//            }
//            exchangeOrder.setTradedAmount(tradedAmount);
//
//        });

        return page;
    }

    /**
     * 行情机器人专用：交易取消委托
     *
     * @param uid
     * @param orderId
     * @return
     */
    @RequestMapping("mockcancelydhdnskd")
    public MessageResult cancelOrdermock(Long uid, String sign, String orderId) {
        ExchangeOrder order = orderService.findOne(orderId);
        if (uid != 1 && uid != 10001) {
            return MessageResult.error(500, msService.getMessage("OPERATION_FORBIDDEN"));
        }
        if (!sign.equals("77585211314qazwsx")) {
            return MessageResult.error(500, msService.getMessage("OPERATION_FORBIDDEN"));
        }
        if (order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, msService.getMessage("ORDER_STATUS_ERROR"));
        }
        // 活动清盘期间，无法撤销订单
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(order.getSymbol());
        if (exchangeCoin.getPublishType() != ExchangeCoinPublishType.NONE) {
            long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
            try {
                // 处在活动结束时间与清盘结束时间之间
                if (currentTime > dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime() &&
                        currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                    return MessageResult.error(500, msService.getMessage("CANNOT_CANCEL_ORDER"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500, msService.getMessage("EXAPI_UNKNOWN_ERROR3"));
            }
        }
        if (isExchangeOrderExist(order)) {
            // 发送消息至Exchange系统
            kafkaTemplate.send("exchange-order-cancel", JSON.toJSONString(order));
        } else {
            //强制取消
            orderService.forceCancelOrder(order);
        }
        return MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
    }

    /**
     * 查询委托成交明细
     *
     * @param member
     * @param orderId
     * @return
     */
    @RequestMapping("detail/{orderId}")
    public List<ExchangeOrderDetail> currentOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member, @PathVariable String orderId) {
        return exchangeOrderDetailService.findAllByOrderId(orderId);
    }

    /**
     * 取消委托
     *
     * @param member
     * @param orderId
     * @return
     */
    @RequestMapping("cancel/{orderId}")
    public MessageResult cancelOrder(@SessionAttribute(SESSION_MEMBER) AuthMember member, @PathVariable String orderId) {
        ExchangeOrder order = orderService.findOne(orderId);

        if (order.getMemberId() != member.getId()) {
            return MessageResult.error(500, msService.getMessage("OPERATION_FORBIDDEN"));
        }
        if (order.getStatus() != ExchangeOrderStatus.TRADING) {
            return MessageResult.error(500, msService.getMessage("ORDER_STATUS_ERROR"));
        }
        // 活动清盘期间，无法撤销订单
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(order.getSymbol());
        if (exchangeCoin.getPublishType() != ExchangeCoinPublishType.NONE) {
            long currentTime = Calendar.getInstance().getTimeInMillis(); // 当前时间戳
            try {
                // 处在活动结束时间与清盘结束时间之间
                if (currentTime > dateTimeFormat.parse(exchangeCoin.getEndTime()).getTime() &&
                        currentTime < dateTimeFormat.parse(exchangeCoin.getClearTime()).getTime()) {
                    return MessageResult.error(500, msService.getMessage("CANNOT_CANCEL_ORDER"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return MessageResult.error(500, msService.getMessage("EXAPI_UNKNOWN_ERROR3"));
            }
        }
        if (isExchangeOrderExist(order)) {
            if (maxCancelTimes > 0 && orderService.findTodayOrderCancelTimes(member.getId(), order.getSymbol()) >= maxCancelTimes) {
                return MessageResult.error(500, msService.getMessage("CANCELLED") + maxCancelTimes + msService.getMessage("SECOND"));
            }
            // 发送消息至Exchange系统
            kafkaTemplate.send("exchange-order-cancel", JSON.toJSONString(order));
        } else {
            //强制取消
            orderService.forceCancelOrder(order);
        }
        return MessageResult.success(msService.getMessage("EXAPI_SUCCESS"));
    }

    /**
     * 查找撮合交易器中订单是否存在
     *
     * @param order
     * @return
     */
    public boolean isExchangeOrderExist(ExchangeOrder order) {
        try {
            String serviceName = "SERVICE-EXCHANGE-TRADE";
            String url = "http://" + serviceName + "/monitor/order?symbol=" + order.getSymbol() + "&orderId=" + order.getOrderId() + "&direction=" + order.getDirection() + "&type=" + order.getType();
            ResponseEntity<ExchangeOrder> result = restTemplate.getForEntity(url, ExchangeOrder.class);
            return result != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取下单时间限制
     *
     * @return
     */
    @GetMapping("/time_limit")
    public MessageResult userAddExchangeTimeLimit() {
        MessageResult mr = new MessageResult();
        mr.setCode(0);
        mr.setMessage("EXAPI_SUCCESS");
        mr.setData(SysConstant.USER_ADD_EXCHANGE_ORDER_TIME_LIMIT_EXPIRE_TIME);
        return mr;
    }
}
