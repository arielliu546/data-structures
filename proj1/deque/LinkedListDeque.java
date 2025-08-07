package deque;

import java.util.Iterator;

public class LinkedListDeque<T>  implements Deque<T>, Iterable<T>  {
    private Node sentinel;
    private int size;

    private class Node {
        private T value;
        private Node prev;
        private Node next;

        Node(Node p, T item, Node n) {
            value = item;
            prev = p;
            next = n;
        }
    }

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    /**
    public LinkedListDeque(T item) {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        addFirst(item);
    }

    public LinkedListDeque(LinkedListDeque other) {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        int size = other.size();
        Node other_item = other.sentinel.next;
        for (int i = 0; i < size; i++) {
            addLast(other_item.value);
            other_item = other_item.next;
        }
    }
     */

    @Override
    public void addFirst(T item) {
        sentinel.next = new Node(sentinel, item, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size++;
    }

    @Override
    public void addLast(T item) {
        sentinel.prev = new Node(sentinel.prev, item, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node temp = sentinel.next;
        while (temp.value != null) {
            System.out.print(temp.value);
            temp = temp.next;
        }
    }

    @Override
    public T removeFirst() {
        if (sentinel.next != sentinel) {
            Node first = sentinel.next;
            sentinel.next = first.next;
            sentinel.next.prev = sentinel;
            size--;
            return first.value;
        } else {
            return null;
        }
    }

    @Override
    public T removeLast() {
        if (sentinel.prev != sentinel) {
            Node last = sentinel.prev;
            sentinel.prev = last.prev;
            sentinel.prev.next = sentinel;
            size--;
            return last.value;
        } else {
            return null;
        }
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node temp = sentinel.next;
        for (int i = 0; i < index; i++) {
            temp = temp.next;
        }
        return temp.value;
    }

    public T getRecursive(int index) {
        Node temp = sentinel.next;
        return getRecursiveHelper(temp, index);
    }

    private T getRecursiveHelper(Node n, int index) {
        if (index == 0) {
            return n.value;
        }
        n = n.next;
        index--;
        return getRecursiveHelper(n, index);
    }

    private class LIterator<T> implements Iterator<T> {
        private int index;

        LIterator() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < size();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            T returnItem = (T) get(index);
            index++;
            return returnItem;
        }
    }

    public Iterator<T> iterator() {
        return new LIterator<T>();
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        Deque<T> ol;

        if (o instanceof Deque) {
            ol = (Deque<T>) o;
        } else {
            return false;
        }

        if (size == ol.size()) {
            for (int i = 0; i < size; i++) {
                if (!get(i).equals(ol.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
