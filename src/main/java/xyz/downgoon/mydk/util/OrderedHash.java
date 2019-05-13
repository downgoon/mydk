package xyz.downgoon.mydk.util;

import java.util.concurrent.atomic.AtomicBoolean;

public interface OrderedHash<K, V> {
    NonThreadSafeOrderedHash add(K key, V value);

    boolean contains(K key);

    V getValue(K key);

    int getIndex(K key);

    int indexOf(K key);

    /**
     * if key not found, then return null and isHead is false;
     * if key is the head, then return null but isHead is true.
     * */
    V getBefore(String key, AtomicBoolean isHead);


    /**
     * if key not found, then return null and isTail is false;
     * if key is the tail, then return null but isTail is true.
     * */
    V getAfter(String key, AtomicBoolean isTail);

    V getHead();

    V getTail();

    int size();
}
