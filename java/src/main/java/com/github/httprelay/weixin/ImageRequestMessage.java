package com.github.httprelay.weixin;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 2:35 PM
 */
public class ImageRequestMessage extends RequestMessage {
    private String picUrl;

    public ImageRequestMessage(String to,String from, int time, String picUrl) {
        super(to,from, time);
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public String getMessageType() {
        return "image";
    }

    @Override
    public String encodeContentToXml() {
        return "<PicUrl><![CDATA["+picUrl+"]]></PicUrl>";
    }
}
