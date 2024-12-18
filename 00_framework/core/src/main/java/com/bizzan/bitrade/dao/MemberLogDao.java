package com.bizzan.bitrade.dao;

import com.bizzan.bitrade.entity.MemberLog;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface MemberLogDao extends MongoRepository<MemberLog, Long> {
}
