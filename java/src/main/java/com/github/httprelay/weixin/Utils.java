package com.github.httprelay.weixin;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

    public static ResponseMessage parseResponseXml(String xml) throws Exception {
        ResponseMessage msg = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            dbFactory.setValidating(false);
            dbFactory.setIgnoringComments(true);
            Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
            NodeList children = doc.getDocumentElement().getChildNodes();
            String toUsername=null;
            String fromUsername=null;
            int createTime=0;
            String content=null;
            int articleCount=0;
            Node articlesNode=null;
            for (int i=0;i<children.getLength();i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Document.ELEMENT_NODE) {
                    if ("MsgType".equals(node.getNodeName()) && msg==null) {
                        if ("news".equals(node.getTextContent().trim())) {
                            msg = new NewsResponseMessage();
                        } else if ("text".equals(node.getTextContent().trim())) {
                            msg = new TextResponseMessage();
                        } else {
                            throw new IllegalArgumentException(node.getTextContent()+" is not a valid xml");
                        }
                    }

                    if ("ToUserName".equals(node.getNodeName())) {
                        toUsername = node.getTextContent();
                    }

                    if ("FromUserName".equals(node.getNodeName())) {
                        fromUsername = node.getTextContent();
                    }
                    if ("Content".equals(node.getNodeName())) {
                        content = node.getTextContent();
                    }
                    if ("CreateTime".equals(node.getNodeName())) {
                        createTime = Integer.parseInt(node.getTextContent());
                    }
                    if ("ArticleCount".equals(node.getNodeName())) {
                        articleCount = Integer.parseInt(node.getTextContent());
                        if (articleCount<1 || articleCount>10) {
                            throw new IllegalArgumentException("article count "+ articleCount+" is not valid");
                        }
                    }
                    if ("Articles".equals(node.getNodeName())) {
                        articlesNode = node;
                    }
                }
            }
            msg.setToUsername(toUsername);
            msg.setFromUsername(fromUsername);
            msg.setCreateTime(createTime);
            if (msg instanceof TextResponseMessage) {
                ((TextResponseMessage) msg).setContent(content);
            }
            if (msg instanceof NewsResponseMessage) {
                ((NewsResponseMessage) msg).setArticleCount(articleCount);
                ((NewsResponseMessage) msg).setArticles(new ArrayList<NewsResponseMessage.Article>(articleCount));
                NodeList items = articlesNode.getChildNodes();
                for (int i=0;i<items.getLength();i++) {
                    if (items.item(i).getNodeType() != Document.ELEMENT_NODE) {
                        continue;
                    }
                    NodeList tmp = items.item(i).getChildNodes();
                    NewsResponseMessage.Article article = new NewsResponseMessage.Article();
                    for (int j=0;j<tmp.getLength();j++) {
                        Node node = tmp.item(j);
                        if (node.getNodeType() != Document.ELEMENT_NODE) {
                            continue;
                        }
                        if ("Title".equals(node.getNodeName())) {
                            article.setTitle(node.getTextContent());
                        }
                        if ("Description".equals(node.getNodeName())) {
                            article.setDescription(node.getTextContent());
                        }
                        if ("PicUrl".equals(node.getNodeName())) {
                            article.setPicUrl(node.getTextContent());
                        }
                        if ("Url".equals(node.getNodeName())) {
                            article.setUrl(node.getTextContent());
                        }
                    }
                    ((NewsResponseMessage) msg).getArticles().add(article);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Response XML invalid: "+e.getMessage()+"\n"+xml);
        }
        return msg;
    }
}
