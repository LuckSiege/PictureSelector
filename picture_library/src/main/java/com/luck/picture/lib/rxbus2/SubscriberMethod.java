package com.luck.picture.lib.rxbus2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * Created by gorden on 2016/7/23.
 */
public class SubscriberMethod {
    public Method method;
    public ThreadMode threadMode;
    public Class<?> eventType;
    public Object subscriber;
    public int code;

    public SubscriberMethod(Object subscriber, Method method, Class<?> eventType, int code, ThreadMode threadMode) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
        this.subscriber = subscriber;
        this.code = code;
    }


    /**
     * 调用方法
     * @param o 参数
     */
    public void invoke(Object o){
        try {
            Class[] parameterType = method.getParameterTypes();
            if(parameterType != null && parameterType.length == 1){
                method.invoke(subscriber, o);
            }else if(parameterType == null || parameterType.length == 0){
                method.invoke(subscriber);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
