package com.github.httprelay.weixin;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Date: 2/4/13 2:52 PM
 * Hugo Zhu <yisu@taobao.com>
 */
public class Utils {

    public static String querySignature(String token, int timestamp,  String nonce) {
        String[] tmpArr = new String[]{token, String.valueOf(timestamp),nonce};
        Arrays.sort(tmpArr);
        String tmp = tmpArr[0]+tmpArr[1]+tmpArr[2];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String signature = byteArrayToHexString(md.digest(tmp.getBytes()));
            return "signature="+signature+"&timestamp="+timestamp+"&nonce="+nonce;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
                    Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    public static ResponseMessage parseResponseXml(String xml) {
        ResponseMessage msg = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dbFactory.setValidating(false);
            dbFactory.setIgnoringComments(true);
            Document doc = dBuilder.parse(xml);
            System.err.println("----"+doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }
}
