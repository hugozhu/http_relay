package com.github.httprelay.weixin;

import java.util.List;

/**
 * User: hugozhu
 * Date: 2/2/13
 * Time: 3:00 PM
 */
public class NewsResponseMessage extends ResponseMessage {
    private int articleCount;
    private List<Article> articles;

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public static class Article {
        private String title;
        private String description;
        private String picUrl;
        private String url;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @Override
    public String getMessageType() {
        return "news";
    }

    @Override
    public String encodeContentToXml() {
        StringBuilder sb = new StringBuilder("<ArticleCount>"+articleCount+"</ArticleCount>");
        sb.append("<Articles>");
        for (Article article: articles) {
            sb.append("<item>");
            sb.append("<Title><![CDATA["+article.title+"]]></Title>");
            sb.append("<Description><![CDATA["+article.description+"]]></Description>");
            sb.append("<PicUrl><![CDATA["+article.picUrl+"]]></PicUrl>");
            sb.append("<Url><![CDATA["+article.url+"]]></Url>");
            sb.append("</item>");
        }
        sb.append("</Articles>");
        return sb.toString();
    }
}