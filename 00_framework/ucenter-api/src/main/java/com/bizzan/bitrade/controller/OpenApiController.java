package com.bizzan.bitrade.controller;

import com.bizzan.bitrade.constant.SysConstant;
import com.bizzan.bitrade.entity.MemberApiKey;
import com.bizzan.bitrade.entity.transform.AuthMember;
import com.bizzan.bitrade.service.MemberApiKeyService;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.bizzan.bitrade.constant.SysConstant.SESSION_MEMBER;

/**
 * @description: OpenApiController
 * @author: Shao
 * @create: 2020/05/07 10:33
 */
@Slf4j
@RestController
@RequestMapping("open")
public class OpenApiController extends BaseController {

    @Autowired
    private MemberApiKeyService memberApiKeyService;

    @Autowired
    private RedisUtil redisUtil ;

    /**
     * 获取ApiKey
     * @param member
     * @return
     */
    @RequestMapping(value = "get_key",method = RequestMethod.GET)
    public MessageResult queryApiKey(@SessionAttribute(SESSION_MEMBER) AuthMember member){
        List<MemberApiKey> result = memberApiKeyService.findAllByMemberId(member.getId());
        return success(result);
    }


    /**
     * 新增api-key
     * @param member
     * @param memberApiKey
     * @return
     */
    @RequestMapping(value = "api/save",method = RequestMethod.POST)
    public MessageResult saveApiKey(@SessionAttribute(SESSION_MEMBER) AuthMember member,MemberApiKey memberApiKey){
        log.info("-------新增API-key:"+ JSONObject.toJSONString(memberApiKey));
        String code = memberApiKey.getCode();
        Assert.isTrue(StringUtils.isNotEmpty(code),"请输入验证码");
        Object cacheCode = redisUtil.get(SysConstant.API_BIND_CODE_PREFIX+member.getMobilePhone());
        if(cacheCode == null){
            return MessageResult.error("验证码已过期");
        }
        if(!code.equalsIgnoreCase(cacheCode.toString())){
            return MessageResult.error("验证码不正确");
        }
        List<MemberApiKey> all = memberApiKeyService.findAllByMemberId(member.getId());
        if (all.isEmpty() || all.size()<5){
            memberApiKey.setId(null);
            if (StringUtils.isBlank(memberApiKey.getBindIp())){
                //不绑定IP时默认90天过期
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,90);
                memberApiKey.setExpireTime(calendar.getTime());
            }
            memberApiKey.setApiName(member.getId()+"");
            memberApiKey.setApiKey(GeneratorUtil.getUUID());
            String secret = GeneratorUtil.getUUID();
            memberApiKey.setSecretKey(secret);
            memberApiKey.setMemberId(member.getId());
            memberApiKey.setCreateTime(new Date());
            memberApiKeyService.save(memberApiKey);
            return success("新增成功",secret);
        }else {
            return error("数量超过最大限制");
        }
    }


    /**
     * 修改API-key
     * @param member
     * @param memberApiKey
     * @return
     */
    @RequestMapping(value = "api/update",method = RequestMethod.POST)
    public MessageResult updateApiKey(@SessionAttribute(SESSION_MEMBER) AuthMember member,MemberApiKey memberApiKey){
        log.info("-------修改API-key:"+ JSONObject.toJSONString(memberApiKey));
        if (memberApiKey.getId() != null){
            MemberApiKey findMemberApiKey = memberApiKeyService.findByMemberIdAndId(member.getId(),memberApiKey
                    .getId());
            if (findMemberApiKey != null){
                if (!memberApiKey.getRemark().equals(findMemberApiKey.getRemark())){
                    findMemberApiKey.setRemark(memberApiKey.getRemark());
                }
                if (StringUtils.isNotEmpty(memberApiKey.getBindIp())){
                    findMemberApiKey.setBindIp(memberApiKey.getBindIp());
                }else {
                    findMemberApiKey.setBindIp(null);
                }

                memberApiKeyService.save(findMemberApiKey);
                return success("修改成功");
            }else {
                return error("记录不存在");

            }
        }else {
            return error("记录不存在");
        }

    }


    /**
     * 删除API-key
     * @param member
     * @param id
     * @return
     */
    @RequestMapping(value = "api/del/{id}",method = RequestMethod.GET)
    public MessageResult updateApiKey(@SessionAttribute(SESSION_MEMBER) AuthMember member, @PathVariable("id")Long id){
        log.info("------删除api-key：memberId={},id={}",member.getId(),id);
        memberApiKeyService.del(id);
        return success("删除成功");
    }

}
