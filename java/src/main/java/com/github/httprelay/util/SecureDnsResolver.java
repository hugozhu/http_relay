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
        InetAddress[] addresses  = new InetAddress[]{InetAddress.getByName(hostname)};
        for (InetAddress addr: addresses) {
            if(addr.isAnyLocalAddress()||addr.isLoopbackAddress()) {
                throw new IllegalStateException(addr+" is not a valid api address");
            }
        }
        return addresses;
    }
}
