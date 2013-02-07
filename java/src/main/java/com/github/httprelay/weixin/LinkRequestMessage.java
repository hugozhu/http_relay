package com.github.httprelay.weixin;

/**
 * User: hugozhu
 * Date: 2/7/13
 * Time: 11:00 PM
 */
public class LinkRequestMessage extends RequestMessage {
    private String title;
    private String description;
    private String url;


    public LinkRequestMessage(String to,String from, int time) {
        super(to,from, time);
    }


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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getMessageType() {
        return "link";
    }

    @Override
    public String encodeContentToXml() {
        return  "<Title><![CDATA["+title+"]]></Title>"+
                "<Description><![CDATA["+description+"]]></Description>"+
                "<Url><![CDATA["+url+"]]></Url>";
    }
}
