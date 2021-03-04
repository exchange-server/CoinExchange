package com.bizzan.bitrade.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONUtils {
    public static boolean isJsonObject(String content) {
        if ("".equals(content) || null == content) {
            return false;
        }
        try {
            JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isJsonArray(String content) {
        if ("".equals(content) || null == content) {
            return false;
        }
        try {
            JSONArray.parseArray(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
