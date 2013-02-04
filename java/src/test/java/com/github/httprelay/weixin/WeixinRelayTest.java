package com.github.httprelay.weixin;

import com.github.httprelay.service.Callback;
import com.github.httprelay.service.threadpool.ThreadPoolHttpRelayService;
import org.junit.Test;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertTrue;

/**
 * Date: 2/4/13 1:32 PM
 * Hugo Zhu <yisu@taobao.com>
 */
public class WeixinRelayTest {

    @Test
    public void testTaobaoSearch() throws Exception {
        final String url = "http://pm.etao.com/tools/weixin/search.php?" + Utils.querySignature("weixinsearch",(int)(System.currentTimeMillis()/1000l),"Hello");
        ThreadPoolHttpRelayService service = new ThreadPoolHttpRelayService();
        final RequestMessage msg = new TextRequestMessage("myalert","hugozhu", (int) (System.currentTimeMillis()/1000l),"phone");
        final CountDownLatch countDown = new CountDownLatch(1);
        final StringBuffer sb = new StringBuffer();
        service.send(new URI(url), msg.encodeToXml() , new Callback() {
            @Override
            public void run(boolean success, Map<String, String> headers, final String response) {
                if (success) {
                    sb.append(response);
                    try {
                        ResponseMessage msg = Utils.parseResponseXml(response);
                        System.out.println(msg.encodeToXml());
                        //call downstream api (async?)
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println(response);
                }
                countDown.countDown();
            }
        });
        countDown.await();
        assertTrue(sb.length()>0);
    }
}
