package xyz.downgoon.mydk.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Since JUnit 4.11
// 单元测试必须串行执行，XRay只能服务于一次并发测试。第二次，需要先Reset。
@FixMethodOrder(MethodSorters.NAME_ASCENDING) 
public class XrayTest {
	
	private static final Logger LOG = LoggerFactory.getLogger(XrayTest.class);

    static class TestingDataStore {

        private ConcurrentHashMap<String, String> redisMap = new ConcurrentHashMap<>();

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


            if (cachedValue == null) {

                XRAY.dot("S2");
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

    @Test
    public void testC3Seq1() {
        TestingDataStore tds = new TestingDataStore();

        // expected sequence
        String[] seq = {"T1#S1", "T2#S1", "T3#S1", "T1#S2", "T2#S2", "T3#S2"};
        xrayCT.seq(seq);

        final List<String> actualSeq = Collections.synchronizedList(new ArrayList<>());

        xrayCT.start(() -> {
            tds.getData("SN1234");
        }, new String[]{
                "T1", "T2", "T3"
        }, (isExeced, threadName, dotName) -> {
        	// actual sequence
            actualSeq.add(threadName + "#" + dotName);

        }).await((xrayName) -> {
        	LOG.info("actual sequence: {}", actualSeq);
        	List<String> expectedSeq = Arrays.asList(seq);
        	LOG.info("expected sequence: {}", expectedSeq);
        	Assert.assertEquals(expectedSeq, actualSeq);
        });
    }
    
    
    @Test
    public void testC3Seq2() {
        TestingDataStore tds = new TestingDataStore();

        // expected sequence
        String[] seq = {"T1#S1", "T1#S2", "T2#S1", "T2#S2", "T3#S1", "T3#S2"};
        xrayCT.seq(seq);

        final List<String> actualSeq = Collections.synchronizedList(new ArrayList<>());

        xrayCT.start(() -> {
            tds.getData("SN1234");
        }, new String[]{
                "T1", "T2", "T3"
        }, (isExeced, threadName, dotName) -> {
        	// actual sequence
            actualSeq.add(threadName + "#" + dotName);

        }).await((xrayName) -> {
        	LOG.info("actual sequence: {}", actualSeq);
        	List<String> expectedSeq = Arrays.asList(seq);
        	LOG.info("expected sequence: {}", expectedSeq);
        	Assert.assertEquals(expectedSeq, actualSeq);
        });
    }


}
