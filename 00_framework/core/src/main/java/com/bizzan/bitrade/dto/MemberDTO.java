package com.bizzan.bitrade.dto;

import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import lombok.Data;

import java.util.List;

@Data
public class MemberDTO {

    private Member member;

    private List<MemberWallet> list;

}
