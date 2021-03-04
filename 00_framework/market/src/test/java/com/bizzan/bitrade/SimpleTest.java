package com.bizzan.bitrade;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleTest {

    @Test
    public void testDate(){
        long tick = 1515588926302L;
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        System.out.println(df.format(new Date(tick)));
    }

    @Test
    public void testBigDecimal(){
        BigDecimal a = BigDecimal.ZERO;
        BigDecimal b = new BigDecimal("0.00");
        System.out.println(a.compareTo(b));
    }

    @Test
    public  void testCalendar(){
        Calendar calendar = Calendar.getInstance();
        System.out.println(calendar.get(Calendar.HOUR));
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void testString(){
        String str = "15H";
        System.out.println(str.substring(0,str.length()-1));
    }
}
