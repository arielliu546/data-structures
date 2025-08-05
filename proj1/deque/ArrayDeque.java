package deque;

public class ArrayDeque<T> implements Deque<T> {
    private T[] items;
    private int size;
    private int head;
    private int tail;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        head = 0; // points to the starting valued bit
        tail = 0; // points to the bit after the first value
        size = 0;
    }

    public ArrayDeque(ArrayDeque other) {
        items = (T[]) new Object[8];
        head = 0;
        tail = 0;
        for (int i = 0; i < other.size(); i++) {
            addLast((T) other.get(i));
        }
    }

    private int move(int p, int step) {
        if (p + step > items.length) {
            return p + step - items.length;
        } else if (p + step < 0) {
            return p + step + items.length;
        } else {
            return p + step;
        }
    }

    private void resize(int l) {
        T[] newArray = (T[]) new Object[l];
        int s = size();
        int p = head;
        for (int i = 0; i < size(); i++) {
            if (p >= items.length) {
                p -= items.length;
            }
            newArray[i] = items[p];
            p++;
        }
        head = 0;
        tail = s;
        items = newArray;
    }

    @Override
    public void addFirst(T item) {
        if (size() == items.length) {
            resize(items.length * 2);
        }
        head = move(head, -1);
        items[head] = item;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size() == items.length) {
            resize(items.length * 2);
        }
        items[tail] = item;
        tail = move(tail, 1);
        size++;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T t = items[head];
        items[head] = null;
        head = move(head, 1);
        size--;
        checkUsage();
        return t;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        tail = move(tail, -1);
        T t = items[tail];
        items[tail] = null;
        size--;
        checkUsage();
        return t;
    }

    private void checkUsage() {
        int minimumLength = 8;
        while ( size() / items.length < 1 / 4 && items.length > minimumLength) {
            resize(items.length / 2);
        }
    }

    @Override
    public T get(int index) {
        return items[move(index, head)];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int p = head;
        for (int i = 0; i < size(); i++) {
            if (p >= items.length) {
                p -= items.length;
            }
            System.out.print(items[p]);
            p++;
        }
    }
}
