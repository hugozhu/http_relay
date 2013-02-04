package com.github.httprelay.service.threadpool;

import java.util.concurrent.*;

/**
 * Date: 2/4/13 3:54 PM
 * Hugo Zhu <yisu@taobao.com>
 */
public class DelayedWorker {
    ScheduledThreadPoolExecutor pool;
    int max = 0;

    public DelayedWorker(int n,int max) {
        pool = new ScheduledThreadPoolExecutor(n);
        this.max = max;
    }

    public boolean add(final Runnable task, int retryTimes) {
        if (pool.getQueue().size() > max) {
            //todo: remove some existing task?
//            pool.getQueue().drainTo(new ArrayList<Runnable>(),max/2);
            return false;
        }
        if (retryTimes<=1) {
            pool.schedule(task,50, TimeUnit.SECONDS);
        } else if (retryTimes==2) {
            pool.schedule(task,5, TimeUnit.MINUTES);
        } else if (retryTimes==3) {
            pool.schedule(task,15, TimeUnit.MINUTES);
        }
        return true;
    }
}
