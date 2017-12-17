# ZLock 
---
### Quickstart 快速上手
#### [Release Page zoolock-1.0.2.jar](https://github.com/louiegit/zoolock/releases)

#### CuratorLock Usage ( Distributed lock based on curator framework )
```java
        //创建锁节点信息
        LockInfo info = new LockInfo("louie");
        Lock cLock = new CuratorLock(info);
        cLock.lock();
        cLock.unlock();
```
#### ZooLock Usage ( Distributed lock based on zkClient )
```java
        //创建锁节点信息
        LockInfo lockInfo = new LockInfo("louie");
        //获取lock对象
        Lock zLock = new ZooLock(lockInfo);
        //获取锁
        zLock.lock();
        //释放锁
        zLock.unlock();
```
---

### ZooLock锁结构
  /-------------- (ROOT)
  |----/locks
  |-------|
  |-------|--/locks/$lockType1
  |-------|---------|
  |-------|---------|---/locks/$lockType1/$key
  |-------|---------|-------------|
  |-------|---------|-------------|---/locks/$lockType1/$key/$subKey
  |-------|---------|-------------|-----------------|
  |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_00000001
  |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_00000002
  |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_00000003
  |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_00000004
  |-------|---------|-------------|-----------------|---/locks/$lockType1/$key/$subkey/_000000..
 
---

### lock流程
a、在获取分布式锁的时候在locker节点下创建临时顺序节点，释放锁的时候删除该临时节点。

b、客户端调用createNode方法在locker下创建临时顺序节点，然后调用getChildren(“locker”)来获取locker下面的所有子节点，注意此时不用设置任何Watcher。

c、客户端获取到所有的子节点path之后，如果发现自己创建的子节点序号最小，那么就认为该客户端获取到了锁。

d、如果发现自己创建的节点并非locker所有子节点中最小的，说明自己还没有获取到锁，此时客户端需要找到比自己小的那个节点，然后对其调用exist()方法，同时对其注册事件监听器。

e、之后，让这个被关注的节点删除，则客户端的Watcher会收到相应通知，此时再次判断自己创建的节点是否是locker子节点中序号最小的，如果是则获取到了锁，如果不是则重复以上步骤继续获取到比自己小的一个节点并注册监听。

