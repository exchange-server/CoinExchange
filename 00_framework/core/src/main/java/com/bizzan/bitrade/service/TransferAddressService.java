package com.bizzan.bitrade.service;

import com.bizzan.bitrade.constant.CommonStatus;
import com.bizzan.bitrade.dao.CoinDao;
import com.bizzan.bitrade.dao.TransferAddressDao;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.TransferAddress;
import com.bizzan.bitrade.service.Base.TopBaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jammy
 * @date 2020年02月27日
 */
@Service
public class TransferAddressService extends TopBaseService<TransferAddress, TransferAddressDao> {

    @Resource
    private CoinDao coinDao;

    @Override
    @Resource
    public void setDao(TransferAddressDao dao) {
        super.setDao(dao);
    }

    public List<TransferAddress> findByUnit(String unit) {
        Coin coin = coinDao.findByUnit(unit);
        return dao.findAllByStatusAndCoin(CommonStatus.NORMAL, coin);
    }

    public List<TransferAddress> findByCoin(Coin coin) {
        return dao.findAllByStatusAndCoin(CommonStatus.NORMAL, coin);
    }

    public TransferAddress findOnlyOne(Coin coin, String address) {
        return dao.findByAddressAndCoin(address, coin);
    }

}
