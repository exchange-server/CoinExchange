//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.spark.blockchain.util;

public class JSON {
    public JSON() {
    }

    public static String stringify(Object o) {
        return com.alibaba.fastjson.JSON.toJSONString(o);
    }

    public static Object parse(String s) {
        return CrippledJavaScriptParser.parseJSExpr(s);
    }
}
