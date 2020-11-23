package com.sutpc.demo.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 存储接收消息时间数据
 */
public class CacheModel {

    private static final CacheModel cacheModel = new CacheModel();

    private CacheModel(){}

    public static CacheModel getCacheModel(){
        return cacheModel;
    }

    private static AtomicLong busTime = new AtomicLong(System.currentTimeMillis());

    private static AtomicLong carTime = new AtomicLong(System.currentTimeMillis());

    public static AtomicLong getBusTime() {
        return busTime;
    }

    public static void setBusTime(Long busTime) {
        CacheModel.busTime.getAndSet(busTime);
    }

    public static AtomicLong getCarTime() {
        return carTime;
    }

    public static void setCarTime(Long carTime) {
        CacheModel.carTime.set(carTime);
    }
}
