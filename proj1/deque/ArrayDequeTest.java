package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
// import org.testng.annotations.Test;

import java.lang.reflect.Array;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


/** Performs some basic linked list tests. */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addLastTest() {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        d.addLast(1);
        d.addLast(2);
        d.addLast(0);
        assertEquals(3, d.size());
        // assertEquals(0, d.head);
        // assertEquals(3, d.tail);
    }

    @Test
    public void addFirstTest() {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        d.addFirst(1);
        d.addFirst(2);
        d.addFirst(3);
        assertEquals(3, d.size());
        // assertEquals(5, d.head);
        // assertEquals(0, d.tail);
        d.printDeque();
    }

    @Test
    public void removeTest() {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        d.addFirst(3);
        d.addFirst(2);
        d.addFirst(1);
        d.addLast(4);
        d.addLast(5);
        assertEquals(5, d.size());
        assertEquals(1, (int) d.removeFirst());
        assertEquals(5, (int) d.removeLast());
        d.printDeque();
    }

    @Test
    public void getTest() {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        d.addFirst(3);
        d.addFirst(2);
        d.addFirst(1);
        d.addLast(4);
        d.addLast(5);
        assertEquals(5, (int)d.get(4));
        assertEquals(3, (int)d.get(2));
    }

    @Test
    public void resizeTest() {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        int N = 512;
        for (int i = 0; i < N; i ++) {
            d.addLast(i);
        }
        d.printDeque();
        assertEquals(512, d.size());
    }

    @Test
    public void randomTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        int N = 500;
        int count = 0;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                count++;
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // remove first;
                if (!L.isEmpty()) {
                    L.removeFirst();
                    count--;
                    System.out.println("removeFirst()");
                }
            } else if (operationNumber == 2) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
                assertEquals(count, size);
            }
        }

    }

    @Test
    public void autoShrinkTest() {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        int N = 16;
        for (int i = 0; i < N; i ++) {
            d.addLast(i);
        }
        d.printDeque();
        System.out.println();
        assertEquals(16, d.size());
        int S = 15;
        for (int i = 0; i < S; i ++) {
            d.removeFirst();
        }
        d.printDeque();
        assertEquals(1, d.size());
    }

    @Test
    public void deepCopyTest() {
        ArrayDeque<Integer> d1 = new ArrayDeque<>();
        int N = 16;
        for (int i = 0; i < N; i ++) {
            d1.addLast(i);
        }
        ArrayDeque<Integer> d2 = new ArrayDeque<>(d1);
        assertEquals(16, d2.size());
        d2.removeLast();
        assertEquals(15, d2.size());
        assertEquals(16, d1.size());
    }

    @Test
    public void isEmptyTest() {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        assertTrue(d.isEmpty());
        d.addLast(1);
        assertFalse(d.isEmpty());
    }

    @Test
    public void sizeTest() {
        ArrayDeque<Integer> d = new ArrayDeque<>();
        assertTrue(d.isEmpty());
        for (int i = 0; i < 4; i ++) {
            d.addLast(1);
            assertEquals(1 + i * 2, d.size());
            d.addFirst(1);
            assertEquals(2 + i * 2, d.size());
            assertFalse(d.isEmpty());
        }
        d.removeLast();
        d.removeLast();
        assertFalse(d.isEmpty());

        for (int i = 0; i < 3; i ++) {
            d.removeLast();
            d.removeFirst();
        }
        assertTrue(d.isEmpty());
    }

    @Test
    public void AIteratorTest() {
        ArrayDeque<Integer> al = new ArrayDeque<>();
        al.addLast(1);
        al.addLast(2);
        for (int i : al) {
            System.out.println(i);
        }

    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> al = new ArrayDeque<>();
        ArrayDeque<Integer> bl = new ArrayDeque<>();
        for (int i = 0; i < 3; i++) {
            al.addLast(i);
            bl.addLast(i);
        }
        assertTrue(al.equals(bl));
        bl.addFirst(0);
        assertFalse(al.equals(bl));
    }
}