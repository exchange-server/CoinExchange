package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.TransferRecordDao;
import com.bizzan.bitrade.entity.TransferRecord;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @date 2020年02月27日
 */
@Service
public class TransferRecordService extends BaseService {
    @Resource
    private TransferRecordDao transferRecordDao;

    public TransferRecord save(TransferRecord transferRecord) {
        return transferRecordDao.save(transferRecord);
    }

    @Transactional(readOnly = true)
    public Page<TransferRecord> findAllByMemberId(Long memberId, int page, int pageSize) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(page, pageSize, orders);
        Criteria<TransferRecord> specification = new Criteria<>();
        specification.add(Restrictions.eq("memberId", memberId, false));
        return transferRecordDao.findAll(specification, pageRequest);
    }
}
