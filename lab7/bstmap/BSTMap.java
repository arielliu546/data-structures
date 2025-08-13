package bstmap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private Node root;
    private int size;

    private class Node<K, V> {
        K key;
        V value;
        Node left;
        Node right;
        // int size;

        public Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    public BSTMap() {
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    private Node find(Node root, K key) {
        if (root == null) {
            return null;
        }
        if (key.equals(root.key)) {
            return root;
        } else if (key.compareTo((K) root.key) < 0) {
            return find(root.left, key);
        } else {
            return find(root.right, key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return (find(root, key) != null);
    }

    @Override
    public V get(K key) {
        if (find(root, key) == null) {
            return null;
        } else {
            return (V) find(root, key).value;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
    }

    // recursively put the mapping under the root
    private Node put(Node root, K k, V v) {
        // if this key is already in the tree
        if (find(root, k) != null) {
            return null;
        }
        // insert the mapping recursively
        if (root == null) {
            size += 1;
            return new Node(k, v);
        }
        if (k.compareTo((K) root.key) < 0) {
            root.left = put(root.left, k, v);
        } else if (k.compareTo((K) root.key) > 0) {
            root.right = put(root.right, k, v);
        }
        // size += 1;
        return root;
    }

    public Set<K> keySet() {
        throw new UnsupportedOperationException("keySet() is not implemented yet");
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("remove(key) is not implemented yet");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("remove(key, value) is not implemented yet");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Iterator is not implemented yet");

        // return new BSTMapIterator();
    }

    private Node getMin() {
        Node temp = root;
        while (temp.left != null) {
            temp = temp.left;
        }
        return temp;
    }

    private Node getMax() {
        Node temp = root;
        while (temp.right != null) {
            temp = temp.right;
        }
        return temp;
    }

    private class BSTMapIterator<K> implements Iterator<K> {
        Node n;
        Queue<K> queue;

        public class Queue<K> {
            Item sentinel;

            private class Item {
                Node node;
                Item next;

                public Item(Node i, Item n) {
                    node = i;
                    next = n;
                }
            }

            public Queue() {
                sentinel = new Item(null, null);
            }

            public void add(Node o) {
                sentinel.next = new Item(o, sentinel.next);
            }

            public Node remove() {
                Item temp = sentinel.next;
                sentinel.next = temp.next;
                return temp.node;
            }

            public void clear() {

            }
        }

        public BSTMapIterator() {
        }

        /**
        private Queue<K> getItems(Node min, Node max) {

        }
         */

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public K next() {
            return null;
        }
    }


    public void printInOrder() {
        print(root);
    }

    private void print(Node root) {
        print(root.left);
        printSingle(root);
        print(root.right);
    }

    private void printSingle(Node root) {
        System.out.println("key: " + root.key + "; value: " + root.value);
    }
}
