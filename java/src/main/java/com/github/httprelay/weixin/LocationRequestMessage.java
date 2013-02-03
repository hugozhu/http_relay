package com.github.httprelay.weixin;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 2:34 PM
 */
public class LocationRequestMessage extends RequestMessage {
    private double x;
    private double y;
    private int scale;
    private String label;

    public LocationRequestMessage(String to,String from, int time) {
        super(to,from, time);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getMessageType() {
        return "location";
    }

    @Override
    public String encodeContentToXml() {
        return  "<Location_X>"+x+"</Location_X>"+
                "<Location_Y>"+y+"</Location_Y>"+
                "<Scale>"+scale+"</Scale>"+
                "<Label><![CDATA["+label+"]]></Label>";
    }
}
