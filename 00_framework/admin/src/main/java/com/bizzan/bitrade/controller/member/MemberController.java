package com.bizzan.bitrade.controller.member;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.CertifiedBusinessStatus;
import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.constant.DepositStatusEnum;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.common.BaseAdminController;
import com.bizzan.bitrade.dto.MemberDTO;
import com.bizzan.bitrade.entity.BusinessAuthApply;
import com.bizzan.bitrade.entity.DepositRecord;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.MemberWallet;
import com.bizzan.bitrade.event.MemberEvent;
import com.bizzan.bitrade.model.screen.MemberScreen;
import com.bizzan.bitrade.service.BusinessAuthApplyService;
import com.bizzan.bitrade.service.DepositRecordService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.util.FileUtil;
import com.bizzan.bitrade.util.MessageResult;
import com.bizzan.bitrade.util.PredicateUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.AUDITING;
import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.CANCEL_AUTH;
import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.FAILED;
import static com.bizzan.bitrade.constant.CertifiedBusinessStatus.VERIFIED;
import static com.bizzan.bitrade.constant.MemberLevelEnum.IDENTIFICATION;
import static com.bizzan.bitrade.entity.QMember.member;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * @author Shaoxianjun
 * @description 后台管理会员
 * @date 2019/12/25 16:50
 */
@RestController
@RequestMapping("/member")
@Slf4j
public class MemberController extends BaseAdminController {

    @Resource
    private MemberService memberService;

    @Resource
    private MemberWalletService memberWalletService;

    @Resource
    private BusinessAuthApplyService businessAuthApplyService;

    @Resource
    private DepositRecordService depositRecordService;

    @Resource
    private LocaleMessageSourceService messageSource;
    @Resource
    private MemberEvent memberEvent;

    @RequiresPermissions("member:all")
    @PostMapping("all")
    @AccessLog(module = AdminModule.MEMBER, operation = "所有会员Member")
    public MessageResult all() {
        List<Member> all = memberService.findAll();
        if (all != null && all.size() > 0) {
            return success(all);
        }
        return error(messageSource.getMessage("REQUEST_FAILED"));
    }

    @RequiresPermissions("member:detail")
    @PostMapping("detail")
    @AccessLog(module = AdminModule.MEMBER, operation = "会员Member详情")
    public MessageResult detail(@RequestParam("id") Long id) {
        Member member = memberService.findOne(id);
        notNull(member, "validate id!");
        List<MemberWallet> list = memberWalletService.findAllByMemberId(member.getId());
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMember(member);
        memberDTO.setList(list);
        return success(memberDTO);
    }

    @RequiresPermissions("member:delete")
    @PostMapping("delete")
    @AccessLog(module = AdminModule.MEMBER, operation = "删除会员Member")
    public MessageResult delete(@RequestParam(value = "id") Long id) {
        Member member = memberService.findOne(id);
        notNull(member, "validate id!");
        member.setStatus(CommonStatus.ILLEGAL);// 修改状态非法
        memberService.save(member);
        return success();
    }

    @RequiresPermissions("member:update")
    @PostMapping(value = "update")
    @AccessLog(module = AdminModule.MEMBER, operation = "更新会员Member")
    public MessageResult update(Member member) {
        if (member.getId() == null) {
            return error("id必须传参");
        }
        Member one = memberService.findOne(member.getId());
        if (one == null) {
            return error("用户不存在");
        }
        if (StringUtils.isNotBlank(member.getUsername())) {
            one.setUsername(member.getUsername());
        }
        if (StringUtils.isNotBlank(member.getPassword())) {
            one.setPassword(member.getPassword());
        }
        if (StringUtils.isNotBlank(member.getRealName())) {
            one.setRealName(member.getRealName());
        }
        Member save = memberService.save(one);
        return success(save);
    }

