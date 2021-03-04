package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.constant.TransactionType;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.bizzan.bitrade.dao.WithdrawCodeRecordDao;
import com.bizzan.bitrade.dao.WithdrawRecordDao;
import com.bizzan.bitrade.entity.*;
import com.bizzan.bitrade.es.ESUtils;
import com.bizzan.bitrade.pagination.Criteria;
import com.bizzan.bitrade.pagination.PageListMapResult;
import com.bizzan.bitrade.pagination.PageResult;
import com.bizzan.bitrade.pagination.Restrictions;
import com.bizzan.bitrade.service.Base.BaseService;
import com.bizzan.bitrade.vo.WithdrawRecordVO;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bizzan.bitrade.constant.BooleanEnum.IS_FALSE;
import static com.bizzan.bitrade.constant.WithdrawStatus.*;
import static com.bizzan.bitrade.entity.QWithdrawCodeRecord.withdrawCodeRecord;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * @author Jammy
 * @date 2020年01月29日
 */
@Service
@Slf4j
public class WithdrawCodeRecordService extends BaseService {
    private Logger logger = LoggerFactory.getLogger(WithdrawCodeRecordService.class);
    @Autowired
    private WithdrawCodeRecordDao withdrawApplyDao;
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    ESUtils esUtils;

    public WithdrawCodeRecord save(WithdrawCodeRecord withdrawApply) {
        return withdrawApplyDao.save(withdrawApply);
    }

    @Override
    public List<WithdrawCodeRecord> findAll() {
        return withdrawApplyDao.findAll();
    }

    public WithdrawCodeRecord findOne(Long id) {
        return withdrawApplyDao.findOne(id);
    }

    /**
     * 条件查询对象
     *
     * @param predicateList
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageResult<WithdrawCodeRecord> query(List<Predicate> predicateList, Integer pageNo, Integer pageSize) {
        List<WithdrawCodeRecord> list;
        JPAQuery<WithdrawCodeRecord> jpaQuery = queryFactory.selectFrom(withdrawCodeRecord);
        if (predicateList != null) {
            jpaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
        }
        if (pageNo != null && pageSize != null) {
            list = jpaQuery.offset((pageNo - 1) * pageSize).limit(pageSize).fetch();
        } else {
            list = jpaQuery.fetch();
        }
        return new PageResult<>(list, jpaQuery.fetchCount());
    }

    @Transactional(readOnly = true)
    public void test() {
        //查询字段
        List<Expression> expressions = new ArrayList<>();
        expressions.add(QWithdrawCodeRecord.withdrawCodeRecord.memberId.as("memberId"));
        //查询表
        List<EntityPath> entityPaths = new ArrayList<>();
        entityPaths.add(QWithdrawCodeRecord.withdrawCodeRecord);
        entityPaths.add(QMember.member);
        //查询条件
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(QWithdrawCodeRecord.withdrawCodeRecord.memberId.eq(QMember.member.id));
        //排序
        List<OrderSpecifier> orderSpecifierList = new ArrayList<>();
        orderSpecifierList.add(QWithdrawCodeRecord.withdrawCodeRecord.id.desc());
        PageListMapResult pageListMapResult = super.queryDslForPageListResult(expressions, entityPaths, predicates, orderSpecifierList, 1, 10);
        System.out.println(pageListMapResult);
    }

    /**
     * 提现成功处理（提现发起人账户处理、使用提现码充值账户处理）
     *
     * @param withdrawId
     * @param txid
     */
    @Transactional
    public void withdrawSuccess(Long withdrawId, Long memberId) {
        WithdrawCodeRecord record = findOne(withdrawId);
        if (record != null) {
        	// 设置状态为成功（已提现）
            record.setStatus(WithdrawStatus.SUCCESS);
            record.setDealTime(new Date());
            // 处理提现人账户
            MemberWallet wallet = walletService.findByCoinUnitAndMemberId(record.getCoin().getUnit(), record.getMemberId());
            if (wallet != null) {
            	// 扣除发起提现码用户余额
                wallet.setFrozenBalance(wallet.getFrozenBalance().subtract(record.getWithdrawAmount()));
                // 增加资产变更记录
                MemberTransaction transaction = new MemberTransaction();
                transaction.setAmount(record.getWithdrawAmount());
                transaction.setSymbol(wallet.getCoin().getUnit());
                transaction.setAddress("");
                transaction.setMemberId(wallet.getMemberId());
                transaction.setType(TransactionType.WITHDRAWCODE_OUT);
                transaction.setFee(BigDecimal.ZERO);
                transaction.setDiscountFee("0");
                transaction.setRealFee("0");
                transaction.setCreateTime(new Date());
                transaction = transactionService.save(transaction);
            }
            // 处理用提现码充值的用户账户
            MemberWallet walletRecharge = walletService.findByCoinUnitAndMemberId(record.getCoin().getUnit(), memberId);
            if(walletRecharge != null) {
            	walletRecharge.setBalance(walletRecharge.getBalance().add(record.getWithdrawAmount()));
            	
            	MemberTransaction transaction = new MemberTransaction();
                transaction.setAmount(record.getWithdrawAmount());
                transaction.setSymbol(walletRecharge.getCoin().getUnit());
                transaction.setAddress("");
                transaction.setMemberId(memberId);
                transaction.setType(TransactionType.WITHDRAWCODE_IN);
                transaction.setFee(BigDecimal.ZERO);
                transaction.setDiscountFee("0");
                transaction.setRealFee("0");
                transaction.setCreateTime(new Date());
                transaction = transactionService.save(transaction);
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<WithdrawCodeRecord> findAllByMemberId(Long memberId, int page, int pageSize) {
        Sort orders = Criteria.sortStatic("id.desc");
        PageRequest pageRequest = new PageRequest(page, pageSize, orders);
        Criteria<WithdrawCodeRecord> specification = new Criteria<WithdrawCodeRecord>();
        specification.add(Restrictions.eq("memberId", memberId, false));
        return withdrawApplyDao.findAll(specification, pageRequest);
    }

    public long countAuditing(){
        return withdrawApplyDao.countAllByStatus(WithdrawStatus.PROCESSING);
    }

	public WithdrawCodeRecord findByWithdrawCode(String withdrawCode) {
		return withdrawApplyDao.findByWithdrawCode(withdrawCode);
	}
    
}
