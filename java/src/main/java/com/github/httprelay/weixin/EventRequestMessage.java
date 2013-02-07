package com.github.httprelay.weixin;

/**
 * User: hugozhu
 * Date: 2/7/13
 * Time: 11:03 PM
 */
public class EventRequestMessage extends RequestMessage {
    private String event;
    private double latitude;
    private double longitude;
    private double precision;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    @Override
    public String getMessageType() {
        return "event";
    }

    public EventRequestMessage(String to,String from, int time) {
        super(to,from, time);
    }

    @Override
    public String encodeContentToXml() {
        return  "<Event><![CDATA["+event+"]]></Event>"+
                "<Latitude>"+latitude+"</Latitude>"+
                "<Longitude>"+longitude+"</Longitude>"+
                "<Precision>"+precision+"</Precision>";
    }
}
