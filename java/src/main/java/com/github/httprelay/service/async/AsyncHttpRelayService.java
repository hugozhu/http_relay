package com.github.httprelay.service.async;

import com.github.httprelay.service.BaseHttpRelayService;
import com.github.httprelay.service.Callback;
import com.github.httprelay.service.NoRedirectStrategy;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingClientAsyncConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 3:39 PM
 */
public class AsyncHttpRelayService extends BaseHttpRelayService {
    DefaultHttpAsyncClient httpclient;

    public AsyncHttpRelayService() {
        try {
            IOReactorExceptionHandler handler = new IOReactorExceptionHandler() {
                @Override
                public boolean handle(IOException ex) {
                    ex.printStackTrace();
                    return false;
                }

                @Override
                public boolean handle(RuntimeException ex) {
                    ex.printStackTrace();
                    return false;
                }
            };
            IOReactorConfig config = new IOReactorConfig();

            DefaultConnectingIOReactor defaultioreactor = new DefaultConnectingIOReactor(config);
            defaultioreactor.setExceptionHandler(handler);

            PoolingClientAsyncConnectionManager connmgr = new PoolingClientAsyncConnectionManager(defaultioreactor);
            connmgr.setDefaultMaxPerRoute(maxPerRoute);
            connmgr.setMaxTotal(maxTotal);

            httpclient = new DefaultHttpAsyncClient(connmgr);
            configHttpCLientParams(httpclient.getParams());
            httpclient.setRedirectStrategy(new NoRedirectStrategy());

            httpclient.start();
        } catch (IOReactorException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Future send(URI uri, String postdata, final Callback callback) throws IOException {
        final HttpUriRequest request = createRequest(uri,postdata);
        HttpAsyncResponseConsumer<HttpResponse> consumer = new MaxBytesLimitedAsyncResponseConsumer(maxResposneBytes,timeout);
        return httpclient.execute(HttpAsyncMethods.create(request), consumer, new FutureCallback<HttpResponse>() {
            public void completed(final HttpResponse response) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    callback.run(false, null, "response status code:"+response.getStatusLine().getStatusCode());
                }
                ByteArrayOutputStream output = new ByteArrayOutputStream(avgResponseSize);
                try {
                    InputStream in = response.getEntity().getContent();
                    byte[] buff = new byte[avgResponseSize];
                    int n;
                    while ( (n = in.read(buff)) !=-1) {
                        output.write(buff,0,n);
                    }
                } catch (IOException e) {
                    failed(e);
                    return;
                } finally {
                    try {
                        response.getEntity().getContent().close();
                    } catch (Exception e) {}
                }
                Header[] headers =  response.getAllHeaders();
                Map<String,String> map = new HashMap<String,String>(headers.length);
                for (Header header:headers) {
                    map.put(header.getName(),header.getValue());
                }
                callback.run(true, map, output.toString());
            }

            public void failed(final Exception ex) {
                if (ex instanceof SocketTimeoutException) {
                    callback.run(false, null, request.getRequestLine()+": timed out");
                } else {
                    callback.run(false, null, request.getRequestLine()+": "+ex.getClass()+" "+ex.getMessage());
                }
            }

            public void cancelled() {
                callback.run(false, null, request.getRequestLine()+": request is cancelled");
            }
        });

    }
}
