package gitlet;

import java.util.HashMap;

public class Branches extends HashMap<String, String> {
    public void put(Branch b) {
        put(b.name, b.hash);
    }
}