package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.MemberApiKeyDao;
import com.bizzan.bitrade.entity.MemberApiKey;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author QQ:247818019 E-mail:247818019@qq.com
 * @description: MemberApiKeyService
 * @create: 2019/05/07 10:40
 */
@Service
public class MemberApiKeyService extends BaseService<MemberApiKey> {

    @Resource
    private MemberApiKeyDao apiKeyDao;

    public MemberApiKey findMemberApiKeyByApiKey(String apiKey) {
        return apiKeyDao.findMemberApiKeyByApiKey(apiKey);
    }

    public MemberApiKey findByMemberIdAndId(Long memberId, Long id) {
        return apiKeyDao.findMemberApiKeyByMemberIdAndId(memberId, id);
    }

    public MemberApiKey save(MemberApiKey memberApiKey) {
        return apiKeyDao.save(memberApiKey);
    }


    public void del(Long id) {
        apiKeyDao.del(id);
    }

    public List<MemberApiKey> findAllByMemberId(Long memberId) {
        return apiKeyDao.findAllByMemberId(memberId);
    }
}
