package com.spark.blockchain.rpcclient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

class MapWrapper {
    public final Map m;

    public MapWrapper(Map m) {
        this.m = m;
    }

    public boolean mapBool(String key) {
        return mapBool(this.m, key);
    }

    public float mapFloat(String key) {
        return mapFloat(this.m, key);
    }

    public double mapDouble(String key) {
        return mapDouble(this.m, key);
    }

    public int mapInt(String key) {
        return mapInt(this.m, key);
    }

    public long mapLong(String key) {
        return mapLong(this.m, key);
    }

    public String mapStr(String key) {
        return mapStr(this.m, key);
    }

    public Date mapCTime(String key) {
        return mapCTime(this.m, key);
    }

    public static boolean mapBool(Map m, String key) {
        return (Boolean)m.get(key);
    }

    public static float mapFloat(Map m, String key) {
        return ((Number)m.get(key)).floatValue();
    }

    public static double mapDouble(Map m, String key) {
        return ((Number)m.get(key)).doubleValue();
    }

    public static BigDecimal mapBigDecimal(Map m, String key) {
        return new BigDecimal(m.get(key).toString());
    }

    public static int mapInt(Map m, String key) {
        return ((Number)m.get(key)).intValue();
    }

    public static long mapLong(Map m, String key) {
        return ((Number)m.get(key)).longValue();
    }

    public static String mapStr(Map m, String key) {
        Object v = m.get(key);
        return v == null ? null : String.valueOf(v);
    }

    public static Date mapCTime(Map m, String key) {
        Object v = m.get(key);
        return v == null ? null : new Date(mapLong(m, key) * 1000L);
    }

    public String toString() {
        return String.valueOf(this.m);
    }
}
