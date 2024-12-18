package com.bizzan.bitrade.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.Trader.CoinTrader;
import com.bizzan.bitrade.Trader.CoinTraderFactory;
import com.bizzan.bitrade.entity.ExchangeCoin;
import com.bizzan.bitrade.entity.ExchangeOrder;
import com.bizzan.bitrade.entity.ExchangeOrderDetail;
import com.bizzan.bitrade.entity.ExchangeOrderDirection;
import com.bizzan.bitrade.entity.ExchangeOrderType;
import com.bizzan.bitrade.entity.TradePlateItem;
import com.bizzan.bitrade.service.ExchangeCoinService;
import com.bizzan.bitrade.service.ExchangeOrderDetailService;
import com.bizzan.bitrade.service.ExchangeOrderService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.util.MessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monitor")
public class MonitorController {
    private Logger log = LoggerFactory.getLogger(MonitorController.class);
    @Resource
    private CoinTraderFactory factory;
    @Resource
    private ExchangeOrderService exchangeOrderService;
    @Resource
    private ExchangeOrderDetailService exchangeOrderDetailService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private ExchangeCoinService exchangeCoinService;
    /**
     * 重置交易引擎，重新开始（应用场景主要是防止plate盘面出现脏数据（倒挂等）
     *
     * @param symbol
     * @return
     */
    @Resource
    private LocaleMessageSourceService msService;

    @RequestMapping("overview")
    public JSONObject traderOverview(String symbol) {
        CoinTrader trader = factory.getTrader(symbol);
        if (trader == null) {
            return null;
        }
        JSONObject result = new JSONObject();
        //卖盘信息
        JSONObject ask = new JSONObject();
        //买盘信息
        JSONObject bid = new JSONObject();
        ask.put("limit_price_order_count", trader.getLimitPriceOrderCount(ExchangeOrderDirection.SELL));
        ask.put("market_price_order_count", trader.getSellMarketQueue().size());
        ask.put("depth", trader.getTradePlate(ExchangeOrderDirection.SELL).getDepth());
        bid.put("limit_price_order_count", trader.getLimitPriceOrderCount(ExchangeOrderDirection.BUY));
        bid.put("market_price_order_count", trader.getBuyMarketQueue().size());
        bid.put("depth", trader.getTradePlate(ExchangeOrderDirection.BUY).getDepth());
        result.put("ask", ask);
        result.put("bid", bid);
        return result;
    }

    @RequestMapping("trader-detail")
    public JSONObject traderDetail(String symbol) {
        CoinTrader trader = factory.getTrader(symbol);
        if (trader == null) {
            return null;
        }
        JSONObject result = new JSONObject();
        //卖盘信息
        JSONObject ask = new JSONObject();
        //买盘信息
        JSONObject bid = new JSONObject();
        ask.put("limit_price_queue", trader.getSellLimitPriceQueue());
        ask.put("market_price_queue", trader.getSellMarketQueue());
        bid.put("limit_price_queue", trader.getBuyLimitPriceQueue());
        bid.put("market_price_queue", trader.getBuyMarketQueue());
        result.put("ask", ask);
        result.put("bid", bid);
        return result;
    }

    @RequestMapping("plate")
    public Map<String, List<TradePlateItem>> traderPlate(String symbol) {
        Map<String, List<TradePlateItem>> result = new HashMap<>();
        CoinTrader trader = factory.getTrader(symbol);
        if (trader == null) {
            return null;
        }
        result.put("bid", trader.getTradePlate(ExchangeOrderDirection.BUY).getItems());
        result.put("ask", trader.getTradePlate(ExchangeOrderDirection.SELL).getItems());
        return result;
    }

    @RequestMapping("plate-mini")
    public Map<String, JSONObject> traderPlateMini(String symbol) {
        Map<String, JSONObject> result = new HashMap<>();
        CoinTrader trader = factory.getTrader(symbol);
        if (trader == null) {
            return null;
        }
        result.put("bid", trader.getTradePlate(ExchangeOrderDirection.BUY).toJSON(24));
        result.put("ask", trader.getTradePlate(ExchangeOrderDirection.SELL).toJSON(24));
        return result;
    }

