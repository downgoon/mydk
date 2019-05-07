package xyz.downgoon.mydk.concurrent;

import java.util.Map;
import java.util.stream.Stream;

public interface Counter {

    /**
     * @param delta number to be increased, it can be negative or zero
     */
    Long increaseAndGet(String key, int delta);

    Long increaseAndGet(String key);

    /**
     * @param delta number to be decreased, it can be negative or zero
     */
    Long decreaseAndGet(String key, int delta);

    Long decreaseAndGet(String key);

    /**
     * if not found, return null
     */
    Long getCount(String key);

    boolean contains(String key);

    Stream<String> keys();

    Stream<Map.Entry<String, Long>> entries();
}
