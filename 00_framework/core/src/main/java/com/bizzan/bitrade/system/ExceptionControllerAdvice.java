package com.bizzan.bitrade.system;

import com.bizzan.bitrade.service.LocaleMessageSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bizzan.bitrade.exception.GeeTestException;
import com.bizzan.bitrade.exception.InformationExpiredException;
import com.bizzan.bitrade.util.MessageResult;

/**
 * @author Jammy
 * @date 2020年12月23日
 */
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    /**
     * 拦截乐观锁失败异常
     *
     * @param ex
     * @return
     */
    @Autowired
    private LocaleMessageSourceService msService;

    @ResponseBody
    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    public MessageResult myErrorHandler(ObjectOptimisticLockingFailureException ex) {
        ex.printStackTrace();
        log.info(">>>拦截乐观锁失败异常>>", ex);
        MessageResult result = MessageResult.error(6000, msService.getMessage("DATA_OUT_OF_DATE"));
        return result;
    }

    /**
     * 拦截参数异常
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = IllegalArgumentException.class)
    public MessageResult myErrorHandler(IllegalArgumentException e) {
        e.printStackTrace();
        log.info(">>>拦截参数异常>>", e);
        MessageResult result = MessageResult.error(e.getMessage());
        return result;
    }

    /**
     * 拦截绑定参数异常
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = ServletRequestBindingException.class)
    public MessageResult myErrorHandler(ServletRequestBindingException e) {
        e.printStackTrace();
        log.info(">>>拦截绑定参数异常>>", e);
        MessageResult result = MessageResult.error(3000, msService.getMessage("PARAMETER_BINDING_ERROR"));
        return result;
    }


//    @ResponseBody
//    @ExceptionHandler(value = RedisConnectionFailureException.class)
//    public MessageResult myErrorHandler(RedisConnectionFailureException e) {
//        e.printStackTrace();
//        MessageResult result = MessageResult.error(2000, "NETWORK_ABNORMALITY");
//        return result;
//    }

    /**
     * 拦截数据过期异常
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = InformationExpiredException.class)
    public MessageResult myErrorHandler(InformationExpiredException ex) {
        ex.printStackTrace();
        log.info(">>>拦截数据过期异常>>", ex);
        MessageResult result = MessageResult.error(msService.getMessage("DATA_OUT_OF_DATE"));
        return result;
    }

    /**
     * 拦截极验证验证失败异常
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = GeeTestException.class)
    public MessageResult myErrorHandler(GeeTestException ex) {
        ex.printStackTrace();
        log.info(">>>拦截极验证验证失败异常>>", ex);
        MessageResult result = MessageResult.error(ex.getMessage());
        return result;
    }

    /**
     * 拦截异常
     *
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public MessageResult myErrorHandler(Exception ex) {
        ex.printStackTrace();
        log.info(">>>拦截异常>>", ex);
        MessageResult result = MessageResult.error(msService.getMessage("UNKNOWN_ERROR"));
        return result;
    }

    /**
     * @param
     * @return
     * @author Jammy
     * @description 错误请求方式异常  HttpRequestMethodNotSupportedException
     * @date 2019/2/28 17:32
     */
    @ResponseBody
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public MessageResult httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ex.printStackTrace();
        log.info(">>>错误请求方式异常>>", ex);
        String methods = "";
        //支持的请求方式
        String[] supportedMethods = ex.getSupportedMethods();
        for (String method : supportedMethods) {
            methods += method;
        }
        MessageResult result = MessageResult.error("Request method " + ex.getMethod() + "  not supported !" +
                " supported method : " + methods + "!");
        return result;
    }
}
