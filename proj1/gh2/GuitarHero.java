package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

/**
 * A client that uses the synthesizer package to replicate a plucked guitar string sound
 */
public class GuitarHero {
    public static final double CONCERT_A = 440.0;
    public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);
    static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString[] board = new GuitarString[37];
        for (int i = 0; i < 37; i++) {
            int f = (int) Math.round(440 * Math.pow(2, (double) (i - 24) / 12));
            System.out.println(f);
            board[i] = new GuitarString(f);
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int i = keyboard.indexOf(key);
                if (i < 37 && i >= 0) {
                    board[i].pluck();
                }
            }

            /* compute the superposition of samples */
            double sample = 0;
            for (int j = 0; j < 37; j++) {
                sample += board[j].sample();
            }

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for (int j = 0; j < 37; j++) {
                board[j].tic();
            }
        }
    }
}

