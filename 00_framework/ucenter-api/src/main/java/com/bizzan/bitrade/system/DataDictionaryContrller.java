package com.bizzan.bitrade.system;

import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.DataDictionary;
import com.bizzan.bitrade.service.DataDictionaryService;
import com.bizzan.bitrade.util.MessageResult;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jammy
 * @Title: ${file_name}
 * @Description:
 * @date 2019/4/1214:21
 */
@RestController
@RequestMapping("data-dictionary")
public class DataDictionaryContrller extends BaseController {
    @Resource
    private DataDictionaryService service;

    @GetMapping("{bond}")
    public MessageResult get(@PathVariable("bond") String bond) {
        DataDictionary data = service.findByBond(bond);
        if (data == null) {
            return error("validate bond");
        }
        return success((Object) data.getValue());
    }

}
