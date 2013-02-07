package com.github.httprelay.weixin;

import java.io.Serializable;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 2:24 PM
 */
public abstract class RequestMessage implements Serializable {
    private String toUsername;
    private String fromUsername;
    private int createTime;
    private long msgId;

    public RequestMessage(String to,String from, int time) {
        this.toUsername = to;
        this.fromUsername = from;
        this.createTime = time;
    }

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

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public abstract String getMessageType();

    public abstract String encodeContentToXml();

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
        sb.append("<MsgId>");
        sb.append(getMsgId());
        sb.append("</MsgId>");
        sb.append("</xml>");
        return sb.toString();
    }
}
