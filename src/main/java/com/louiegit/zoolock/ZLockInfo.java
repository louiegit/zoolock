package com.louiegit.zoolock;

/**
 * @author tianxiang.luo
 * @version 2017/12/6 14:48
 */
public class ZLockInfo {


    private static final String ROOT = "/lock-";

    private ZLockType type;

    private String path;

    public ZLockInfo(String path, ZLockType type) {
        this.path = ROOT + type.getName()+"/"+ path;
    }

    public ZLockType getType() {
        return type;
    }

    public void setType(ZLockType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
