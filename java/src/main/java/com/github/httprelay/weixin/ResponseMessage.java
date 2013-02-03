package com.github.httprelay.weixin;

import java.io.Serializable;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 2:50 PM
 */
public class ResponseMessage implements Serializable {
    private String toUsername;
    private String fromUsername;
    private int createTime;
    private int funcFlag;

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getFuncFlag() {
        return funcFlag;
    }

    public void setFuncFlag(int funcFlag) {
        this.funcFlag = funcFlag;
    }
}
