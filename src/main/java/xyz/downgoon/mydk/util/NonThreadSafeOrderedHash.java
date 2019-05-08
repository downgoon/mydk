package xyz.downgoon.mydk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NOTE: Non Thread-Safe
 */
public class NonThreadSafeOrderedHash<K, V> implements OrderedHash<K, V> {

    private Map<K, Integer> keyIndex = new HashMap<>();

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
    public V getBefore(String key) {
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            return null;
        }

        if (idx <= 0) {
            // return head
            return orderedValues.get(0);
        }
        return orderedValues.get(idx - 1);
    }

    @Override
    public V getAfter(String key) {
        Integer idx = keyIndex.get(key);
        if (idx == null) {
            return null;
        }
        if (idx >= count - 1) {
            // return tail
            return orderedValues.get(count - 1);
        }
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

}
