package com.peakmain.baselibrary.rx;


import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author Leehor
 * 版本：
 * 创建日期：2019/3/18
 * 描述：订阅者
 */
public class RxBus {
    private static final String TAG = "RxBus";

    //订阅者集合
    private Set<Object> subscribers;

    private RxBus() {
        subscribers = new CopyOnWriteArraySet<>();
    }

    /**
     * 注册DataBusSubscriber
     */
    public synchronized void register(Object subcriber) {
        subscribers.add(subcriber);
    }

    /**
     * 销毁DataBusSubscriber
     */
    public synchronized void unregister(Object subscriber) {
        subscribers.remove(subscriber);
    }

    /**
     * 单例模式
     */
    public static RxBus getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static final RxBus instance = new RxBus();
    }

    public void chainProcess(Function func) {
        Observable.just("")
                .compose(RxSchedulers.applySchedulers())
                .map(func)//包装处理过程
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object data) throws Exception {
                        if (data == null) {
                            return;
                        }
                        RxBus.this.send(data);
                    }
                });
    }

    private void send(Object data) {
        for (Object subscriber : subscribers) {
            //扫描注解，将数据发送到注册的对象的标记方法
            callMethodByAnnotiation(subscriber, data);
        }
    }

    /**
     * 反射获取对象方法列表，判断
     * 1.是否被注解修饰
     * 2.参数类型是否和data类型一致
     */
    private void callMethodByAnnotiation(Object target, Object data) {
        try {
            //获得所有方法
            Method[] methodArray = target.getClass().getDeclaredMethods();
            for (int i = 0; i < methodArray.length; i++) {
                //被@RegisterBus修饰的方法
                if (methodArray[i].isAnnotationPresent(RegisterBus.class)) {
                    //构造方法中的参数自己发送的参数类型需要一致
                    Class paramType = methodArray[i].getParameterTypes()[0];
                    if (data.getClass().getName().equals(paramType.getName())) {
                        //参数类型和data类型一样,调用此方法
                        methodArray[i].invoke(target, new Object[]{data});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
