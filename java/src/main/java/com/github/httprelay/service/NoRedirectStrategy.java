package com.github.httprelay.service;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 3:19 PM
 */
public class NoRedirectStrategy implements RedirectStrategy {
    @Override
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        return false;
    }

    @Override
    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
        return null;
    }
}
