package com.louiegit.zoolock;

import com.louiegit.zoolock.lock.Lock;
import com.louiegit.zoolock.lock.LockInfo;
import com.louiegit.zoolock.zookeeper.ZooLock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tianxiang.luo
 * @version 2017/12/7 10:58
 */
public class ZooLockTest {


    @Test
    public void testLock(){
        LockInfo lockInfo = new LockInfo("louie");
        Lock zLock = new ZooLock(lockInfo);
        zLock.lock();
        Assert.assertTrue(zLock.isLock());
        zLock.lock();
        zLock.unlock();
        Assert.assertFalse(zLock.isLock());
    }
}
