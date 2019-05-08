package xyz.downgoon.mydk.testing;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class XrayTest {

    static class TestingCounter {

        private int count = 0;

        private Random rand = new Random();

        public int incr(int delta) {
            count += (delta - 2);
            Xray.xray().dot("S1");
            count += 2;
            Xray.xray().dot("S2");
            return count;
        }

        public int get() {
            return count;
        }

    }


    @Test
    public void testSingle() {
        TestingCounter cnt = new TestingCounter();
        cnt.incr(5);
        Assert.assertEquals(5, cnt.get());

        cnt.incr(10);
        Assert.assertEquals(15, cnt.get());
    }


    @Test
    public void testC2NoSeq() {
        TestingCounter cnt = new TestingCounter();

        XrayCTFactory.get("DEFAULT")
                .start(() -> {
                    cnt.incr(10);
                }, "T1", "T2", "T3").await((logs) -> {

            System.out.println(logs);
        });

        System.out.println(cnt.get());


    }


    @Test
    public void testC3Seq() {
        TestingCounter cnt = new TestingCounter();


        XrayCTFactory.get("DEFAULT")
                .seq("T1", "S1")
                .seq("T1", "S2")
                .seq("T2", "S1")
                .seq("T2", "S2")
                .seq("T3", "S1")
                .seq("T3", "S2")
                .startAndAwait(() -> {
                    cnt.incr(10);
                }, new String[]{
                        "T1", "T2", "T3"
                }, (logs) -> {
                    System.out.println(cnt.get());
                    System.out.println(logs);
                });


    }
}
