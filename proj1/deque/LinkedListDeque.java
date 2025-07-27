package deque;

public class LinkedListDeque <T> {
    private Node sentinel;
    private int size;

    private class Node {
        public T value;
        public Node prev;
        public Node next;

        public Node(Node p, T item, Node n) {
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

    public void addFirst(T item) {
        sentinel.next = new Node(sentinel, item, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size ++;
    }

    public void addLast(T item) {
        sentinel.prev = new Node(sentinel.prev, item, sentinel);
        sentinel.prev.prev.next = sentinel.prev;
        size ++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node temp = sentinel.next;
        while(temp.value != null) {
            System.out.print(temp.value);
            temp = temp.next;
        }
    }

    public T removeFirst(){
        if (sentinel.next != sentinel) {
            Node first = sentinel.next;
            sentinel.next = first.next;
            sentinel.next.prev = sentinel;
            size --;
            return first.value;
        } else {
            return null;
        }
    }

    public T removeLast(){
        if (sentinel.prev != sentinel) {
            Node last = sentinel.prev;
            sentinel.prev = last.prev;
            sentinel.prev.next = sentinel;
            size --;
            return last.value;
        } else {
            return null;
        }
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node temp = sentinel.next;
        for (int i = 0; i < index; i ++) {
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
        index --;
        return getRecursiveHelper(n, index);
    }


    public static void main(String[] args) {
        LinkedListDeque<Integer> list = new LinkedListDeque<>(5);
        list.addFirst(9);
        list.addLast(10);
        list.printDeque();
    }

}