    @RequestMapping("plate-full")
    public Map<String, JSONObject> traderPlateFull(String symbol) {
        Map<String, JSONObject> result = new HashMap<>();
        CoinTrader trader = factory.getTrader(symbol);
        if (trader == null) {
            return null;
        }
        result.put("bid", trader.getTradePlate(ExchangeOrderDirection.BUY).toJSON(100));
        result.put("ask", trader.getTradePlate(ExchangeOrderDirection.SELL).toJSON(100));
        return result;
    }

    @RequestMapping("symbols")
    public List<String> symbols() {
        Map<String, CoinTrader> traders = factory.getTraderMap();
        List<String> symbols = new ArrayList<>();
        traders.forEach((key, trader) -> {
            symbols.add(key);
        });
        return symbols;
    }

    @RequestMapping("engines")
    public Map<String, Integer> engines() {
        Map<String, CoinTrader> traders = factory.getTraderMap();
        Map<String, Integer> symbols = new HashMap<String, Integer>();
        traders.forEach((key, trader) -> {
            if (trader.isTradingHalt()) {
                symbols.put(key, 2);
            } else {
                symbols.put(key, 1);
            }
        });
        return symbols;
    }

    /**
     * 查找订单
     *
     * @param symbol
     * @param orderId
     * @param direction
     * @param type
     * @return
     */
    @RequestMapping("order")
    public ExchangeOrder findOrder(String symbol, String orderId, ExchangeOrderDirection direction,
                                   ExchangeOrderType type) {
        CoinTrader trader = factory.getTrader(symbol);
        return trader.findOrder(orderId, type, direction);
    }

    @RequestMapping("reset-trader")
    public MessageResult resetTrader(String symbol) {
        log.info("======[Start]Reset CoinTrader: " + symbol + "======");
        if (factory.containsTrader(symbol)) {
            // 交易对引擎不存在，则创建
            // 检查该币种在数据库定义中是否存在
            ExchangeCoin coin = exchangeCoinService.findBySymbol(symbol);
            if (coin == null || coin.getEnable() != 1) {
                return MessageResult.error(500, "CURRENCY_PAIR_DOES_NOT_EXIST");
            }

            if (coin.getEnable() != 1) {
                return MessageResult.error(500, "PROHIBITION_OF_CURRENCY_PAIRS");
            }

            CoinTrader trader = factory.getTrader(symbol);
            if (!trader.isTradingHalt()) {
                return MessageResult.error(500, "STOP_CURRENT_ENGINE");
            }
            CoinTrader newTrader = new CoinTrader(symbol);
            newTrader.setKafkaTemplate(kafkaTemplate);
            newTrader.setBaseCoinScale(coin.getBaseCoinScale());
            newTrader.setCoinScale(coin.getCoinScale());
            newTrader.setPublishType(coin.getPublishType());
            newTrader.setClearTime(coin.getClearTime());

            // 创建成功以后需要对未处理订单预处理
            log.info("======CoinTrader Process: " + symbol + "======");
            List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(symbol);
            List<ExchangeOrder> tradingOrders = new ArrayList<>();
            List<ExchangeOrder> completedOrders = new ArrayList<>();
            orders.forEach(order -> {
                BigDecimal tradedAmount = BigDecimal.ZERO;
                BigDecimal turnover = BigDecimal.ZERO;
                List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(order.getOrderId());

                for (ExchangeOrderDetail od : details) {
                    tradedAmount = tradedAmount.add(od.getAmount());
                    turnover = turnover.add(od.getAmount().multiply(od.getPrice()));
                }
                order.setTradedAmount(tradedAmount);
                order.setTurnover(turnover);
                if (!order.isCompleted()) {
                    tradingOrders.add(order);
                } else {
                    completedOrders.add(order);
                }
            });
            try {
                newTrader.trade(tradingOrders);
            } catch (ParseException e) {
                e.printStackTrace();
                log.info("异常：trader.trade(tradingOrders);");
                return MessageResult.error(500, symbol + msService.getMessage("ENGINE_CREATION_FAILED"));
            }
            //判断已完成的订单发送消息通知
            if (completedOrders.size() > 0) {
                kafkaTemplate.send("exchange-order-completed", JSON.toJSONString(completedOrders));
            }
            newTrader.setReady(true);
            factory.resetTrader(symbol, newTrader);
            log.info("======[END]Reset CoinTrader: " + symbol + " successful======");
            return MessageResult.success(symbol + msService.getMessage("ENGINE_CREATED_SUCCESSFULLY"));
        } else {
            return MessageResult.error(500, symbol + msService.getMessage("ENGINE_DOES_NOT_EXIST"));
        }
    }

