package xyz.downgoon.mydk.util;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * {@link ImmutableOrderedHash} is thread-safe, though its underlying dependency class {@link NonThreadSafeOrderedHash} is not.
 */
public class ImmutableOrderedHash<K, V> implements OrderedHash<K, V> {

    private NonThreadSafeOrderedHash<K, V> hash = new NonThreadSafeOrderedHash<>();

    public ImmutableOrderedHash(LinkedHashMap<K, V> map) {
        map.entrySet().stream().forEach((e) -> {
            hash.add(e.getKey(), e.getValue());
        });
    }

    @Override
    public NonThreadSafeOrderedHash add(K key, V value) {
        throw new IllegalStateException("can't add new elements due to immutable features");
    }

    @Override
    public boolean contains(K key) {
        return hash.contains(key);
    }

    @Override
    public V getValue(K key) {
        return hash.getValue(key);
    }

    @Override
    public int getIndex(K key) {
        return hash.getIndex(key);
    }

    @Override
    public int indexOf(K key) {
        return hash.indexOf(key);
    }

    @Override
    public V getBefore(String key, AtomicBoolean isHead) {
        return hash.getBefore(key, isHead);
    }

    @Override
    public V getAfter(String key, AtomicBoolean isTail) {
        return hash.getAfter(key, isTail);
    }

    @Override
    public V getHead() {
        return hash.getHead();
    }

    @Override
    public V getTail() {
        return hash.getTail();
    }

    @Override
    public int size() {
        return hash.size();
    }

    @Override
    public String toString() {
        return hash.toString();
    }
}