    @RequiresPermissions("member:audit-business")
    @PatchMapping("{id}/audit-business")
    @AccessLog(module = AdminModule.MEMBER, operation = "会员Member认证商家")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult auditBusiness(
            @PathVariable("id") Long id,
            @RequestParam("status") CertifiedBusinessStatus status,
            @RequestParam("detail") String detail) {
        Member member = memberService.findOne(id);
        notNull(member, "validate id!");
        //确认是审核中
        isTrue(member.getCertifiedBusinessStatus() == AUDITING, "validate member certifiedBusinessStatus!");
        //确认传入certifiedBusinessStatus值正确，审核通过或者不通过
        isTrue(status == VERIFIED || status == FAILED, "validate certifiedBusinessStatus!");
        //member.setCertifiedBusinessApplyTime(new Date());//time
        List<BusinessAuthApply> businessAuthApplyList = businessAuthApplyService.findByMemberAndCertifiedBusinessStatus(member, AUDITING);
        if (status == VERIFIED) {
            //通过
            member.setCertifiedBusinessStatus(VERIFIED);//已认证
            member.setMemberLevel(IDENTIFICATION);//认证商家
            if (businessAuthApplyList != null && businessAuthApplyList.size() > 0) {
                BusinessAuthApply businessAuthApply = businessAuthApplyList.get(0);
                businessAuthApply.setCertifiedBusinessStatus(VERIFIED);
                //如果申请的时候选择了保证金策略
                if (businessAuthApply.getBusinessAuthDeposit() != null) {
                    //扣除保证金
                    MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(businessAuthApply.getBusinessAuthDeposit().getCoin().getUnit(), member.getId());
                    memberWallet.setFrozenBalance(memberWallet.getFrozenBalance().subtract(businessAuthApply.getAmount()));
                    DepositRecord depositRecord = new DepositRecord();
                    depositRecord.setId(UUID.randomUUID().toString());
                    depositRecord.setAmount(businessAuthApply.getAmount());
                    depositRecord.setCoin(businessAuthApply.getBusinessAuthDeposit().getCoin());
                    depositRecord.setMember(member);
                    depositRecord.setStatus(DepositStatusEnum.PAY);
                    depositRecordService.create(depositRecord);
                    businessAuthApply.setDepositRecordId(depositRecord.getId());
                }
            }
        } else {
            //不通过
            member.setCertifiedBusinessStatus(FAILED);//认证失败
            if (businessAuthApplyList != null && businessAuthApplyList.size() > 0) {
                BusinessAuthApply businessAuthApply = businessAuthApplyList.get(0);
                businessAuthApply.setCertifiedBusinessStatus(FAILED);
                businessAuthApply.setDetail(detail);
                //申请商家认证时冻结的金额退回
                if (businessAuthApply.getBusinessAuthDeposit() != null) {
                    MemberWallet memberWallet = memberWalletService.findByCoinUnitAndMemberId(businessAuthApply.getBusinessAuthDeposit().getCoin().getUnit(), member.getId());
                    memberWallet.setFrozenBalance(memberWallet.getFrozenBalance().subtract(businessAuthApply.getAmount()));
                    memberWallet.setBalance(memberWallet.getBalance().add(businessAuthApply.getAmount()));
                }
            }
        }
        member.setCertifiedBusinessCheckTime(new Date());
        memberService.save(member);
        return success();
    }

