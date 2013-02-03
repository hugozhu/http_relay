package com.github.httprelay.service;

import java.util.Map;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 3:51 PM
 */
public interface Callback {
    public void run(boolean success, Map<String,String> headers, String response);
}
