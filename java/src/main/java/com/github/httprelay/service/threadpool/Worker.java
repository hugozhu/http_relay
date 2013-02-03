package com.github.httprelay.service.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: hugozhu
 * Date: 2/3/13
 * Time: 3:13 PM
 */
public class Worker extends Thread {
    volatile boolean run;
    BlockingQueue<Runnable> queue;

    public Worker() {
        run = true;
        queue = new LinkedBlockingQueue<Runnable>();
    }

    public int getPending() {
        return queue.size();
    }

    @Override
    public void run() {
        while(run) {
            try {
                Runnable task = queue.take();
                task.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(Runnable task) {
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