    @RequestMapping("start-trader")
    public MessageResult startTrader(String symbol) {
        log.info("======Start CoinTrader: " + symbol + "======");
        if (!factory.containsTrader(symbol)) {
            // 交易对引擎不存在，则创建
            // 检查该币种在数据库定义中是否存在
            ExchangeCoin coin = exchangeCoinService.findBySymbol(symbol);
            if (coin == null || coin.getEnable() != 1) {
                return MessageResult.error(500, "CURRENCY_PAIR_DOES_NOT_EXIST");
            }
            if (coin.getEnable() != 1) {
                return MessageResult.error(500, "PROHIBITION_OF_CURRENCY_PAIRS");
            }
            CoinTrader newTrader = new CoinTrader(symbol);
            newTrader.setKafkaTemplate(kafkaTemplate);
            newTrader.setBaseCoinScale(coin.getBaseCoinScale());
            newTrader.setCoinScale(coin.getCoinScale());
            newTrader.setPublishType(coin.getPublishType());
            newTrader.setClearTime(coin.getClearTime());

            // 创建成功以后需要对未处理订单预处理
            log.info("======CoinTrader Process: " + symbol + "======");
            List<ExchangeOrder> orders = exchangeOrderService.findAllTradingOrderBySymbol(symbol);
            List<ExchangeOrder> tradingOrders = new ArrayList<>();
            List<ExchangeOrder> completedOrders = new ArrayList<>();
            orders.forEach(order -> {
                BigDecimal tradedAmount = BigDecimal.ZERO;
                BigDecimal turnover = BigDecimal.ZERO;
                List<ExchangeOrderDetail> details = exchangeOrderDetailService.findAllByOrderId(order.getOrderId());

                for (ExchangeOrderDetail od : details) {
                    tradedAmount = tradedAmount.add(od.getAmount());
                    turnover = turnover.add(od.getAmount().multiply(od.getPrice()));
                }
                order.setTradedAmount(tradedAmount);
                order.setTurnover(turnover);
                if (!order.isCompleted()) {
                    tradingOrders.add(order);
                } else {
                    completedOrders.add(order);
                }
            });
            try {
                newTrader.trade(tradingOrders);
            } catch (ParseException e) {
                e.printStackTrace();
                log.info("异常：trader.trade(tradingOrders);");
                return MessageResult.error(500, "ENGINE_CREATION_FAILED");
            }
            //判断已完成的订单发送消息通知
            if (completedOrders.size() > 0) {
                kafkaTemplate.send("exchange-order-completed", JSON.toJSONString(completedOrders));
            }
            newTrader.setReady(true);
            factory.addTrader(symbol, newTrader);

            return MessageResult.success("CURRENCY_PAIR_CREATED_SUCCESSFULLY");
        } else {
            CoinTrader trader = factory.getTrader(symbol);
            if (trader.isTradingHalt()) {
                trader.resumeTrading();
                return MessageResult.success("ENGINE_STATE_HAS_STOPPED");
            } else {
                return MessageResult.error(500, "ENGINE_STATUS_IS_RUNNING");
            }
        }
    }

    @RequestMapping("stop-trader")
    public MessageResult stopTrader(String symbol) {
        CoinTrader trader = factory.getTrader(symbol);
        log.info("======Stop CoinTrader: " + symbol + "======");
        if (trader == null) {
            return MessageResult.error(500, symbol + msService.getMessage("CURRENCY_PAIR_ENGINE_DOES_NOT_EXIST"));
        } else {
            if (trader.isTradingHalt()) {
                return MessageResult.error(500, symbol + msService.getMessage("ENGINE_STATE_HAS_STOPPED"));
            } else {
                trader.haltTrading();
                return MessageResult.success("ENGINE_STOPPED_SUCCESSFULLY");
            }
        }
    }
}
