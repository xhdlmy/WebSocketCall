package com.cl.cloud;

import com.xhd.base.util.LogUtils;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    /**
     * 测试堆内存地址空间
     */
    String s1 = "xuhuangda";
    A a = new A(1);
    class A {
        int a;
        public A(int a) {
            this.a = a;
        }
    }
    @Test
    public void test1() throws Exception {
        // 指向的是 "xuhuangda" 对象的地址
        String s = this.s1;
        // this.s1 重新指向了其他地址，注意 s 指向的地址不会随之改变
        this.s1 = "limengyin";
        System.out.print("s:" + s);

        // 将 a 地址赋予 aa，同一个对象内存地址
        A aa = a;
        a.a = 2;
        System.out.print(aa.a);
    }

}