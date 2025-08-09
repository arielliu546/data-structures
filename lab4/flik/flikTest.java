package flik;

import static org.junit.Assert.*;
import org.junit.Test;

public class flikTest {
    @Test
    public void firstTest() {
        // cassertEquals(1, 1);
        assertTrue(Flik.isSameNumber(128, 128));
    }
}