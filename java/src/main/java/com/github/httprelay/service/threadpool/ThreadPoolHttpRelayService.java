package com.github.httprelay.service.threadpool;

import com.github.httprelay.service.BaseHttpRelayService;
import com.github.httprelay.service.Callback;
import com.github.httprelay.service.NoRedirectStrategy;
import com.github.httprelay.util.SecureDnsResolver;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URI;

import java.util.concurrent.*;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 6:58 PM
 */
public class ThreadPoolHttpRelayService extends BaseHttpRelayService {
    final DefaultHttpClient httpclient;
    ConcurrentHashMap<String, Worker[]> queueMap = new ConcurrentHashMap<String, Worker[]>();

    public ThreadPoolHttpRelayService() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry, new SecureDnsResolver());
        cm.setDefaultMaxPerRoute(maxPerRoute);
        cm.setMaxTotal(maxTotal);

        httpclient = new DefaultHttpClient(cm);
        configHttpCLientParams(httpclient.getParams());
        httpclient.setRedirectStrategy(new NoRedirectStrategy());
    }

    public Worker getWorker(String host, int retryTimes) {
        Worker[] works = queueMap.get(host);
        if (works == null) {
            works = new Worker[this.maxPerRoute];
            for (int i=0;i<works.length;i++) {
                works[i] = new Worker();
            }
            Worker[] exist = queueMap.putIfAbsent(host,works);
            if (exist!=null) {
                works = exist;
            } else {
                for (int i=0;i<works.length;i++) {
                    works[i].start();
                }
            }
        }
        if (retryTimes>0) {
            return works[works.length-1];
        } else {
            int min = Integer.MAX_VALUE;
            int index = 0;
            for (int i=0;i<works.length-1;i++) {
                if (works[i].getPending() < min) {
                    min = works[i].getPending();
                    index = i;
                }
            }
            return works[index];
        }
    }

    @Override
    public void send(final URI uri, final String postdata, final Callback callback) throws IOException {
        try {
            final HttpUriRequest request = createRequest(uri,postdata);
            Runnable task = new Runnable() {
                int retry = 0;
                @Override
                public void run() {
                    HttpResponse response = null;
                    try {
                        response = httpclient.execute(request);
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            ByteArrayOutputStream output = new ByteArrayOutputStream(avgResponseSize);
                            try {
                                InputStream in = response.getEntity().getContent();
                                byte[] buff = new byte[avgResponseSize];
                                int n;
                                int total = 0;
                                while ( (n = in.read(buff)) !=-1) {
                                    output.write(buff,0,n);
                                    total += n;
                                    if (total > maxResposneBytes) {
                                        request.abort();
                                        throw new IllegalStateException("Content too large: "+total);
                                    }
                                }
                                callback.run(true, null, output.toString("UTF-8"));
                            } catch (Exception e) {
                                callback.run(false, null, uri+": " + e.getClass()+" "+e.getMessage());
                            } finally {
                                try {
                                    response.getEntity().getContent().close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        //timeout error: can be retried
                        if (this.retry>=3) {
                            callback.run(false, null, uri + ": timed out after " + this.retry+" retries");
                        } else {
                            getWorker(uri.getHost(),++this.retry).add(this);
                        }
                    } catch (Exception e) {
                        callback.run(false, null, uri + ": " + e.getClass()+" "+e.getMessage());
                    }
                }
            };
            getWorker(uri.getHost(),0).add(task);
        } catch (RejectedExecutionException e) {
            throw new IOException(e.getMessage());
        }
    }
}
