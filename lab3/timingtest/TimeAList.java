package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    private static class TimeData {
        int N;
        double time;
        int opCount;
        public TimeData(int n, double t, int o) {
            N = n;
            time = t;
            opCount = o;
        }
    }

    private static TimeData timeOps(int N) {
        AList<Integer> al = new AList<Integer>();
        int opCount = 0;
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < N; i ++) {
            al.addLast(1);
            opCount++;
        }
        double timeInSeconds = sw.elapsedTime();
        return new TimeData(N, timeInSeconds, opCount);
    }

    private static void processTimeData(int N, AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        TimeData data = timeOps(N);
        Ns.addLast(data.N);
        times.addLast(data.time);
        opCounts.addLast(data.opCount);
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();

        int[] list = new int[]{1000, 2000, 4000, 8000, 16000, 32000, 64000, 128000};
        for (int j : list) {
            processTimeData(j, Ns, times, opCounts);
        }

        printTimingTable(Ns, times, opCounts);
    }
}
