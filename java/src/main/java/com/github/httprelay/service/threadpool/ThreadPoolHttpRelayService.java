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
import java.net.URI;
import java.util.concurrent.*;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 6:58 PM
 */
public class ThreadPoolHttpRelayService extends BaseHttpRelayService {
    final DefaultHttpClient httpclient;
    ExecutorService pool;


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

        pool = new ThreadPoolExecutor(0, maxTotal,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
    }

    @Override
    public Future send(URI uri, String postdata, final Callback callback) throws IOException {
        try {
            final HttpUriRequest request = createRequest(uri,postdata);

            return pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpResponse response = httpclient.execute(request);
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
                                callback.run(false, null, request.getRequestLine()+": "+e.getMessage());
                                return;
                            } finally {
                                try {
                                    response.getEntity().getContent().close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e) {
                        callback.run(false, null, request.getRequestLine() + ": " + e.getMessage());
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            throw new IOException(e.getMessage());
        }
    }
}
