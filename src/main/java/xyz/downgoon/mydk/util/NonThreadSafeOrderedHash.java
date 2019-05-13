package xyz.downgoon.mydk.util;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * NOTE: Non Thread-Safe
 */
public class NonThreadSafeOrderedHash<K, V> implements OrderedHash<K, V> {

    // mapping from Key to Index (0,1,...,N-1)
    // private Map<K, Integer> keyIndex = new HashMap<>();
    private Map<K, Integer> keyIndex = new LinkedHashMap<>();

    // mapping from Index (0,1,...,N-1) to Value
    private List<V> orderedValues = new ArrayList<>();

    private int count = 0;

    @Override
    public NonThreadSafeOrderedHash add(K key, V value) {
        keyIndex.put(key, count);
        orderedValues.add(value);
        count++;
        return this;
    }

    @Override
    public boolean contains(K key) {
        return keyIndex.containsKey(key);
    }

    @Override
    public V getValue(K key) {
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            return null;
        }
        return orderedValues.get(idx);
    }


    @Override
    public int getIndex(K key) {
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            return -1;
        }
        return idx;
    }

    @Override
    public int indexOf(K key) {
        return getIndex(key);
    }

    @Override
    public V getBefore(String key, AtomicBoolean isHead) {
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            isHead.set(false);
            return null;
        }

        if (idx <= 0) {
            isHead.set(true);
            return null;
        }
        isHead.set(false);
        return orderedValues.get(idx - 1);
    }

    @Override
    public V getAfter(String key, AtomicBoolean isTail) {
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            isTail.set(false);
            return null;
        }
        if (idx >= count - 1) {
            isTail.set(true);
            return null;
        }
        isTail.set(false);
        return orderedValues.get(idx + 1);
    }

    @Override
    public V getHead() {
        if (count <= 0) {
            return null;
        }
        return orderedValues.get(0);
    }

    @Override
    public V getTail() {
        if (count <= 0) {
            return null;
        }
        return orderedValues.get(count - 1);
    }

    @Override
    public int size() {
        return count;
    }


    @Override
    public String toString() {
        return keyIndex.toString();
    }
}
