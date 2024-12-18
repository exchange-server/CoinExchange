package com.bizzan.bitrade.service;

import com.bizzan.bitrade.dao.FeedbackDao;
import com.bizzan.bitrade.entity.Feedback;
import com.bizzan.bitrade.service.Base.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Jammy
 * @date 2020年03月19日
 */
@Service
public class FeedbackService extends BaseService {
    @Resource
    private FeedbackDao feedbackDao;

    public Feedback save(Feedback feedback) {
        return feedbackDao.save(feedback);
    }
}
