package com.github.httprelay;

import com.github.httprelay.service.Callback;
import com.github.httprelay.service.HttpRelayService;
import com.github.httprelay.service.async.AsyncHttpRelayService;
import com.github.httprelay.service.threadpool.ThreadPoolHttpRelayService;
import com.github.httprelay.weixin.RequestMessage;
import com.github.httprelay.weixin.TextRequestMessage;
import org.junit.Test;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;


/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 4:16 PM
 */
public class RelayServiceTest {

    @Test
    public void testThreadPoolService() throws Exception {
        ThreadPoolHttpRelayService service = new ThreadPoolHttpRelayService();
        RequestMessage msg = new TextRequestMessage("myalert","hugozhu", (int) (System.currentTimeMillis()/1000l),"1");
        final CountDownLatch countDown = new CountDownLatch(1);
        service.send(new URI("http://go.myalert.info/weixin.php"), msg.encodeToXml() , new Callback() {
            @Override
            public void run(boolean success, Map<String, String> headers, String response) {
                System.err.println(success+ "=====" + response);
                countDown.countDown();
            }
        });
        countDown.await();
    }


    @Test
    public void testAsyncService() throws Exception {
        AsyncHttpRelayService service = new AsyncHttpRelayService();
        RequestMessage msg = new TextRequestMessage("myalert","hugozhu", (int) (System.currentTimeMillis()/1000l),"1");
        final CountDownLatch countDown = new CountDownLatch(1);
        service.send(new URI("http://go.myalert.info/weixin.php"), msg.encodeToXml() , new Callback() {
            @Override
            public void run(boolean success, Map<String, String> headers, String response) {
                System.err.println(success+ "=====" + response);
                countDown.countDown();
            }
        });
        countDown.await();
    }

    @Test
    public void testTooLargeAsyncResponse() throws Exception {
        final CountDownLatch countDown = new CountDownLatch(10);
        AsyncHttpRelayService service = new AsyncHttpRelayService();
        for (int i=0;i<10; i++) {
            final int id=i;
            service.send(new URI("http://go.myalert.info/test/large.php?id="+i), null, new Callback() {
                @Override
                public void run(boolean success, Map<String, String> headers, String response) {
                    countDown.countDown();
                    if (success) {
                        System.err.println(id +"===="+ success + "=====" + response.length());
                    } else {
                        System.err.println(id +"===="+ success + "=====" + response);
                    }
                }
            });
        }
        countDown.await();
    }

    @Test
    public void testTimeoutSyncResponse() throws Exception {
        final CountDownLatch countDown = new CountDownLatch(20);
        HttpRelayService service = new ThreadPoolHttpRelayService();
        for (int i=0;i<20; i++) {
            final int id=i;
            service.send(new URI("http://go.myalert.info/test/timeout.php?id="+i), null, new Callback() {
                @Override
                public void run(boolean success, Map<String, String> headers, String response) {
                    countDown.countDown();
                    if (success) {
                        System.err.println(id +"===="+ success + "=====" + response.length());
                    } else {
                        System.err.println(id +"===="+ success + "=====" + response);
                    }
                }
            });
        }
        countDown.await();
    }

    @Test
    public void testTooLargeSyncResponse() throws Exception {
        final CountDownLatch countDown = new CountDownLatch(10);
        HttpRelayService service = new ThreadPoolHttpRelayService();
        for (int i=0;i<10; i++) {
            final int id=i;
            service.send(new URI("http://go.myalert.info/test/large.php?id="+i), null, new Callback() {
                @Override
                public void run(boolean success, Map<String, String> headers, String response) {
                    countDown.countDown();
                    if (success) {
                        System.err.println(id +"===="+ success + "=====" + response.length());
                    } else {
                        System.err.println(id +"===="+ success + "=====" + response);
                    }
                }
            });
        }
        countDown.await();
    }
}