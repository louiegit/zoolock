package com.louiegit.zoolock.lock;

/**
 * @author tianxiang.luo
 * @version 2017/12/6 14:48
 */
public class LockInfo {


    public static final String DEFAULT_SOCKET = "127.0.0.1:2181";

    public static final String ROOT = "/lock";

    private String path;

    public LockInfo(String path) {
        this.path = ROOT +"/"+ path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
