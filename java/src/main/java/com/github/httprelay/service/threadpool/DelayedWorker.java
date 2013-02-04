package com.github.httprelay.service.threadpool;

/**
 * Date: 2/4/13 3:54 PM
 * Hugo Zhu <yisu@taobao.com>
 */
public class DelayedWorker extends Worker  {

    //时间优先队列？
    public void add(Runnable task, int retryTimes) {
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
