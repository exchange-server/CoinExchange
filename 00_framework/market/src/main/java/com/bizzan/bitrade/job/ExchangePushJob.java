package com.bizzan.bitrade.job;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bizzan.bitrade.handler.NettyHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Component
public class ExchangePushJob {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private NettyHandler nettyHandler;
    private Map<String,List<ExchangeTrade>> tradesQueue = new HashMap<>();
    private Map<String,List<TradePlate>> plateQueue = new HashMap<>();
    private Map<String,List<CoinThumb>> thumbQueue = new HashMap<>();

    private Random rand = new Random();
    private Map<String, TradePlate> plateLastBuy = new HashMap<>(); // 最后一次推送的盘口，仅仅是为了虚拟推送二设立的
    private Map<String, TradePlate> plateLastSell = new HashMap<>(); // 最后一次推送的盘口，仅仅是为了虚拟推送二设立的

    public void addTrades(String symbol, List<ExchangeTrade> trades){
        List<ExchangeTrade> list = tradesQueue.get(symbol);
        if(list == null){
            list = new ArrayList<>();
            tradesQueue.put(symbol,list);
        }
        synchronized (list) {
            list.addAll(trades);
        }
    }

    public void addPlates(String symbol, TradePlate plate){
        List<TradePlate> list = plateQueue.get(symbol);
        if(list == null){
            list = new ArrayList<>();
            plateQueue.put(symbol,list);
        }
        synchronized (list) {
            list.add(plate);
        }

        if(plate.getDirection() == ExchangeOrderDirection.BUY) {
            // 更新最新盘口
            synchronized (plateLastBuy) {
                plateLastBuy.put(symbol, plate);
            }
        }
        if(plate.getDirection() == ExchangeOrderDirection.SELL) {
            // 更新最新盘口
            synchronized (plateLastSell) {
                plateLastSell.put(symbol, plate);
            }
        }
    }

    public void addThumb(String symbol, CoinThumb thumb){
        List<CoinThumb> list = thumbQueue.get(symbol);
        if(list == null){
            list = new ArrayList<>();
            thumbQueue.put(symbol,list);
        }
        synchronized (list) {
            list.add(thumb);
        }
    }


    @Scheduled(fixedRate = 300)
    public void pushTrade(){
        Iterator<Map.Entry<String,List<ExchangeTrade>>> entryIterator = tradesQueue.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<String,List<ExchangeTrade>> entry =  entryIterator.next();
            String symbol = entry.getKey();
            List<ExchangeTrade> trades = entry.getValue();
            if(trades.size() > 0){
                synchronized (trades) {
                    messagingTemplate.convertAndSend("/topic/market/trade/" + symbol, trades);
                    trades.clear();
                }
            }
        }
    }

    @Scheduled(fixedDelay = 500)
    public void pushPlate(){
        Iterator<Map.Entry<String,List<TradePlate>>> entryIterator = plateQueue.entrySet().iterator();
        while (entryIterator.hasNext()){
            Map.Entry<String,List<TradePlate>> entry =  entryIterator.next();
            String symbol = entry.getKey();
            List<TradePlate> plates = entry.getValue();
            if(plates.size() > 0){
                boolean hasPushAskPlate = false;
                boolean hasPushBidPlate = false;
                synchronized (plates) {
                    for(TradePlate plate:plates) {
                        if(plate.getDirection() == ExchangeOrderDirection.BUY && !hasPushBidPlate) {
                            hasPushBidPlate = true;
                        }
                        else if(plate.getDirection() == ExchangeOrderDirection.SELL && !hasPushAskPlate){
                            hasPushAskPlate = true;
                        }
                        else {
                            continue;
                        }
                        //websocket推送盘口信息
                        messagingTemplate.convertAndSend("/topic/market/trade-plate/" + symbol, plate.toJSON(24));
                        //websocket推送深度信息
                        messagingTemplate.convertAndSend("/topic/market/trade-depth/" + symbol, plate.toJSON(50));
                        //netty推送
                        nettyHandler.handlePlate(symbol, plate);
                    }

                    plates.clear();
                }
            }else{
                // 不管盘口有没有变化，都推送一下数据，显得盘口交易很活跃的样子(这里获取到的盘口有可能是买盘，也可能是卖盘)
                TradePlate plateBuy = plateLastBuy.get(symbol);
                TradePlate plateSell = plateLastSell.get(symbol);
                if(plateBuy != null) {
                    // 随机修改盘口数据，然后推送
                    List<TradePlateItem> list = plateBuy.getItems();
                    if(list.size() > 9) { // 只要大于随机数的种子就行，这里的4是随意设置的
                        int randInt = rand.nextInt(9);
                        list.get(randInt).setAmount(list.get(randInt).getAmount().multiply(BigDecimal.valueOf(0.5))); // 随机挑选一个，让盘口订单数量+50%
                        if(randInt > 0 && randInt < list.size() - 1) { // 如果在中间，就可以变动价格
                            // 价格取前后价格居中的价格
                            list.get(randInt).setPrice(list.get(randInt - 1).getPrice().add(list.get(randInt + 1).getPrice()).divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_DOWN));
                        }
                        //websocket推送盘口信息
                        messagingTemplate.convertAndSend("/topic/market/trade-plate/" + symbol, plateBuy.toJSON(24));
                        //websocket推送深度信息
                        messagingTemplate.convertAndSend("/topic/market/trade-depth/" + symbol, plateBuy.toJSON(50));
                        //netty推送
                        nettyHandler.handlePlate(symbol, plateBuy);
                    }
                }
                if(plateSell != null) {
                    // 随机修改盘口数据，然后推送
                    List<TradePlateItem> list = plateSell.getItems();
                    if(list.size() > 9) { // 只要大于随机数的种子就行，这里的4是随意设置的
                        int randInt = rand.nextInt(9);
                        list.get(randInt).setAmount(list.get(randInt).getAmount().multiply(BigDecimal.valueOf(0.5))); // 随机挑选一个，让盘口订单数量+50%
                        if(randInt > 0 && randInt < list.size() - 1) { // 如果在中间，就可以变动价格
                            // 价格取前后价格居中的价格
                            list.get(randInt).setPrice(list.get(randInt - 1).getPrice().add(list.get(randInt + 1).getPrice()).divide(BigDecimal.valueOf(2), 8, RoundingMode.HALF_DOWN));
                        }
                        //websocket推送盘口信息
                        messagingTemplate.convertAndSend("/topic/market/trade-plate/" + symbol, plateSell.toJSON(24));
                        //websocket推送深度信息
                        messagingTemplate.convertAndSend("/topic/market/trade-depth/" + symbol, plateSell.toJSON(50));
                        //netty推送
                        nettyHandler.handlePlate(symbol, plateSell);
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 300)
    public void pushThumb(){
        Iterator<Map.Entry<String,List<CoinThumb>>> entryIterator = thumbQueue.entrySet().iterator();
        log.info("thumbQueue::::"+ JSON.toJSONString(thumbQueue));
        while (entryIterator.hasNext()){
            Map.Entry<String,List<CoinThumb>> entry =  entryIterator.next();
            String symbol = entry.getKey();
            List<CoinThumb> thumbs = entry.getValue();
            if(thumbs.size() > 0){
                synchronized (thumbs) {
                    messagingTemplate.convertAndSend("/topic/market/thumb",thumbs.get(thumbs.size() - 1));
                    thumbs.clear();
                }
            }
        }
    }
}
