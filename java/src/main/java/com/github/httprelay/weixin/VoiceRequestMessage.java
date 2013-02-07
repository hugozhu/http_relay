package com.github.httprelay.weixin;

/**
 * User: hugozhu
 * Date: 2/7/13
 * Time: 11:11 PM
 */
public class VoiceRequestMessage extends RequestMessage {
    private String mediaId;

    public VoiceRequestMessage(String to,String from, int time, String mediaId) {
        super(to,from, time);
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public String getMessageType() {
        return "voice";
    }

    @Override
    public String encodeContentToXml() {
        return "<MediaId><![CDATA["+ mediaId +"]]></MediaId>";
    }
}

