package xyz.downgoon.mydk.testing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class XrayTest {

    static class TestingDataStore {

        private ConcurrentHashMap<String, String> redisMap = new ConcurrentHashMap();

        private AtomicInteger redisHit = new AtomicInteger();

        private AtomicInteger redisLos = new AtomicInteger();

        private AtomicInteger mysqlCnt = new AtomicInteger();

        private AtomicInteger readCnt = new AtomicInteger();

        private Random rand = new Random();

        private static final Xray XRAY = Xray.xray(TestingDataStore.class);

        public String getData(String key) {
            readCnt.incrementAndGet();
            XRAY.dot("S1");

            String cachedValue = getDataFromRedis(key);
            XRAY.dot("S2");

            if (cachedValue == null) {


                String refreshedValue = getDataFromMySQL(key);
                setDataIntoRedis(key, refreshedValue);
            }
            return cachedValue;
        }

        private String getDataFromRedis(String key) {

            String cached = redisMap.get(key);
            if (cached != null) {
                redisHit.incrementAndGet();
            } else {
                redisLos.incrementAndGet();
            }
            return cached;
        }

        private void setDataIntoRedis(String key, String value) {
            redisMap.put(key, value);
        }

        private String getDataFromMySQL(String key) {
            mysqlCnt.incrementAndGet();
            return key + "@" + Math.abs(rand.nextLong());
        }

        @Override
        public String toString() {
            return "{" +
                    "redisHit=" + redisHit.get() +
                    ", redisLos=" + redisLos.get() +
                    ", mysqlCnt=" + mysqlCnt.get() +
                    ", readCnt=" + readCnt.get() +
                    "} on " + redisMap.toString();
        }

    }


    private static XrayCT xrayCT = (XrayCT) Xray.xray(TestingDataStore.class);

//    @Test
//    public void testSingle() {
//        TestingDataStore tds = new TestingDataStore();
//        tds.getData("SN1234");
//        System.out.println(tds);
//        tds.getData("SN1234");
//        System.out.println(tds);
//        tds.getData("SN1234");
//        System.out.println(tds);
//    }
//
//
//    @Test
//    public void testC2NoSeq() {
//        TestingDataStore tds = new TestingDataStore();
//
//        xrayCT.start(() -> {
//            tds.getData("SN1234");
//        }, "T1", "T2", "T3").await((logs) -> {
//
//            System.out.println(logs);
//        });
//
//        System.out.println(tds);
//
//
//    }
//
//
//    @Test
//    public void testC3Seq1() {
//        TestingDataStore tds = new TestingDataStore();
//
//        xrayCT.seq("T1#S1", "T1#S2", "T2#S1", "T2#S2", "T3#S1", "T3#S2");
//
//        xrayCT.startAndAwait(() -> {
//            tds.getData("SN1234");
//        }, new String[]{
//                "T1", "T2", "T3"
//        }, (logs) -> {
//            System.out.println(tds);
//            System.out.println(logs);
//        });
//    }
//
    @Test
    public void testC3Seq2() {
        TestingDataStore tds = new TestingDataStore();

        // xrayCT.seq("T1#S1", "T1#S2", "T2#S1", "T2#S2", "T3#S1", "T3#S2");

        xrayCT.seq("T1#S1", "T2#S1", "T3#S1", "T1#S2", "T2#S2", "T3#S2");

        // 并发是最终一致性？
        final List<String> dotExec = Collections.synchronizedList(new ArrayList<>());

        xrayCT.start(() -> {
            tds.getData("SN1234");
        }, new String[]{
                "T1", "T2", "T3"
        }, (threadName, dotName) -> {
            dotExec.add(threadName + "#" + dotName + "\r\n");
            System.out.println(String.format("[%s#%s] @@ callback", threadName, dotName));

        }).await((xrayName) -> {
            System.out.println(String.format("FIN: %s", xrayName));
            System.out.println(String.format("do exec seq:\r\n %s", dotExec));
        });
    }

//    @Test
//    public void testC2Seq2() {
//        TestingDataStore tds = new TestingDataStore();
//
//        // xrayCT.seq("T1#S1", "T1#S2", "T2#S1", "T2#S2");
//        xrayCT.seq("T1#S1", "T2#S1", "T1#S2", "T2#S2");
//        // xrayCT.seq("T1#S1", "T2#S1", "T2#S2", "T1#S2");
//
//        // xrayCT.seq("T2#S1", "T2#S2", "T1#S1", "T1#S2");
//        // xrayCT.seq("T2#S1", "T1#S1", "T2#S2", "T1#S2");
//
//
//        // xrayCT.seq("T1#S1", "T1#S2");
//
//        // 并发是最终一致性？
//        final List<String> dotExec = Collections.synchronizedList(new ArrayList<>());
//
//        xrayCT.start(() -> {
//            tds.getData("SN1234");
//        }, new String[]{
//                "T1", "T2"
//        }, (threadName, dotName) -> {
//            dotExec.add(threadName + "#" + dotName + "\r\n");
//            System.out.println(String.format("@@@ callback: %s#%s", threadName, dotName));
//
//        }).await((xrayName) -> {
//            System.out.println(String.format("FIN: %s", xrayName));
//            System.out.println(String.format("do exec seq:\r\n %s", dotExec));
//        });
//
//
//    }

}