    @RequiresPermissions("member:page-query")
    @PostMapping("page-query")
    @ResponseBody
    @AccessLog(module = AdminModule.MEMBER, operation = "分页查找会员Member")
    public MessageResult page(
            PageModel pageModel,
            MemberScreen screen) {
        Predicate predicate = getPredicate(screen);
        Page<Member> all = memberService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

   /* @RequiresPermissions("member:audit-business")
    @PatchMapping("{id}/cancel-business")
    @AccessLog(module = AdminModule.MEMBER, operation = "会员Member取消认证商家")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult cancelBusiness(
            @PathVariable("id") Long id,
            @RequestParam("status") CertifiedBusinessStatus status) {
        Member member = memberService.findOne(id);
        notNull(member, "validate id!");
        //确认是申请取消认证状态
        isTrue(member.getCertifiedBusinessStatus() == CANCEL_AUTH, "validate member certifiedBusinessStatus!");
        //确认传入certifiedBusinessStatus值正确
        isTrue(status == NOT_CERTIFIED || status == VERIFIED, "validate certifiedBusinessStatus!");
        //member.setCertifiedBusinessApplyTime(new Date());//time
        //查询状态为申请取消认证的申请记录
        List<BusinessAuthApply> businessAuthApplyList=businessAuthApplyService.findByMemberAndCertifiedBusinessStatus(member,CANCEL_AUTH);
        if (status == VERIFIED) {
            //不允许取消
            member.setCertifiedBusinessStatus(VERIFIED);//状态改回已认证
            if(businessAuthApplyList!=null&&businessAuthApplyList.size()>0){
                businessAuthApplyList.get(0).setCertifiedBusinessStatus(VERIFIED);
            }
        } else {
            //取消认证的申请通过
            member.setCertifiedBusinessStatus(NOT_CERTIFIED);//未认证
            member.setMemberLevel(MemberLevelEnum.REALNAME);
            if(businessAuthApplyList!=null&&businessAuthApplyList.size()>0){
                businessAuthApplyList.get(0).setCertifiedBusinessStatus(NOT_CERTIFIED);
            }
            //商家认证时收取的保证金退回
            List<DepositRecord> depositRecordList=depositRecordService.findByMemberAndStatus(member,DepositStatusEnum.PAY);
            if(depositRecordList!=null&&depositRecordList.size()>0){
                BigDecimal deposit=BigDecimal.ZERO;
                for(DepositRecord depositRecord:depositRecordList){
                    depositRecord.setStatus(DepositStatusEnum.GET_BACK);
                    deposit=deposit.add(depositRecord.getAmount());
                }
                if(businessAuthApplyList!=null&&businessAuthApplyList.size()>0){
                    BusinessAuthApply businessAuthApply=businessAuthApplyList.get(0);
                    MemberWallet memberWallet=memberWalletService.findByCoinUnitAndMemberId(businessAuthApply.getBusinessAuthDeposit().getCoin().getUnit(),member.getId());
                    memberWallet.setBalance(memberWallet.getBalance().add(deposit));
                }
            }
        }
        memberService.save(member);
        return success();
    }*/

    @RequiresPermissions("member:audit-business")
    @GetMapping("{id}/business-auth-detail")
    @AccessLog(module = AdminModule.MEMBER, operation = "查询会员Member申请资料")
    @Transactional(rollbackFor = Exception.class)
    public MessageResult getBusinessAuthApply(@PathVariable("id") Long id,
                                              @RequestParam("status") CertifiedBusinessStatus status) {
        if (status == null) {
            return MessageResult.error("缺少参数");
        }
        isTrue(status == AUDITING || status == CANCEL_AUTH, "validate certifiedBusinessStatus!");
        Member member = memberService.findOne(id);
        notNull(member, "validate id!");
        //查询申请记录
        List<BusinessAuthApply> businessAuthApplyList = businessAuthApplyService.findByMemberAndCertifiedBusinessStatus(member, status);
        MessageResult result = MessageResult.success();
        if (businessAuthApplyList != null && businessAuthApplyList.size() > 0) {
            result.setData(businessAuthApplyList.get(0));
        }
        return result;
    }

    private Predicate getPredicate(MemberScreen screen) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        if (screen.getStatus() != null) {
            booleanExpressions.add(member.certifiedBusinessStatus.eq(screen.getStatus()));
        }
        if (screen.getStartTime() != null) {
            booleanExpressions.add(member.registrationTime.goe(screen.getStartTime()));
        }
        if (screen.getEndTime() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(screen.getEndTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            booleanExpressions.add(member.registrationTime.lt(calendar.getTime()));
        }

        if (!StringUtils.isEmpty(screen.getAccount())) {
            booleanExpressions.add(member.username.like("%" + screen.getAccount() + "%")
                    .or(member.mobilePhone.like(screen.getAccount() + "%"))
                    .or(member.email.like(screen.getAccount() + "%"))
                    .or(member.id.eq(Long.valueOf(screen.getAccount())))
                    .or(member.realName.like("%" + screen.getAccount() + "%")));
        }
        if (screen.getCommonStatus() != null) {
            booleanExpressions.add(member.status.eq(screen.getCommonStatus()));
        }

        if (screen.getSuperPartner() != null && !screen.getSuperPartner().equals("")) {
            booleanExpressions.add(member.superPartner.eq(screen.getSuperPartner()));
        }
        return PredicateUtils.getPredicate(booleanExpressions);
    }

    private Predicate getPredicate(MemberScreen screen, Long userId) {
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        if (screen.getStatus() != null) {
            booleanExpressions.add(member.certifiedBusinessStatus.eq(screen.getStatus()));
        }
        if (screen.getStartTime() != null) {
            booleanExpressions.add(member.registrationTime.goe(screen.getStartTime()));
        }
        if (screen.getEndTime() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(screen.getEndTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            booleanExpressions.add(member.registrationTime.lt(calendar.getTime()));
        }

        if (!StringUtils.isEmpty(screen.getAccount())) {
            booleanExpressions.add(member.username.like("%" + screen.getAccount() + "%")
                    .or(member.mobilePhone.like(screen.getAccount() + "%"))
                    .or(member.email.like(screen.getAccount() + "%"))
                    .or(member.id.eq(Long.valueOf(screen.getAccount())))
                    .or(member.realName.like("%" + screen.getAccount() + "%")));
        }
        // 筛选过滤出我邀请的人
        booleanExpressions.add(member.inviterId.eq(userId));
        if (screen.getCommonStatus() != null) {
            booleanExpressions.add(member.status.eq(screen.getCommonStatus()));
        }
        return PredicateUtils.getPredicate(booleanExpressions);
    }

    @RequiresPermissions("member:out-excel")
    @GetMapping("out-excel")
    @AccessLog(module = AdminModule.MEMBER, operation = "导出会员Member Excel")
    public MessageResult outExcel(
            @RequestParam(value = "startTime", required = false) Date startTime,
            @RequestParam(value = "endTime", required = false) Date endTime,
            @RequestParam(value = "account", required = false) String account,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<BooleanExpression> booleanExpressionList = getBooleanExpressionList(startTime, endTime, account, null);
        List list = memberService.queryWhereOrPage(booleanExpressionList, null, null).getContent();
        return new FileUtil().exportExcel(request, response, list, "member");
    }

    // 获得条件
    private List<BooleanExpression> getBooleanExpressionList(
            Date startTime, Date endTime, String account, CertifiedBusinessStatus status) {
        List<BooleanExpression> booleanExpressionList = new ArrayList();
        if (status != null) {
            booleanExpressionList.add(member.certifiedBusinessStatus.eq(status));
        }
        if (startTime != null) {
            booleanExpressionList.add(member.registrationTime.gt(startTime));
        }
        if (endTime != null) {
            booleanExpressionList.add(member.registrationTime.lt(endTime));
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(account)) {
            booleanExpressionList.add(member.username.like("%" + account + "%")
                    .or(member.mobilePhone.like(account + "%"))
                    .or(member.email.like(account + "%")));
        }
        return booleanExpressionList;
    }


    @RequiresPermissions("member:alter-publish-advertisement-status")
    @PostMapping("alter-publish-advertisement-status")
    @AccessLog(module = AdminModule.SYSTEM, operation = "禁用/解禁发布广告")
    public MessageResult publishAdvertise(@RequestParam("memberId") Long memberId,
                                          @RequestParam("status") BooleanEnum status) {
        Member member = memberService.findOne(memberId);
        if (member.getCertifiedBusinessStatus() != CertifiedBusinessStatus.VERIFIED) {
            return error("请先认证商家");
        }
        Assert.notNull(member, "玩家不存在");
        member.setPublishAdvertise(status);
        memberService.save(member);
        return success(status == BooleanEnum.IS_FALSE ? "禁止发布广告成功" : "解除禁止成功");
    }

    @RequiresPermissions("member:alter-status")
    @PostMapping("alter-status")
    @AccessLog(module = AdminModule.SYSTEM, operation = "禁用/解禁会员账号")
    public MessageResult ban(@RequestParam("status") CommonStatus status,
                             @RequestParam("memberId") Long memberId) {
        Member member = memberService.findOne(memberId);
        member.setStatus(status);
        memberService.save(member);
        log.info(">>>>>>>>>>>>>>>开始初始化BZB数据>>>>>>>");
        return success(messageSource.getMessage("SUCCESS"));
    }

    @RequiresPermissions("member:alter-transaction-status")
    @PostMapping("alter-transaction-status")
    @AccessLog(module = AdminModule.SYSTEM, operation = "禁用/解禁会员账号")
    public MessageResult alterTransactionStatus(
            @RequestParam("status") BooleanEnum status,
            @RequestParam("memberId") Long memberId) {
        Member member = memberService.findOne(memberId);
        member.setTransactionStatus(status);
        memberService.save(member);
        return success(messageSource.getMessage("SUCCESS"));
    }

    /**
     * 更改用户等级（合伙人/代理商等）
     *
     * @param superPartner
     * @param memberId
     * @return
     */
    @RequiresPermissions("member:alter-member-superpartner")
    @PostMapping("alter-member-superpartner")
    @AccessLog(module = AdminModule.SYSTEM, operation = "修改用户等级")
    public MessageResult alterSuperPartner(
            @RequestParam("superPartner") String superPartner,
            @RequestParam("memberId") Long memberId) {
        Member member = memberService.findOne(memberId);
        member.setSuperPartner(superPartner);
        memberService.save(member);
        return success(messageSource.getMessage("SUCCESS"));
    }

    /**
     * 查询代理商列表
     *
     * @param pageModel
     * @param screen
     * @return
     */
    @RequiresPermissions("member:page-query-super")
    @PostMapping("page-query-super")
    @ResponseBody
    @AccessLog(module = AdminModule.MEMBER, operation = "分页查找会员Member")
    public MessageResult pageSuperPartner(
            PageModel pageModel,
            MemberScreen screen) {
        screen.setSuperPartner("1"); // 默认选择代理商
        Predicate predicate = getPredicate(screen);
        Page<Member> all = memberService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    /**
     * 查询代理商邀请用户列表
     *
     * @param pageModel
     * @param screen
     * @param userId
     * @return
     */
    @RequiresPermissions("member:supermember-page-query")
    @PostMapping(value = "/supermember-page-query")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public MessageResult pageSuperMember(
            PageModel pageModel,
            MemberScreen screen,
            Long userId) {
        // 检查用户是否是代理商
        Member checkMember = memberService.findOne(userId);
        if (!checkMember.getSuperPartner().equals("1")) {
            return error("您不是代理商！");
        }
        Predicate predicate = getPredicate(screen, userId);
        Page<Member> all = memberService.findAll(predicate, pageModel.getPageable());
        return success(all);
    }

    @RequiresPermissions("member:set-inviter")
    @PostMapping("setInviter")
    @AccessLog(module = AdminModule.MEMBER, operation = "设置邀请人")
    public MessageResult setInviter(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "inviterId") Long inviterId) throws Exception {
        Member member = memberService.findOne(id);
        notNull(member, "validate id!");
        Member pMember = memberService.findOne(inviterId);
        notNull(member, "validate id!");
        if (member.getInviterId() != null) {
            return error("已存在邀请人");
        }
        memberEvent.setMemberInviter(member, pMember);
        return success();
    }
}
