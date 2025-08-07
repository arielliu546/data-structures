package deque;
import java.util.Comparator;

public class Dog implements Comparable<Dog> {
    private String name;
    private int size;

    public Dog(String n, int s) {
        name = n;
        size = s;
    }

    @Override
    public int compareTo(Dog o) {
        return this.size - o.size;
    }

    public static class NameComparator implements Comparator<Dog> {
        @Override
        public int compare(Dog o1, Dog o2) {
            return o1.name.compareTo(o2.name);
        }
    }
}
