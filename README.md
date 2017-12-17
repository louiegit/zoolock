# ZLock 
distributed lock based on curator framework 
---
### Quickstart 快速上手
#### [Release Page zoolock-1.0.2.jar](https://github.com/louiegit/zoolock/releases)
#### CuratorLock Usage
```java
        LockInfo info = new LockInfo("louie");
        Lock cLock = new CuratorLock(info);
        cLock.lock();
        cLock.unlock();
```
distributed lock based on zkClient
#### ZooLock Usage
```java
        LockInfo lockInfo = new LockInfo("louie");
        Lock zLock = new ZooLock(lockInfo);
        zLock.lock();
        Assert.assertTrue(zLock.isLock());
        zLock.unlock();
        Assert.assertFalse(zLock.isLock());
```
---
### ZooLock锁结构
* /-------------- (ROOT)
 * |----/locks
 * |       |
 * |-------|--/locks/$lockType1
 * |       |         |
 * |-------|---------|---/locks/$lockType1/$key
 * |       |         |             |
 * |-------|---------|-------------|---/locks/$lockType1/$key/$subKey
 * |       |         |             |                 |
 * |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_00000001
 * |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_00000002
 * |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_00000003
 * |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_00000004
 * |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_000000..
 
