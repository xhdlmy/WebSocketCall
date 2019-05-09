package com.cl.cloud;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.xhd.base.util.LogUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private String TAG = "ExampleInstrumentedTest";

    Runnable runnable = new Runnable() {

        private int tickets = 100;

        @Override
        public void run() {
            while (tickets > 0){
                tickets--;
                LogUtils.i(TAG, Thread.currentThread() + "取走了第" + (100 - tickets) + "票");
            }
        }
    };

    Thread thread1 = new Thread(runnable, "徐煌达");

    Thread thread2 = new Thread(runnable, "黎梦吟");

    @Test
    public void testThread() throws Exception {
        // 一个线程只能调用start（）方法一次，多次启动一个线程是非法的
        thread1.start();
        thread2.start();
    }

}
