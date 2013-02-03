package com.github.httprelay.service;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;


/**
 * 本服务主要用来调取外部第三方的实现的API，外部API的网络环境不相同（速度不一样，调用量也不一样）
 * 单机吞吐量是衡量服务的一个指标
 * <ul>功能要求：
 *     <li>不支持跳转</li>
 *     <li>对域名解析需要校验</li>
 *     <li>对于每个API允许的网络连接数有一定限制，如：10</li>
 *     <li>对于单机的总共网络连接数一定限制，如：100K</li>
 *     <li>对相同的API调用尽量按次序执行</li>
 *     <li>每次调用有一定的超时设置：如10秒，失败后排入失败队列，在15分钟内重试三次，分别是1分钟，5分钟，15分钟</li>
 * </ul>
 * <ul>监控数据：
 *     <li>总调用QPS</li>
 *     <li>总失败数</li>
 *     <li>总失败队列长度</li>
 *     <li>每个API的QPS，RT和失败率</li>
 * </ul>
 * User: hugozhu
 * Date: 2/2/13
 * Time: 3:32 PM
 */
public interface HttpRelayService {

    /**
     * 增加一个调用，当系统容量不足时可能会拒绝增加
     * @param uri
     * @param postdata
     * @param callback
     * @return
     * @throws IOException
     */
    public Future send(URI uri, String postdata, Callback callback) throws IOException;
}
