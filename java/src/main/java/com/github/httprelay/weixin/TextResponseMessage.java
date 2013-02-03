package com.github.httprelay.weixin;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 2:56 PM
 */
public class TextResponseMessage extends ResponseMessage {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
