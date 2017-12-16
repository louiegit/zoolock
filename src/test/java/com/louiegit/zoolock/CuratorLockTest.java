package com.louiegit.zoolock;

import com.louiegit.zoolock.lock.Lock;
import com.louiegit.zoolock.lock.LockInfo;
import com.louiegit.zoolock.curator.CuratorLock;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author tianxiang.luo
 * @version 2017/12/7 10:58
 */
public class CuratorLockTest {

    @Test
    public void testLock(){
        LockInfo info = new LockInfo("louie");
        Lock cLock = new CuratorLock(info);
        cLock.lock();
        Assert.assertTrue(cLock.isLock());
        cLock.unlock();
        Assert.assertFalse(cLock.isLock());
    }

}
