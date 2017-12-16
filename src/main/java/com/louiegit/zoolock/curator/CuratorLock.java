package com.louiegit.zoolock.curator;

import com.louiegit.zoolock.common.Lock;
import com.louiegit.zoolock.common.LockInfo;
import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * @author tianxiang.luo
 * @version 2017/12/6 15:02
 */
public class CuratorLock implements Lock {

    private InterProcessLock processLock;

    private String path;

    private static final Integer RETRY_MAX = Integer.MAX_VALUE;

    private static final CuratorFramework client = CuratorFrameworkFactory.newClient(LockInfo.DEFAULT_SOCKET, new RetryPolicy() {
        @Override
        public boolean allowRetry(int retry, long sleepTime, RetrySleeper retrySleeper) {
            //重试次数超过Integer.MAX_VALUE便放弃重试
            if (retry > RETRY_MAX){
                return false;
            }
            try {
                retrySleeper.sleepFor(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    });

    public CuratorLock(LockInfo zLockInfo) {
        path = zLockInfo.getPath();
        //client操作需要先start
        client.start();
        //zk锁
        processLock = new InterProcessMutex(client,path);
    }

    @Override
    public boolean lock() {
        try {
            //acquire zk lock
            processLock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean lock(long timeout) {
        try {
            //acquire zk lock with timeout
            processLock.acquire(timeout,TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean unlock() {
        try {
            //unlock zk lock
            processLock.release();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean isLock() {
        //check zk lock is acquire
        return processLock.isAcquiredInThisProcess();
    }

}
