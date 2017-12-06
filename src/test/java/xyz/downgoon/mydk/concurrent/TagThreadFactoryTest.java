package xyz.downgoon.mydk.concurrent;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @title TagThreadFactoryTest
 * @description TODO 
 * @author liwei39
 * @date 2014-7-3
 * @version 1.0
 */
public class TagThreadFactoryTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() throws InterruptedException {
        TagThreadFactory f = new TagThreadFactory("fcv");
        final StringBuilder tagContainer = new StringBuilder();
        final CountDownLatch latch = new CountDownLatch(1);
        f.newThread(new Runnable() {
            @Override
            public void run() {
                tagContainer.append(Thread.currentThread().getName());
                latch.countDown();
            }
        }).start();

        latch.await();
        Assert.assertTrue(tagContainer.toString().indexOf("fcv") != -1);

    }
}
