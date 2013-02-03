package com.github.httprelay;

import com.github.httprelay.service.NoRedirectStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingClientAsyncConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.ClientAsyncConnectionManager;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 * User: hugozhu
 * Date: 2/1/13
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncHttpClientTest {

    @Test
    public void testAsyncHttpClient() throws Exception {
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

        ClientAsyncConnectionManager connmgr = new PoolingClientAsyncConnectionManager(defaultioreactor);

        DefaultHttpAsyncClient httpclient = new DefaultHttpAsyncClient(connmgr);
        httpclient.setRedirectStrategy(new NoRedirectStrategy());
        httpclient.getParams()
                .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000)
                .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000)
                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
                .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);

        httpclient.start();

        try {
            HttpGet[] requests = new HttpGet[] {
                    new HttpGet("http://www.apache.org/"),
                    new HttpGet("https://www.verisign.com/"),
                    new HttpGet("http://www.google.com/")
            };
            final CountDownLatch latch = new CountDownLatch(requests.length);
            for (final HttpGet request: requests) {
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    public void completed(final HttpResponse response) {
                        latch.countDown();
                        System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
                    }

                    public void failed(final Exception ex) {
                        latch.countDown();
                        System.out.println(request.getRequestLine() + "->" + ex);
                    }

                    public void cancelled() {
                        latch.countDown();
                        System.out.println(request.getRequestLine() + " cancelled");
                    }

                });
            }
            latch.await();
            System.out.println("Shutting down");
        } finally {
            httpclient.shutdown();
        }
        System.out.println("Done");
    }
}
