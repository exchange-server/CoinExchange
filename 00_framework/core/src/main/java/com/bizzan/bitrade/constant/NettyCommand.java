package com.bizzan.bitrade.constant;

public class NettyCommand {
    public static final int COMMANDS_VERSION = 1;
    public static final short SUBSCRIBE_SYMBOL_THUMB = 20001;
    public static final short UNSUBSCRIBE_SYMBOL_THUMB = 20002;
    public static final short PUSH_SYMBOL_THUMB = 20003;

    public static final short SUBSCRIBE_EXCHANGE = 20021;
    public static final short UNSUBSCRIBE_EXCHANGE = 20022;
    public static final short PUSH_EXCHANGE_TRADE = 20023;
    public static final short PUSH_EXCHANGE_PLATE = 20024;
    public static final short PUSH_EXCHANGE_KLINE = 20025;
    public static final short PUSH_EXCHANGE_ORDER_COMPLETED = 20026;
    public static final short PUSH_EXCHANGE_ORDER_CANCELED = 20027;
    public static final short PUSH_EXCHANGE_ORDER_TRADE = 20028;
    public static final short PUSH_EXCHANGE_DEPTH = 20029;

    public static final short SUBSCRIBE_CHAT = 20031;
    public static final short UNSUBSCRIBE_CHAT = 20032;
    public static final short PUSH_CHAT = 20033;

    public static final short SEND_CHAT = 20034;
    public static final short SUBSCRIBE_GROUP_CHAT = 20035;
    public static final short UNSUBSCRIBE_GROUP_CHAT = 20036;
    public static final short SUBSCRIBE_APNS = 20037;
    public static final short UNSUBSCRIBE_APNS = 20038;
    public static final short PUSH_GROUP_CHAT = 20039;

    // 合约相关
    public static final short CONTRACT_SUBSCRIBE_SYMBOL_THUMB = 30001; // 指令：订阅行情
    public static final short CONTRACT_UNSUBSCRIBE_SYMBOL_THUMB = 30002; // 指令：取消订阅行情
    public static final short CONTRACT_PUSH_SYMBOL_THUMB = 30003; // 指令：推送币种行情

    public static final short CONTRACT_SUBSCRIBE_EXCHANGE = 30021; // 指令：订阅交易信息（盘口，K线、成交明细）
    public static final short CONTRACT_UNSUBSCRIBE_EXCHANGE = 30022;  // 指令：取消订阅交易信息
    public static final short CONTRACT_PUSH_EXCHANGE_TRADE = 30023;  // 指令：推送交易明细
    public static final short CONTRACT_PUSH_EXCHANGE_PLATE = 30024;  // 指令：推送盘口数据
    public static final short CONTRACT_PUSH_EXCHANGE_KLINE = 30025; // 指令：推送K线
    public static final short CONTRACT_PUSH_EXCHANGE_ORDER_COMPLETED = 30026;  // 指令：推送订单完成信息（指定用户）
    public static final short CONTRACT_PUSH_EXCHANGE_ORDER_CANCELED = 30027; // 指令：推送订单取消信息（指定用户）
    public static final short CONTRACT_PUSH_EXCHANGE_ORDER_TRADE = 30028; // 指令：推送订单成交信息（指定用户）
    public static final short CONTRACT_PUSH_EXCHANGE_DEPTH = 30029; // 指令：推送盘口深度
}
