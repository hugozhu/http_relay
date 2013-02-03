package com.github.httprelay.weixin;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 2:32 PM
 */
public class TextRequestMessage extends RequestMessage {

    public TextRequestMessage(String to,String from, int time,String content) {
        super(to,from, time);
        this.content = content;
    }

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getMessageType() {
        return "text";
    }

    @Override
    public String encodeContentToXml() {
        return "<Content><![CDATA["+content+"]]></Content>";
    }
}
