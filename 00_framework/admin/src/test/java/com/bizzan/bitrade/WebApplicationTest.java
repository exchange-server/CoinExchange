package com.bizzan.bitrade;

import com.alibaba.fastjson.JSON;
import com.bizzan.bitrade.core.Menu;
import com.bizzan.bitrade.dao.MemberDao;
import com.bizzan.bitrade.dao.MemberWalletDao;
import com.bizzan.bitrade.dao.OrderDao;
import com.bizzan.bitrade.entity.TurnoverStatistics;
import com.bizzan.bitrade.service.AdminService;
import com.bizzan.bitrade.service.AdvertiseService;
import com.bizzan.bitrade.service.BusinessCancelApplyService;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.ExchangeOrderDetailService;
import com.bizzan.bitrade.service.MemberPromotionService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.OrderService;
import com.bizzan.bitrade.service.RewardRecordService;
import com.bizzan.bitrade.service.SysAdvertiseService;
import com.bizzan.bitrade.service.SysRoleService;
import com.bizzan.bitrade.service.TestQueryDSLService;
import com.bizzan.bitrade.service.WithdrawRecordService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class WebApplicationTest {

    @Resource
    private MemberWalletDao memberWalletDao;

    @Resource
    private KafkaTemplate kafkaTemplate;

    @Resource
    private AdvertiseService advertiseService;
    @Resource
    private TestQueryDSLService testQueryDSLService;
    @Resource
    private WithdrawRecordService withdrawRecordService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private ExchangeOrderDetailService exchangeOrderDetailService;
    @Resource
    private SysAdvertiseService sysAdvertiseService;
    @Resource
    private SysRoleService roleService;
    @Resource
    private MemberWalletService memberWalletService;
    @Resource
    private CoinService coinService;
    @Resource
    private RewardRecordService rewardRecordService;
    @Resource
    private MemberPromotionService memberPromotionService;
    @Resource
    private MemberService memberService;
    @Resource
    private OrderService orderService;
    @Resource
    private OrderDao orderDao;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private AdminService adminService;
    @Resource
    private BusinessCancelApplyService businessCancelApplyService;
    @Resource
    private MemberDao memberDao;
    @Resource
    private RestTemplate restTemplate;

    //@Test
    public void testConfig() {
        System.out.print(advertiseService.findOne(1L).getCreateTime());
    }

    @Test
    public void testRpcWallet() {
        String urlBalance = "http://SERVICE-RPC-BTC" + "/rpc/balance";
        ResponseEntity<MessageResult> result = restTemplate.getForEntity(urlBalance, MessageResult.class);
        log.info("result={}", result);
        if (result.getStatusCode().value() == 200) {
            MessageResult mr = result.getBody();
            if (mr.getCode() == 0) {
                String balance = mr.getData().toString();
                BigDecimal bigDecimal = new BigDecimal(balance);
                log.info("BTC" + "热钱包余额:", bigDecimal);
            }
        }
    }

    @Test
    public void testStatistics() {
        Date date = memberDao.getStartRegistrationDate();

        System.out.println(date);
		/*int a = memberDao.getApplicationNum("2018-03-27");
		System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,"+a);*/
	/*	Admin admin = adminService.login("root","47943eeeab5ed28f8a93f7fb77ec5111");
		System.out.println(admin);*/
		/*try {
			MemberStatisticsJob job = new MemberStatisticsJob() ;
			Date startDate = DateUtil.YYYY_MM_DD.parse("2018-03-18");
			Date endDate = DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);
			while(!startDate.after(endDate)){
				String dateStr = DateUtil.YYYY_MM_DD.format(startDate);
				job.statisticsMember(dateStr,startDate);
				startDate = DateUtil.dateAddDay(startDate,1);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}*/
    }

    @Test
    public void testSum() throws ParseException {
		/*PageModel pageModel = new PageModel() ;
		pageModel.setSort();
		Page<RegisterPromotionVO> page = memberPromotionService.getPromotionDetails(30L ,pageModel) ;
		//System.out.println(memberWalletDao.getWalletAllBalance("Bitcoin"));
		System.out.println(page.getContent());*/
        //kafkaTemplate.send("test","aaaa");
		/*System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:"+memberService.getRegistrationNum("2018-03-19"));
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:"+memberService.getApplicationNum("2018-03-19"));
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa:"+memberService.getBusinessNum("2018-03-19"));*/
		/*for(Map<String,Object> map : orderService.getOtcOrderStatistics()){
			System.out.println(map.keySet()+"-----------------------"+map.values());
		}*/

        /*System.out.println(orderDao.getOtcTurnoverAmount());*/
        /*	Date date = *//*DateUtil.dateAddDay(DateUtil.getCurrentDate(),-1);*//* new Date();
		String dateStr = DateUtil.getFormatTime(DateUtil.YYYY_MM_DD,date);*/
        String dateStr = "2018-03-20";
        Date date = DateUtil.YYYY_MM_DD.parse(dateStr);
        /**
         * 法币成交
         *
         */
        List<Object[]> list = orderService.getOtcOrderStatistics(dateStr);
        for (Object[] objects : list) {
            System.out.println(objects[0] + "----------" + objects[1] + "----------" + objects[2] + "--------" + objects[3]);
            TurnoverStatistics turnoverStatistics = new TurnoverStatistics();
            turnoverStatistics.setDate(date);
            turnoverStatistics.setYear(DateUtil.getDatePart(date, Calendar.YEAR));
            turnoverStatistics.setMonth(DateUtil.getDatePart(date, Calendar.MONTH) + 1);
            turnoverStatistics.setDay(DateUtil.getDatePart(date, Calendar.DAY_OF_MONTH));
            turnoverStatistics.setUnit(objects[0].toString());
			/*turnoverStatistics.setAmount(new Decimal128((BigDecimal) objects[2]));
			turnoverStatistics.setFee(new Decimal128((BigDecimal) objects[3]));*/
            //turnoverStatistics.setType(TransactionTypeEnum.OTC);
            System.out.println(turnoverStatistics);
            mongoTemplate.insert(turnoverStatistics, "turnover_statistics");
        }
        Query query = new Query();
        List list1 = mongoTemplate.find(query, TurnoverStatistics.class);
        System.out.println(list.get(0));

    }


    @Test
    public void testExcelOut() {
        File file = new File("D://demo1.xls");
        try {
			/*if(!file.exists()){
				file.createNewFile();
			}
			OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			List<MemberWallet> list = memberWalletService.findAllByCoin(coinService.findOne("Bitcoin"));
			*//*LinkedHashMap<String,String> fieldMap = new LinkedHashMap<>();
			fieldMap.put("memberId","会员id");
			fieldMap.put("balance","余额");
			fieldMap.put("coin.unit","币种");*//*
			Field[] fields = MemberWallet.class.getDeclaredFields();
			ExcelUtil.listToExcel(list,fields,65535,out);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPage() throws Exception {
		/*SysRole role = roleService.findOne(28L);
		System.out.println(role);*/
	/*	List<com.querydsl.core.types.Predicate> predicates = new ArrayList<>();
		//predicates.add(QMember.member.id.eq(33L));
		Page<MemberWalletDTO> page = memberWalletService.joinFind(predicates, QMember.member, QMemberWallet.memberWallet,new PageModel());
		System.out.println(page);*/
        //System.out.println(page.getContent());
        //System.out.println(sysAdvertiseService.getMaxSort());
        System.out.println(coinService.getAllCoinName());
        //sysAdvertiseService.deleteBatch(new String[]{"111","222"});
        //exchangeOrderDetailService.add();
		/*EntityPage<ExchangeOrderDetailAggregation> result = exchangeOrderDetailService.findAllByPageNo(1,15);
		System.out.println(result);
		System.out.println("orderId-----------:"+result.getList().get(2).getOrderId());
		System.out.println(result.getList());*/
    }

    @Test
    public void test() throws Exception {
        withdrawRecordService.test();
    }

    @Test
    public void testPermissions() {
        List<Menu> list = sysRoleService.toMenus(sysRoleService.getAllPermission(), 0L);
        System.out.println(JSON.toJSONString(list));
    }
}
