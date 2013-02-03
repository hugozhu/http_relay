package com.github.httprelay.util;

import org.apache.http.conn.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * todo: check IP is not intranet
 */
public class SecureDnsResolver implements DnsResolver {

    public SecureDnsResolver() {

    }

    @Override
    public InetAddress[] resolve(String hostname) throws UnknownHostException{
        return new InetAddress[]{InetAddress.getByName(hostname)};
    }
}
