package com.github.httprelay.service.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date: 2/4/13 3:54 PM
 * Hugo Zhu <yisu@taobao.com>
 */
public class DelayedWorker {
    ScheduledThreadPoolExecutor pool;
    AtomicInteger pending = new AtomicInteger(0);
    int max = 0;

    public DelayedWorker(int n,int max) {
        pool = new ScheduledThreadPoolExecutor(n);
        this.max = max;
    }

    public boolean add(final Runnable task1, int retryTimes) {
        if (pending.intValue()>max) {
            //todo: remove last one?
            return false;
        }
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    task1.run();
                } finally {
                    pending.decrementAndGet();
                }
            }
        };
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
