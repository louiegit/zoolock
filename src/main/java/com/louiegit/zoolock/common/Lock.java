package com.louiegit.zoolock.common;

/**
 * @author tianxiang.luo
 * @version 2017/12/6 14:54
 */
public interface Lock {


    /**
     * 上锁
     */
    boolean lock();

    /**
     * 上锁
     */
    boolean lock(long timeout);

    /**
     * 解锁
     */
    boolean unlock();

    /**
     * 判断锁
     */
    boolean isLock();

}
