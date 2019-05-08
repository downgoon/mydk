package xyz.downgoon.mydk.util;

public interface OrderedHash<K, V> {
    NonThreadSafeOrderedHash add(K key, V value);

    boolean contains(K key);

    V getValue(K key);

    int getIndex(K key);

    int indexOf(K key);

    V getBefore(String key);

    V getAfter(String key);

    V getHead();

    V getTail();

    int size();
}
