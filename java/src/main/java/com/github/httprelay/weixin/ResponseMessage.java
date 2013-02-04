package com.github.httprelay.weixin;

import java.io.Serializable;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 2:50 PM
 */
public abstract class ResponseMessage implements Serializable {
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

    public abstract String encodeContentToXml();

    public abstract String getMessageType();

    public String encodeToXml() {
        StringBuilder sb = new StringBuilder("<xml>");
        sb.append("<ToUserName><![CDATA[");
        sb.append(toUsername);
        sb.append("]]></ToUserName>");
        sb.append("<FromUserName><![CDATA[");
        sb.append(fromUsername);
        sb.append("]]></FromUserName>");
        sb.append("<CreateTime>");
        sb.append(createTime);
        sb.append("</CreateTime>");
        sb.append("<MsgType>");
        sb.append(getMessageType());
        sb.append("</MsgType>");
        sb.append(encodeContentToXml());
        sb.append("</xml>");
        return sb.toString();
    }
}
