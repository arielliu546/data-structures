package deque;

public class ArrayDeque <T> {
    private T[] items;
    // private int size;
    public int head;
    public int tail;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        head = 0; // points to the starting valued bit
        tail = 0; // points to the bit after the first value
        // size can be deducted with head and tail.
    }

    public ArrayDeque(ArrayDeque other) {
        items = (T[]) new Object[8];
        head = 0;
        tail = 0;
        for (int i = 0; i < other.size(); i ++) {
            addLast((T)other.get(i));
        }
    }

    private int move(int p, int step){
        if (p + step > items.length) {
            return p + step - items.length;
        } else if (p + step < 0){
            return p + step + items.length;
        } else {
            return p + step;
        }
    }

    private void resize(int l) {
        T[] new_array = (T[]) new Object[l];
        int s = size();
        for (int i = 0, p = head; i < size(); i++, p++) {
            if (p >= items.length) {
                p -= items.length;
            }
            new_array[i] = items[p];
        }
        head = 0;
        tail = s;
        items = new_array;
    }

    public void addFirst(T item) {
        if (size() == items.length)
            resize(items.length * 2);
        head = move(head, -1);
        items[head] = item;
    }

    public void addLast(T item) {
        if (size() == items.length)
            resize(items.length * 2);
        items[tail] = item;
        tail = move(tail, 1);
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T t = items[head];
        items[head] = null;
        head = move(head, 1);
        checkUsage();
        return t;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        tail = move(tail, -1);
        T t = items[tail];
        items[tail] = null;
        checkUsage();
        return t;
    }

    private void checkUsage() {
        while (items.length / size() > 4 && items.length >= 16) {
            resize(items.length / 2);
        }
    }

    public T get(int index) {
       return items[move(index, head)];
    }

    public int size() {
        int m = tail - head;
        if (m >= 0) {
            return m;
        } else {
            return items.length + m;
        }
    }

    public boolean isEmpty() { return size() == 0; }

    public void printDeque() {
        for (int i = 0, p = head; i < size(); i++, p++) {
            if (p >= items.length) {
                p -= items.length;
            }
            System.out.print(items[p]);
        }
    }


    public static void main(String[] args) {

    }

}