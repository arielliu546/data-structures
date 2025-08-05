package deque;

import org.junit.Test;

import java.lang.reflect.Array;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
/** Performs some basic linked list tests. */
public class MaxArrayDequeTest {

    @Test
    public void addLastTest() {
        Comparator<Dog> c = new Dog.NameComparator();
        MaxArrayDeque<Dog> il = new MaxArrayDeque<Dog>(c);
        il.addFirst(new Dog("Andy", 5));
        il.addFirst(new Dog("Bert", 4));
        il.addFirst(new Dog("Candy", 3));
        System.out.print(il.max().name);


    }
}