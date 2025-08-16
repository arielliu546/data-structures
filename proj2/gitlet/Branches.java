package gitlet;

import java.nio.file.Path;
import java.util.TreeMap;

public class Branches extends TreeMap<String, String> {
    public void put(Branch b) {
        if (containsKey(b.name)) {
            throw new GitletException("A branch with that name already exists.");
        }
        put(b.name, b.hash);
    }

    public void move(String name, String hash) {
        put(name, hash);
    }
}