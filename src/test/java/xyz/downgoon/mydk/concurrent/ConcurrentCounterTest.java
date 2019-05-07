package xyz.downgoon.mydk.concurrent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;


public class ConcurrentCounterTest {

    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void testIncrDecrSingle() throws Exception {

        ConcurrentCounter cnt = new ConcurrentCounter();
        long bj = cnt.increaseAndGet("beijing", 10);
        long sh = cnt.increaseAndGet("shanghai");

        Assert.assertEquals(10, bj);
        Assert.assertEquals(1, sh);

        bj = cnt.decreaseAndGet("beijing", 2);
        sh = cnt.decreaseAndGet("shanghai");

        Assert.assertEquals(8, bj);
        Assert.assertEquals(0, sh);

        Assert.assertEquals(8, cnt.getCount("beijing").longValue());

        // NOT FOUND CASE
        Assert.assertNull(cnt.getCount("not-found"));

        // Negative Delta Case
        cnt.increaseAndGet("beijing", -5);
        Assert.assertEquals(3, cnt.getCount("beijing").longValue());

    }


    @Test
    public void testStream() throws Exception {

        ConcurrentCounter cnt = new ConcurrentCounter();
        cnt.increaseAndGet("beijing", 10);
        cnt.increaseAndGet("shanghai");

        cnt.entries().forEach(System.out::println);


        Map<String, Long> exportMap = new HashMap<>();
        cnt.entries().forEach(kv -> { // export key/value pairs
            exportMap.put(kv.getKey(), kv.getValue());
        });

        Assert.assertEquals(10, exportMap.get("beijing").longValue());
        Assert.assertEquals(1, exportMap.get("shanghai").longValue());
        Assert.assertEquals(2, exportMap.size());


        Set<String> exportSet = new HashSet<>();
        cnt.keys().forEach(k -> { // export keys
            exportSet.add(k);
        });
        Assert.assertTrue(exportSet.contains("beijing"));
        Assert.assertTrue(exportSet.contains("shanghai"));
        Assert.assertEquals(2, exportSet.size());
    }



}
