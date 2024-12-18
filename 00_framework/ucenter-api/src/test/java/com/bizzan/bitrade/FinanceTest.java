package com.bizzan.bitrade;

import org.junit.Test;

import java.math.BigDecimal;

public class FinanceTest {

    @Test
    public void testBigdecimal() {
        BigDecimal a = new BigDecimal("12.5");
        System.out.println(a);
        a.subtract(BigDecimal.ONE);
        System.out.println(a);
    }
}
