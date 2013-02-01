package com.github.httprelay;

import org.apache.http.conn.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SecureDnsResolver implements DnsResolver {

    public SecureDnsResolver() {

    }

    @Override
    public InetAddress[] resolve(String hostname) throws UnknownHostException{
        return new InetAddress[]{InetAddress.getByName(hostname)};
    }
}
