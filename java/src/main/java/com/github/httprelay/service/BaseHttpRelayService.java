package com.github.httprelay.service;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 9:39 PM
 */
public abstract class BaseHttpRelayService implements HttpRelayService {
    protected int maxResposneBytes = 1024*5;
    protected int avgResponseSize = 256;
    protected int timeout = 10000; //10 seconds
    protected int maxPerRoute = 10;
    protected int maxTotal = 1000;

    public void configHttpCLientParams(HttpParams params) {
        params
            .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout)
            .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout)
            .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
            .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
    }

    public HttpUriRequest createRequest(URI uri, String postdata) {
        try {
            final HttpUriRequest request;
            if (postdata!=null) {
                request = new HttpPost(uri);
                ((HttpPost) request).setEntity(new ByteArrayEntity(postdata.getBytes("UTF-8")));
            } else {
                request = new HttpGet(uri);
            }
            return request;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
