package gh2;

import deque.ArrayDeque;
import deque.Deque;

public class Harp{
    private static final double DECAY = .996;
    private static final int SR = 44100;

    private Deque<Double> buffer;

    public Harp(double frequency) {
        buffer = new ArrayDeque<Double>();
        long c = Math.round(SR / frequency) * 2;
        for (int i = 0; i < c; i++) {
            buffer.addLast(0.);
        }
    }

    public void pluck() {
        int s = buffer.size();
        for (int i = 0; i < s; i++) {
            buffer.removeFirst();
            buffer.addLast(Math.random() - 0.5);
        }
    }

    public void tic() {
        double s1 = buffer.get(0);
        double s2 = buffer.get(1);
        double x = - (s1 + s2) * DECAY / 2;
        buffer.removeFirst();
        buffer.addLast(x);
    }

    public double sample() {
        // TODO: Return the correct thing.
        return buffer.get(0);
    }
}
