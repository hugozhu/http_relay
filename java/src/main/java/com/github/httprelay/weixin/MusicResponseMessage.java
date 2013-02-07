package com.github.httprelay.weixin;

/**
 * User: hugozhu
 * Date: 2/7/13
 * Time: 10:52 PM
 */
public class MusicResponseMessage extends ResponseMessage {
    private String title;
    private String description;
    private String musicUrl;
    private String hqMusicUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getHqMusicUrl() {
        return hqMusicUrl;
    }

    public void setHqMusicUrl(String hqMusicUrl) {
        this.hqMusicUrl = hqMusicUrl;
    }

    @Override
    public String getMessageType() {
        return "music";
    }

    @Override
    public String encodeContentToXml() {
        StringBuilder sb = new StringBuilder("");
        sb.append("<Music>");
        sb.append("<Title><![CDATA["+title+"]]></Title>");
        sb.append("<Description><![CDATA["+description+"]]></Description>");
        sb.append("<MusicUrl><![CDATA["+musicUrl+"]]></MusicUrl>");
        sb.append("<HQMusicUrl><![CDATA["+hqMusicUrl+"]]></HQMusicUrl>");
        sb.append("</Music>");
        return sb.toString();
    }
}

