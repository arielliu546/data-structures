package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Ariel
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int itemNumber;
    private double loadFactor;
    // You should probably define some more!

    private int getHash(K key) {
        int hash = key.hashCode() % buckets.length;
        if (hash < 0) {
            hash += buckets.length;
        }
        return hash;
    }

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(16);
        itemNumber = 0;
        loadFactor = 0.75;
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
        itemNumber = 0;
        loadFactor = 0.75;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        itemNumber = 0;
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        return table;
    }

    // Your code won't compile until you do so!
    @Override
    public void clear() {
        for (Collection<Node> bucket : buckets) {
            bucket.clear();
        }
        itemNumber = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public V get(K key) {
        int hash = getHash(key);
        if (buckets[hash] == null) {
            return null;
        }
        for (Node node : buckets[hash]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return itemNumber;
    }

    @Override
    public void put(K key, V value) {
        int hash = getHash(key);
        // if there's no bucket here
        if (buckets[hash] == null) {
            buckets[hash] = createBucket();
        }
        // if the key is already in the bucket
        for (Node node : buckets[hash]) {
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        buckets[hash].add(createNode(key, value));
        itemNumber++;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("This method is not supported yet");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("This method is not supported yet");
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node node : bucket) {
                    set.add(node.key);
                }
            }
        }
        return set;
    }

    @Override
    public Iterator<K> iterator() {
        return new HashMapIterator();
    }

    private class HashMapIterator implements Iterator {
        Set<K> set;
        Iterator<K> setIterator;

        public HashMapIterator() {
            set = keySet();
            setIterator = set.iterator();
        }

        @Override
        public boolean hasNext() {
            return setIterator.hasNext();
        }

        @Override
        public K next() {
            return setIterator.next();
        }
    }


}
