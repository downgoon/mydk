package xyz.downgoon.mydk.util;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @title RetryTemplate
 * @author downgoon
 * @date 2014-7-17
 * @version 1.0
 */
public class RetryTemplate {
    
    private static final Logger LOG = LoggerFactory.getLogger(RetryTemplate.class);
    
    /** 重试的次数和间隔 */
    private int[] retryIntervalSec;
    
    private Object delegate;
    
    private String logTag = "";
    
    public RetryTemplate(Object delegate, String logTag) {
        this(delegate, logTag, 1,2,4);
    }
    
    public RetryTemplate(Object delegate) {
        this(delegate,"", 1,2,4);
    }
    
    /***
     * 为指定代理对象构造重试模板
     * @param   delegate    重试方法作用的目标对象
     * @param   retryIntervalSec  重试次数及其时间间隔（单位：秒）   
     * */
    public RetryTemplate(Object delegate, String logTag, int... retryIntervalSec) {
        super();
        this.delegate = delegate;
        this.logTag = logTag;
        this.retryIntervalSec = retryIntervalSec;
    }

    /**
     * 通用重试方法：通过指定方法名和参数来调用方法
     * @param   methodName  重试的方法名称
     * @param   方法的参数
     * @throws  如果调用期间，有异常，则表示失败，会按给定的次数重试；如果依然失败，抛出最后一次的异常。
     * @return	返回底层代理对象的方法返回值
     * */
    public Object retry(String methodName, Object... args) throws Exception {
        Method method = parseMethod(methodName, args);
        return retryMethod(method, delegate, args);
    }
    
    private Method parseMethod(String methodName, Object... args) throws NoSuchMethodException {
        if (args == null || args.length == 0) {
            return delegate.getClass().getMethod(methodName);
        } else {
            Class<?>[] paramTypes = new Class<?>[args.length];
            for(int i=0; i<paramTypes.length; i++) {
                paramTypes[i] = args[i].getClass(); 
            }
            return delegate.getClass().getMethod(methodName,paramTypes);
        }
    }

    /* Retry Template-Callback */
    protected Object retryMethod(Method method, Object instance, Object... args) throws Exception {
        int retryCnt = 1;
        Exception lastException = null;
        do {
            try {
                Object r = method.invoke(instance, args);//method callback
                LOG.info("{} method {} invoke retry succ at time {}",logTag, method.getName(), retryCnt);
                return r;

            } catch (java.lang.reflect.InvocationTargetException targetException) {
                Throwable targetThrowable = targetException.getTargetException();
                if (targetException instanceof Exception) {//TargetException会被转译
                    lastException = (Exception) targetThrowable;
                } else {
                    lastException = targetException;
                }

            } catch (Exception e) {
                lastException = e;
            }
            if (lastException != null) {
                LOG.warn("{} method {} invoke fail at time {}, retry ...",logTag, method.getName(), retryCnt, lastException);
                if (retryCnt < retryIntervalSec.length) {//最后一次不用睡眠
                    Thread.sleep(1000L * retryIntervalSec[retryCnt]);//retry interval
                }
                retryCnt++;
            }

        } while (retryCnt < retryIntervalSec.length);

        throw lastException;
    }

}
