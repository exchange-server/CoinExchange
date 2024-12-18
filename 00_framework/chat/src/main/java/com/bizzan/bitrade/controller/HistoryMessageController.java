package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.entity.HistoryChatMessage;
import com.bizzan.bitrade.entity.HistoryMessagePage;
import com.bizzan.bitrade.handler.MessageHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class HistoryMessageController {

    @Resource
    private MessageHandler chatMessageHandler;

    @RequestMapping("/getHistoryMessage")
    public HistoryMessagePage getHistoryMessage(HistoryChatMessage message) {
        return chatMessageHandler.getHistoryMessage(message);
    }
}
