package com.louiegit.zoolock.common;

/**
 * @author tianxiang.luo
 * @version 2017/12/6 14:54
 */
public interface Lock {


    /**
     * 获取zk锁
     * @return
     */
    boolean lock();

    /**
     * 超时锁
     * @param timeout 超时时间单位milliseconds
     * @return
     */
    boolean lock(long timeout);

    /**
     * 释放锁
     * @return
     */
    boolean unlock();

    /**
     * 判断是否获取锁
     * @return
     */
    boolean isLock();

}
