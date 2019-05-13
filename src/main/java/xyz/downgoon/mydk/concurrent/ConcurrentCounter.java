package xyz.downgoon.mydk.concurrent;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * ConcurrentCounter
 *
 * @author downgoon@qq.com
 * @date 2019/05/07
 */
public class ConcurrentCounter implements Counter {

    private ConcurrentHashMap<String, AtomicLong> map = new ConcurrentHashMap<String, AtomicLong>();

    @Override
    public Long increaseAndGet(String key, int delta) {
        AtomicLong c = map.get(key);
        if (c == null) {
            c = new AtomicLong(0L);
            AtomicLong pre = map.putIfAbsent(key, c);
            if (pre != null) {
                c = pre;
            }
        }
        return c.addAndGet(delta);
    }


    @Override
    public Long increaseAndGet(String key) {
        return increaseAndGet(key, 1);
    }

    @Override
    public Long decreaseAndGet(String key, int delta) {
        return increaseAndGet(key, -delta);
    }

    @Override
    public Long decreaseAndGet(String key) {
        return increaseAndGet(key, -1);
    }

    @Override
    public Long getCount(String key) {
        AtomicLong v = map.get(key);
        if (v == null) {
            return null;
        }
        return v.get();
    }

    @Override
    public boolean contains(String key) {
        return map.contains(key);
    }

    @Override
    public Stream<String> keys() {
        return map.entrySet().stream().map(kv -> kv.getKey());
    }

    @Override
    public Stream<Map.Entry<String, Long>> entries() {
        return map.entrySet().stream().map(kv -> {

            return new Map.Entry<String, Long>() {
                @Override
                public String getKey() {

                    return kv.getKey();
                }

                @Override
                public Long getValue() {
                    return kv.getValue().get();
                }

                @Override
                public Long setValue(Long value) {
                    throw new IllegalStateException("Bad Method Executed");
                }

                @Override
                public String toString() {
                    return kv.toString();
                }
            };

        });
    }

}
