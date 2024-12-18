package com.bizzan.bitrade.test.service;

import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.test.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;


public class MemberServiceTest extends BaseTest {

    @Resource
    private MemberService memberService;

    @Test
    public void test() {
        Member member = memberService.findOne(25L);
        System.out.println(">>>>>>>>>>>>>>" + member);

    }

}
