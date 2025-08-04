package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> alnr = new AListNoResizing<Integer>();
        BuggyAList<Integer> bal = new BuggyAList<Integer>();
        for (int i = 0; i < 3; i++) {
            alnr.addLast(i);
            bal.addLast(i);
        }
        assertEquals(alnr.size(), bal.size());
        assertEquals(alnr.removeLast(), bal.removeLast());
        assertEquals(alnr.removeLast(), bal.removeLast());
        assertEquals(alnr.removeLast(), bal.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            }
        }
    }

    @Test
    public void randomizedTest3Calls() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                // System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                int sizeL = L.size();
                int sizeB = B.size();
                // System.out.println("size: " + sizeL);
                // System.out.println("size: " + sizeB);
                assertEquals(sizeL, sizeB);
            } else if (operationNumber == 2) {
                if (L.size() > 0) {
                    // System.out.println("L.getLast() returns " + L.getLast());
                    // System.out.println("B.getLast() returns " + B.getLast());
                    assertEquals(L.getLast(), B.getLast());
                }
            } else if (operationNumber == 3) {
                if (L.size() > 0) {
                    // System.out.println("removeLast() ");
                    assertEquals(L.removeLast(), B.removeLast());
                }
            }
        }
    }

}
