package com.cl.cloud;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by work2 on 2019/5/9.
 */

public class ThreadTest {

    /**
     * 测试 Thread 线程
     */
    private String TAG = "ThreadTest";

    Runnable runnable = new Runnable() {

        private AtomicInteger tickets = new AtomicInteger(100);

        @Override
        public void run() {
            while (tickets.get() > 0){
                tickets.decrementAndGet();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "获得了选举的第" + (100 - tickets.get()) + "张票");
            }
        }
    };

    Thread thread1 = new Thread(runnable, "xxx");

    Thread thread2 = new Thread(runnable, "yyy");

    @Test
    public void testThread() throws Exception {
        // 一个线程只能调用start（）方法一次，多次启动一个线程是非法的
        thread1.start();
        thread2.start();

        while (thread1.isAlive()){

        }
    }

}
