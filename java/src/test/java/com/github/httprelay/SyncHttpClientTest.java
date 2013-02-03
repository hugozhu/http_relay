package com.github.httprelay;

import com.github.httprelay.util.SecureDnsResolver;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: hugozhu
 * Date: 2/1/13
 * Time: 3:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SyncHttpClientTest {

    @Test
    public void testSyncHttpClient() throws Exception {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(
                new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        ClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry, new SecureDnsResolver());
        HttpClient httpClient = new DefaultHttpClient(cm);

        for (int i = 0; i < 3; i++) {
            HttpGet httpget = new HttpGet("http://www.baidu.com/");
            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
                    // do something useful with the response
                    System.out.println(reader.readLine());

                } catch (IOException ex) {

                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    throw ex;

                } catch (RuntimeException ex) {

                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection and release it back to the connection manager.
                    httpget.abort();
                    throw ex;

                } finally {

                    // Closing the input stream will trigger connection release
                    instream.close();

                }
            }
        }
    }
}
